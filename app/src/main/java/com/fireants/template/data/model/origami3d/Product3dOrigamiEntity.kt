package com.fireants.template.data.model.origami3d

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product3dOrigamiEntity(
    val productId: Int,
    val image: String,
    val stepNumber: Int,
    val id: Int,
    val startTimeMs: Int = 0,
    val endTimeMs: Int = 0
)
