package com.fireants.template.data.repository

import com.fireants.template.data.model.origami.CategoryEntity
import com.fireants.template.data.model.origami.ItemEntity
import com.fireants.template.data.model.origami.ProductEntity
import com.fireants.template.data.model.translation.CategoryTranslation
import com.fireants.template.data.model.translation.ItemTranslation

interface OrigamiRepository {
    suspend fun getCategories(): List<CategoryEntity>
    suspend fun getItems(categoryId: Int? = null): List<ItemEntity>
    suspend fun getItem(itemId: Int): ItemEntity?
    suspend fun getProducts(productId: Int): List<ProductEntity>
    suspend fun getCategoryTranslations(): List<CategoryTranslation>
    suspend fun getItemTranslations(): List<ItemTranslation>
}
