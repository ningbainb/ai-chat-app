package com.aichat.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aichat.app.data.ChatRepository
import com.aichat.app.data.local.AppDatabase
import com.aichat.app.data.model.ChatSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ChatRepository(app, AppDatabase.create(app).chatDao())
    private val json = Json { ignoreUnknownKeys = true }

    val history = repo.observeHistory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val suggestions = MutableStateFlow<List<ChatSuggestion>>(emptyList())
    val error = MutableStateFlow<String?>(null)
    val loading = MutableStateFlow(false)

    fun generate(uri: Uri?, goal: String, style: String) {
        if (uri == null) {
            error.value = "请先选择截图"
            return
        }
        if (goal.isBlank()) {
            error.value = "请填写目标"
            return
        }
        if (style.isBlank()) {
            error.value = "请填写风格"
            return
        }
        viewModelScope.launch {
            loading.value = true
            error.value = null
            runCatching {
                val dataUrl = toDataUrl(uri)
                val payload = repo.generate(dataUrl, style, goal)
                suggestions.value = payload.suggestions
                repo.saveRecord(uri.toString(), goal, style, json.encodeToString(payload))
            }.onFailure {
                suggestions.value = emptyList()
                error.value = it.message ?: "请求失败"
            }
            loading.value = false
        }
    }

    fun saveSettings(baseUrl: String, apiKey: String, model: String): String? {
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            return "Base URL 必须以 http:// 或 https:// 开头"
        }
        if (apiKey.isBlank()) return "API Key 不能为空"
        if (model.isBlank()) return "Model 不能为空"
        repo.saveSettings(baseUrl, apiKey, model)
        return null
    }
    fun readSettings(): Triple<String, String, String> = repo.readSettings()

    private fun toDataUrl(uri: Uri): String {
        val input: InputStream = getApplication<Application>().contentResolver.openInputStream(uri)!!
        val bytes = input.use { it.readBytes() }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val compressed = compressBitmap(bitmap)
        val b64 = Base64.getEncoder().encodeToString(compressed)
        return "data:image/jpeg;base64,$b64"
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        val maxWidth = 1080
        val ratio = maxWidth.toFloat() / bitmap.width.toFloat()
        val resized = if (bitmap.width > maxWidth) {
            Bitmap.createScaledBitmap(bitmap, maxWidth, (bitmap.height * ratio).toInt(), true)
        } else {
            bitmap
        }
        val out = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 80, out)
        return out.toByteArray()
    }

    fun parseSuggestions(jsonText: String): List<ChatSuggestion> =
        runCatching { json.decodeFromString<com.aichat.app.data.model.SuggestionPayload>(jsonText).suggestions }.getOrDefault(emptyList())
