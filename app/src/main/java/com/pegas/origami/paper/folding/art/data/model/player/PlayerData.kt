package com.pegas.origami.paper.folding.art.data.model.player

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
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
