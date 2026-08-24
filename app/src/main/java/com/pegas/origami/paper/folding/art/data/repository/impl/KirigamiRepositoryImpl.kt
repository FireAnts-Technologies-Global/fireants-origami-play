package com.pegas.origami.paper.folding.art.data.repository.impl

import com.pegas.origami.paper.folding.art.data.local.asset.KirigamiAssetDataSource
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiCategoryEntity
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiItemEntity
import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiProductEntity
import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
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
