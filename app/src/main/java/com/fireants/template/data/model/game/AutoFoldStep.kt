package com.fireants.template.data.model.game

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutoFoldStep(
    val startXRatio: Float,
    val startYRatio: Float,
    val endXRatio: Float,
    val endYRatio: Float
)
