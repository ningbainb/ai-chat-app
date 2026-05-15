package com.aichat.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestionPayload(val suggestions: List<ChatSuggestion> = emptyList())

@Serializable
data class ChatSuggestion(val style: String, val text: String)

@Serializable
data class VisionRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("response_format") val responseFormat: Map<String, String> = mapOf("type" to "json_object")
)

@Serializable
data class Message(val role: String, val content: List<ContentPart>)

@Serializable
data class ContentPart(
    val type: String,
    val text: String? = null,
    @SerialName("image_url") val imageUrl: ImageUrl? = null
)

@Serializable
data class ImageUrl(val url: String)

@Serializable
data class VisionResponse(val choices: List<Choice> = emptyList())

@Serializable
data class Choice(val message: ChoiceMessage)

@Serializable
data class ChoiceMessage(val content: String)
