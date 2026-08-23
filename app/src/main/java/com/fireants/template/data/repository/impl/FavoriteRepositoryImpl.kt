package com.fireants.template.data.repository.impl

import com.fireants.template.data.local.db.favorite.FavoriteDao
import com.fireants.template.data.local.db.favorite.FavoriteEntity
import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.data.repository.FavoriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override suspend fun getFavoriteIds(): Set<Int> {
        return favoriteDao.getFavoriteIds().toSet()
    }

    override suspend fun getFavorites(): List<ProductItem> {
        return favoriteDao.getFavorites().map { it.toProductItem() }
    }

    override suspend fun isFavorite(id: Int): Boolean {
        return favoriteDao.isFavorite(id)
    }

    override suspend fun setFavorite(item: ProductItem, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.insert(item.toFavoriteEntity())
        } else {
            favoriteDao.deleteById(item.id)
        }
    }

    override suspend fun toggleFavorite(item: ProductItem): Boolean {
        return toggleFavorite(
            id = item.id,
            sourceId = item.sourceId,
            gameType = item.gameType.name,
            name = item.name,
            image = item.image,
            difficulty = item.difficulty,
            stepCount = item.stepCount,
            estimatedTime = item.estimatedTime
        )
    }

    override suspend fun toggleFavorite(
        id: Int,
        sourceId: Int,
        gameType: String,
        name: String,
        image: String,
        difficulty: String,
        stepCount: Int,
        estimatedTime: String
    ): Boolean {
        val isCurrentlyFavorite = favoriteDao.isFavorite(id)
        return if (isCurrentlyFavorite) {
            favoriteDao.deleteById(id)
            false
        } else {
            favoriteDao.insert(
                FavoriteEntity(
                    id = id,
                    sourceId = sourceId,
                    gameType = gameType,
                    name = name,
                    image = image,
                    difficulty = difficulty,
                    stepCount = stepCount,
                    estimatedTime = estimatedTime
                )
            )
            true
        }
    }

    private fun ProductItem.toFavoriteEntity(): FavoriteEntity {
        return FavoriteEntity(
            id = id,
            sourceId = sourceId,
            gameType = gameType.name,
            name = name,
            image = image,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }

    private fun FavoriteEntity.toProductItem(): ProductItem {
        return ProductItem(
            id = id,
            sourceId = sourceId,
            categoryId = 0,
            name = name,
            image = image,
            order = 0,
            isFavorite = true,
            gameType = GameType.entries.firstOrNull { it.name == gameType } ?: GameType.ORIGAMI,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }
}
