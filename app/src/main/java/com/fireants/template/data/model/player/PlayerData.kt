package com.fireants.template.data.model.player

data class PlayerData(
    val coins: Int,
    val stars: Int,
    val hints: Int,
    val tickets: Int,
    val selectedPaperId: Int,
    val unlockedPaperIds: Set<Int>,
    val lastClaimBag: Long,
    val lastClaimTicket: Long,
    val bannerDate: String,
    val bannerItems: String
)
