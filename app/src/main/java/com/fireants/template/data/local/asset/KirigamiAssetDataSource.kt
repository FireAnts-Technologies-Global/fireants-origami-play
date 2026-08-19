package com.fireants.template.data.local.asset

import com.fireants.template.data.model.kirigami.KirigamiCategoryEntity
import com.fireants.template.data.model.kirigami.KirigamiItemEntity
import com.fireants.template.data.model.kirigami.KirigamiProductEntity
import com.fireants.template.data.model.translation.CategoryTranslation
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
