package com.example.flat_rent_app.domain.model

data class Message(
    val messageId: String = "",
    val senderUid: String = "",
    val text: String = "",
    val type: String = "text",
    val createdAt: Long = 0L,
    val status: MessageStatus = MessageStatus.SENDING
)

enum class MessageStatus {
    SENDING,
    SENT,
    READ
}
