package com.fireants.template.data.repository

import com.fireants.template.data.model.product.BaseResponse
import com.fireants.template.data.model.product.ProductItem

interface ProductRepository {
    suspend fun getBanners(): BaseResponse<List<ProductItem>>
    suspend fun getRecommendations(): BaseResponse<List<ProductItem>>
    suspend fun getHotItems(): BaseResponse<List<ProductItem>>
    suspend fun getItemsByType(gameType: String): BaseResponse<List<ProductItem>>
}
