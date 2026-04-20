package com.example.flat_rent_app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SlotItemDto(
    val slot: Int,
    val urls: PhotoUrlsDto?
)
