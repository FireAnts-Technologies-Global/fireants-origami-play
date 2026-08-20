package com.fireants.template.domain.usecase.product

import com.fireants.template.data.repository.ProductRepository
import com.fireants.template.domain.model.product.ProductData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

import javax.inject.Inject

class GetProductDataUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): Result<ProductData> = coroutineScope {
        try {
            val bannerDeferred = async { productRepository.getBanners() }
            val recommendDeferred = async { productRepository.getRecommendations() }
            val hotDeferred = async { productRepository.getHotItems() }

            val bannerResponse = bannerDeferred.await()
            val recommendResponse = recommendDeferred.await()
            val hotResponse = hotDeferred.await()

            val productData = ProductData(
                banners = bannerResponse.data ?: emptyList(),
                recommendations = recommendResponse.data ?: emptyList(),
                hotItems = hotResponse.data ?: emptyList()
            )

            Result.success(productData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
