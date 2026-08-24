package com.pegas.origami.paper.folding.art.data.model.kirigami

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KirigamiProductEntity(
    val productId: Int,
    val image: String,
    val stepNumber: Int,
    val id: Int,
    val startTimeMs: Int = 0,
    val endTimeMs: Int = 0
)
