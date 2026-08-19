package com.fireants.template.data.model.origami

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryEntity(
    val id: Int,
    val name: String,
    val image: String,
    val order: Int = 0
)
