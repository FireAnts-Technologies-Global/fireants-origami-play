package com.pegas.origami.paper.folding.art.data.repository

import com.pegas.origami.paper.folding.art.data.model.origami.CategoryEntity
import com.pegas.origami.paper.folding.art.data.model.origami.ItemEntity
import com.pegas.origami.paper.folding.art.data.model.origami.ProductEntity
import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import com.pegas.origami.paper.folding.art.data.model.translation.ItemTranslation

interface OrigamiRepository {
    suspend fun getCategories(): List<CategoryEntity>
    suspend fun getItems(categoryId: Int? = null): List<ItemEntity>
    suspend fun getItem(itemId: Int): ItemEntity?
    suspend fun getProducts(productId: Int): List<ProductEntity>
    suspend fun getCategoryTranslations(): List<CategoryTranslation>
    suspend fun getItemTranslations(): List<ItemTranslation>
}
