package com.pegas.origami.paper.folding.art.data.local.asset

import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiCategoryEntity
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiItemEntity
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiProductEntity
import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KirigamiAssetDataSource @Inject constructor(
    private val reader: AssetJsonReader
) {
    suspend fun getCategories(): List<KirigamiCategoryEntity> =
        reader.read("kirigami/categories.json")

    suspend fun getItems(): List<KirigamiItemEntity> =
        reader.read("kirigami/items.json")

    suspend fun getProducts(): List<KirigamiProductEntity> =
        reader.read("kirigami/products.json")

    suspend fun getCategoryTranslations(): List<CategoryTranslation> =
        reader.read("kirigami/translations/categories.json")
}
