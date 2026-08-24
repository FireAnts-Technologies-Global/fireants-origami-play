package com.pegas.origami.paper.folding.art.data.model.game

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LevelEntity(
    val id: Int,
    val levelNumber: Int,
    val isLocked: Boolean,
    val isPremium: Boolean = false,
    val stars: Int,
    val targetPoints: List<Float>
)
