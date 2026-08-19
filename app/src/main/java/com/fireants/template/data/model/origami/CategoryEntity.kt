package com.fireants.template.data.model.origami

data class CategoryEntity(
    val id: Int,
    val name: String,
    val image: String,
    val order: Int = 0
)
