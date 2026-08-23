package com.fireants.template.data.repository.impl

import com.fireants.template.data.local.asset.ProductAssetDataSource
import com.fireants.template.data.model.product.BaseResponse
import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.data.repository.FavoriteRepository
import com.fireants.template.data.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAssetDataSource: ProductAssetDataSource,
    private val favoriteRepository: FavoriteRepository
) : ProductRepository {
    
    override suspend fun getBanners(): BaseResponse<List<ProductItem>> {
        return BaseResponse(data = getRandomItems(BANNER_LIMIT))
    }

    override suspend fun getRecommendations(): BaseResponse<List<ProductItem>> {
        return BaseResponse(data = getRandomItems(RECOMMENDATION_LIMIT))
    }

    override suspend fun getHotItems(): BaseResponse<List<ProductItem>> {
        return BaseResponse(data = getRandomItems(HOT_LIMIT))
    }

    override suspend fun getItemsByType(gameType: String): BaseResponse<List<ProductItem>> {
        val type = GameType.entries.firstOrNull {
            it.name.equals(gameType, ignoreCase = true) ||
                    it.folderName.equals(gameType, ignoreCase = true)
        }

        val items = productAssetDataSource.getItems()
            .filter { type == null || it.gameType == type }
            .withFavoriteState()
            .shuffled()

        return BaseResponse(data = items)
    }

    private suspend fun getRandomItems(limit: Int): List<ProductItem> {
        return productAssetDataSource.getItems()
            .withFavoriteState()
            .shuffled()
            .take(limit)
    }

    private suspend fun List<ProductItem>.withFavoriteState(): List<ProductItem> {
        val favoriteIds = favoriteRepository.getFavoriteIds()
        return map { item ->
            item.copy(isFavorite = favoriteIds.contains(item.id))
        }
    }

    companion object {
        private const val BANNER_LIMIT = 5
        private const val RECOMMENDATION_LIMIT = 10
        private const val HOT_LIMIT = 10
    }
}
