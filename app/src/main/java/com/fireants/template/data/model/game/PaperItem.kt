package com.fireants.template.data.model.game

data class PaperItem(
    val id: Int,
    val textureRes: String? = null,
    val imagePreview: String,
    val price: Int = DEFAULT_PRICE,
    val isUnlocked: Boolean,
    val isSelected: Boolean
) {
    companion object {
        const val DEFAULT_PRICE = 500
    }
}
