package com.fireants.template.data.model.origami

data class ProductEntity(
    val productId: Int,
    val image: String,
    val stepNumber: Int,
    val id: Int,
    val videoUrl: String? = null,
    val startTimeMs: Int = 0,
    val endTimeMs: Int = 0
)
