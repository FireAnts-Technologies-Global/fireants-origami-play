package com.fireants.template.data.remote

import com.fireants.template.data.model.product.BaseResponse
import com.fireants.template.data.model.product.ProductItem
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApiService {
    @GET("home/banner")
    suspend fun getBanners(): BaseResponse<List<ProductItem>>

    @GET("home/recommend")
    suspend fun getRecommendations(): BaseResponse<List<ProductItem>>

    @GET("home/hot")
    suspend fun getHotItems(): BaseResponse<List<ProductItem>>

    @GET("items/{type}")
    suspend fun getItemsByType(
        @Path("type") gameType: String
    ): BaseResponse<List<ProductItem>>
}
