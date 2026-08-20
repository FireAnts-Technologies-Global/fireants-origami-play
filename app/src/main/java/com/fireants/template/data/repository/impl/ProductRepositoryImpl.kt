package com.fireants.template.data.repository.impl

import com.fireants.template.data.model.product.BaseResponse
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.data.remote.ProductApiService
import com.fireants.template.data.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApiService: ProductApiService
) : ProductRepository {
    
    override suspend fun getBanners(): BaseResponse<List<ProductItem>> {
        return productApiService.getBanners()
    }

    override suspend fun getRecommendations(): BaseResponse<List<ProductItem>> {
        return productApiService.getRecommendations()
    }

    override suspend fun getHotItems(): BaseResponse<List<ProductItem>> {
        return productApiService.getHotItems()
    }

    override suspend fun getItemsByType(gameType: String): BaseResponse<List<ProductItem>> {
        return productApiService.getItemsByType(gameType)
    }
}
