package com.fireants.template.data.model.game

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LevelEntity(
    val id: Int,
    val levelNumber: Int,
    val isLocked: Boolean,
    val stars: Int,
    val targetPoints: List<Float>
)
