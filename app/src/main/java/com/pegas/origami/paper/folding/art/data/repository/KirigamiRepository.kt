package com.pegas.origami.paper.folding.art.data.repository

import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiCategoryEntity
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiItemEntity
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiProductEntity
import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation

interface KirigamiRepository {
    suspend fun getCategories(): List<KirigamiCategoryEntity>
    suspend fun getItems(categoryId: Int? = null): List<KirigamiItemEntity>
    suspend fun getItem(itemId: Int): KirigamiItemEntity?
    suspend fun getProducts(productId: Int): List<KirigamiProductEntity>
    suspend fun getCategoryTranslations(): List<CategoryTranslation>
}
