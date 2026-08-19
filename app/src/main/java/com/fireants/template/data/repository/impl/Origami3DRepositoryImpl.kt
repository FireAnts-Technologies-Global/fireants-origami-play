package com.fireants.template.data.repository.impl

import com.fireants.template.data.local.asset.Origami3DAssetDataSource
import com.fireants.template.data.model.origami.CategoryEntity
import com.fireants.template.data.model.origami3d.Item3dOrigamiEntity
import com.fireants.template.data.model.origami3d.Product3dOrigamiEntity
import com.fireants.template.data.repository.Origami3DRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Origami3DRepositoryImpl @Inject constructor(
    private val assetDataSource: Origami3DAssetDataSource
) : Origami3DRepository {

    override suspend fun getCategories(): List<CategoryEntity> =
        assetDataSource.getCategories().sortedBy { it.order }

    override suspend fun getItems(categoryId: Int?): List<Item3dOrigamiEntity> =
        assetDataSource.getItems()
            .filter {
                categoryId == null ||
                        categoryId == ALL_CATEGORY_ID ||
                        it.categoryId == categoryId
            }
            .sortedBy { it.order }

    override suspend fun getItem(itemId: Int): Item3dOrigamiEntity? =
        assetDataSource.getItems().firstOrNull { it.id == itemId }

    override suspend fun getProducts(productId: Int): List<Product3dOrigamiEntity> =
        assetDataSource.getProducts()
            .filter { it.productId == productId }
            .sortedBy { it.stepNumber }

    companion object {
        private const val ALL_CATEGORY_ID = 0
    }
}
