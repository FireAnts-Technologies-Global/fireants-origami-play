package com.pegas.origami.paper.folding.art.data.local.asset

import com.pegas.origami.paper.folding.art.data.model.origami.CategoryEntity
import com.pegas.origami.paper.folding.art.data.model.origami.ItemEntity
import com.pegas.origami.paper.folding.art.data.model.origami.ProductEntity
import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import com.pegas.origami.paper.folding.art.data.model.translation.ItemTranslation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrigamiAssetDataSource @Inject constructor(
    private val reader: AssetJsonReader
) {
    suspend fun getCategories(): List<CategoryEntity> =
        reader.read("origami/categories.json")

    suspend fun getItems(): List<ItemEntity> =
        reader.read("origami/items.json")

    suspend fun getProducts(): List<ProductEntity> =
        reader.read("origami/products.json")

    suspend fun getCategoryTranslations(): List<CategoryTranslation> =
        reader.read("origami/translations/categories.json")

    suspend fun getItemTranslations(): List<ItemTranslation> =
        reader.read("origami/translations/items.json")
}
