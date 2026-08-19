package com.fireants.template.data.model.origami

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
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
