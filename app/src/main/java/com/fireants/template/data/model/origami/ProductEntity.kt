package com.fireants.template.data.model.origami

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductEntity(
    val productId: Int,
    val image: String,
    val stepNumber: Int,
    val id: Int,
    val startTimeMs: Int = 0,
    val endTimeMs: Int = 0
)
