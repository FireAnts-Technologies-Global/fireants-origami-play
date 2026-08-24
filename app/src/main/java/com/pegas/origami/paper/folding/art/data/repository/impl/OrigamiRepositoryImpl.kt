package com.pegas.origami.paper.folding.art.data.repository.impl

import com.pegas.origami.paper.folding.art.data.local.asset.OrigamiAssetDataSource
import com.pegas.origami.paper.folding.art.data.model.origami.CategoryEntity
import com.pegas.origami.paper.folding.art.data.model.origami.ItemEntity
import com.pegas.origami.paper.folding.art.data.model.origami.ProductEntity
import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import com.pegas.origami.paper.folding.art.data.model.translation.ItemTranslation
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrigamiRepositoryImpl @Inject constructor(
    private val assetDataSource: OrigamiAssetDataSource
) : OrigamiRepository {

    override suspend fun getCategories(): List<CategoryEntity> =
        assetDataSource.getCategories().sortedBy { it.order }

    override suspend fun getItems(categoryId: Int?): List<ItemEntity> =
        assetDataSource.getItems()
            .filter {
                categoryId == null ||
                        categoryId == ALL_CATEGORY_ID ||
                        it.categoryId == categoryId
            }
            .sortedBy { it.order }

    override suspend fun getItem(itemId: Int): ItemEntity? =
        assetDataSource.getItems().firstOrNull { it.id == itemId }

    override suspend fun getProducts(productId: Int): List<ProductEntity> =
        assetDataSource.getProducts()
            .filter { it.productId == productId }
            .sortedBy { it.stepNumber }

    override suspend fun getCategoryTranslations(): List<CategoryTranslation> =
        assetDataSource.getCategoryTranslations()

    override suspend fun getItemTranslations(): List<ItemTranslation> =
        assetDataSource.getItemTranslations()

    companion object {
        private const val ALL_CATEGORY_ID = 0
    }
}
