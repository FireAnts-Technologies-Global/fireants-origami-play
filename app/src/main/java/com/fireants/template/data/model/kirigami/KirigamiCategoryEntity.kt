package com.fireants.template.data.model.kirigami

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KirigamiCategoryEntity(
    val id: Int,
    val name: String,
    val image: String,
    val order: Int = 0
)
