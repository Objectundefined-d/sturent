package com.example.flat_rent_app.util

sealed class LikeOutCome {
    data object LikedOnly: LikeOutCome()
    data class Match(val chatId: String): LikeOutCome()
}
