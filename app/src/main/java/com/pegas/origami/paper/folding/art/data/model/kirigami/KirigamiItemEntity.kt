package com.pegas.origami.paper.folding.art.data.model.kirigami

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KirigamiItemEntity(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val image: String,
    val isFavorite: Boolean = false,
    val hasVideo: Boolean = false,
    val isPremium: Boolean = false,
    val order: Int = 0,
    val difficulty: String = "",
    val stepCount: Int = 0,
    val estimatedTime: String = ""
)
