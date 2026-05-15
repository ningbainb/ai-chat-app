package com.aichat.app.data.remote

import com.aichat.app.data.model.VisionRequest
import com.aichat.app.data.model.VisionResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {
    @Headers("Content-Type: application/json")
    @POST("/v1/chat/completions")
    suspend fun chat(@Body request: VisionRequest): VisionResponse
}
