package com.fireants.template.data.repository.impl

import com.fireants.template.data.local.asset.KirigamiAssetDataSource
import com.fireants.template.data.model.kirigami.KirigamiCategoryEntity
import com.fireants.template.data.model.kirigami.KirigamiItemEntity
import com.fireants.template.data.model.kirigami.KirigamiProductEntity
import com.fireants.template.data.model.translation.CategoryTranslation
import com.fireants.template.data.repository.KirigamiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KirigamiRepositoryImpl @Inject constructor(
    private val assetDataSource: KirigamiAssetDataSource
) : KirigamiRepository {

    override suspend fun getCategories(): List<KirigamiCategoryEntity> =
        assetDataSource.getCategories().sortedBy { it.order }

    override suspend fun getItems(categoryId: Int?): List<KirigamiItemEntity> =
        assetDataSource.getItems()
            .filter {
                categoryId == null ||
                        categoryId == ALL_CATEGORY_ID ||
                        it.categoryId == categoryId
            }
            .sortedBy { it.order }

    override suspend fun getItem(itemId: Int): KirigamiItemEntity? =
        assetDataSource.getItems().firstOrNull { it.id == itemId }

    override suspend fun getProducts(productId: Int): List<KirigamiProductEntity> =
        assetDataSource.getProducts()
            .filter { it.productId == productId }
            .sortedBy { it.stepNumber }

    override suspend fun getCategoryTranslations(): List<CategoryTranslation> =
        assetDataSource.getCategoryTranslations()

    companion object {
        private const val ALL_CATEGORY_ID = 0
    }
}
