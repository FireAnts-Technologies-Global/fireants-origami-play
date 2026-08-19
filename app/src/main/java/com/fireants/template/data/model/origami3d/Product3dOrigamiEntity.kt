package com.fireants.template.data.model.origami3d

data class Product3dOrigamiEntity(
    val productId: Int,
    val image: String,
    val stepNumber: Int,
    val id: Int,
    val videoUrl: String? = null,
    val startTimeMs: Int = 0,
    val endTimeMs: Int = 0
)
