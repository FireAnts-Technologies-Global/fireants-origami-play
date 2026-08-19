package com.fireants.template.data.repository

import com.fireants.template.data.model.kirigami.KirigamiCategoryEntity
import com.fireants.template.data.model.kirigami.KirigamiItemEntity
import com.fireants.template.data.model.kirigami.KirigamiProductEntity
import com.fireants.template.data.model.translation.CategoryTranslation

interface KirigamiRepository {
    suspend fun getCategories(): List<KirigamiCategoryEntity>
    suspend fun getItems(categoryId: Int? = null): List<KirigamiItemEntity>
    suspend fun getItem(itemId: Int): KirigamiItemEntity?
    suspend fun getProducts(productId: Int): List<KirigamiProductEntity>
    suspend fun getCategoryTranslations(): List<CategoryTranslation>
}
