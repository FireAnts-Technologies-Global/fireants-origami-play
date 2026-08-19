package com.fireants.template.data.model.game

data class LevelEntity(
    val id: Int,
    val levelNumber: Int,
    val isLocked: Boolean,
    val stars: Int,
    val targetPoints: List<Float>
)
