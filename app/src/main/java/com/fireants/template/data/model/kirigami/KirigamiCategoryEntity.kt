package com.fireants.template.data.model.kirigami

data class KirigamiCategoryEntity(
    val id: Int,
    val name: String,
    val image: String,
    val order: Int = 0
)
