package com.pegas.origami.paper.folding.art.data.repository

import com.pegas.origami.paper.folding.art.data.model.product.ProductItem

interface FavoriteRepository {
    suspend fun getFavoriteIds(): Set<Int>
    suspend fun getFavorites(): List<ProductItem>
    suspend fun isFavorite(id: Int): Boolean
    suspend fun setFavorite(item: ProductItem, isFavorite: Boolean)
    suspend fun toggleFavorite(item: ProductItem): Boolean
    suspend fun toggleFavorite(
        id: Int,
        sourceId: Int,
        gameType: String,
        name: String,
        image: String,
        difficulty: String,
        stepCount: Int,
        estimatedTime: String
    ): Boolean
}
