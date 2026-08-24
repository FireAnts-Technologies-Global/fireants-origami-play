package com.pegas.origami.paper.folding.art.data.repository

import com.pegas.origami.paper.folding.art.data.model.product.BaseResponse
import com.pegas.origami.paper.folding.art.data.model.product.ProductItem

interface ProductRepository {
    suspend fun getBanners(): BaseResponse<List<ProductItem>>
    suspend fun getRecommendations(): BaseResponse<List<ProductItem>>
    suspend fun getHotItems(): BaseResponse<List<ProductItem>>
    suspend fun getItemsByType(gameType: String): BaseResponse<List<ProductItem>>
}
