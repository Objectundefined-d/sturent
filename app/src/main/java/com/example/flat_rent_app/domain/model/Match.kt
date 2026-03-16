package com.example.flat_rent_app.domain.model

data class Match(
    val matchId: String = "",
    val uids: List<String> = emptyList(),
    val updatedAtMillis: Long? = null
) {
    fun otherUid(myUid: String): String? = uids.firstOrNull { it != myUid }
}
