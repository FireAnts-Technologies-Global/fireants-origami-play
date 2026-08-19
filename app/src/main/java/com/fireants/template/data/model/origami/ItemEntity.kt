package com.fireants.template.data.model.origami

data class ItemEntity(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val image: String,
    val isFavorite: Boolean = false,
    val hasVideo: Boolean = false,
    val showReward: Boolean = false,
    val order: Int = 0
)
