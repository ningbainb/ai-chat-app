package com.aichat.app.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.aichat.app.data.local.ChatDao
import com.aichat.app.data.local.ChatRecord
import com.aichat.app.data.model.ContentPart
import com.aichat.app.data.model.ImageUrl
import com.aichat.app.data.model.Message
import com.aichat.app.data.model.SuggestionPayload
import com.aichat.app.data.model.VisionRequest
import com.aichat.app.data.remote.OpenAiApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ChatRepository(private val context: Context, private val dao: ChatDao) {
    private val json = Json { ignoreUnknownKeys = true }

    fun observeHistory(): Flow<List<ChatRecord>> = dao.observeAll()

    suspend fun generate(imageDataUrl: String, style: String, goal: String): SuggestionPayload {
        val prefs = securePrefs(context)
        val baseUrl = prefs.getString("base_url", "https://api.openai.com")!!.trimEnd('/')
        val apiKey = prefs.getString("api_key", "")!!
        val model = prefs.getString("model", "gpt-4o-mini")!!

        require(apiKey.isNotBlank()) { "请先在设置中填写 API Key" }

        val api = buildApi(baseUrl, apiKey)
        val prompt = """
请根据截图生成 JSON：{"suggestions":[{"style":"自然","text":"..."}]}。
要求：
1) 共 5 条
2) 每条 <= 40 字
3) 风格=$style
4) 用户目标=$goal
5) 禁止骚扰/PUA/威胁
仅输出 JSON。
""".trimIndent()

        val resp = api.chat(
            VisionRequest(
                model = model,
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            ContentPart(type = "text", text = prompt),
                            ContentPart(type = "image_url", imageUrl = ImageUrl(imageDataUrl))
                        )
                    )
                )
            )
        )

        val content = resp.choices.firstOrNull()?.message?.content ?: "{\"suggestions\":[]}"
        return runCatching { json.decodeFromString<SuggestionPayload>(content) }
            .getOrElse { SuggestionPayload(emptyList()) }
    }

    suspend fun saveRecord(imageUri: String, goal: String, style: String, suggestionsJson: String) {
        dao.insert(ChatRecord(imageUri = imageUri, goal = goal, style = style, suggestionsJson = suggestionsJson))
    }

    fun saveSettings(baseUrl: String, apiKey: String, model: String) {
        securePrefs(context).edit().putString("base_url", baseUrl).putString("api_key", apiKey).putString("model", model).apply()
    }

    fun readSettings(): Triple<String, String, String> {
        val p = securePrefs(context)
        return Triple(
            p.getString("base_url", "https://api.openai.com")!!,
            p.getString("api_key", "")!!,
            p.getString("model", "gpt-4o-mini")!!
        )
    }

    private fun buildApi(baseUrl: String, apiKey: String): OpenAiApi {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder().addHeader("Authorization", "Bearer $apiKey").build()
                chain.proceed(req)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(OpenAiApi::class.java)
    }

    private fun securePrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
