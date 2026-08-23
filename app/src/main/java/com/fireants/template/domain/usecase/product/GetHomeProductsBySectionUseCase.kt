package com.fireants.template.domain.usecase.product

import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.data.repository.ProductRepository
import com.fireants.template.domain.model.product.HomeProductSection
import com.fireants.template.domain.usecase.favorite.GetFavoriteProductsUseCase
import javax.inject.Inject

class GetHomeProductsBySectionUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase
) {
    suspend operator fun invoke(section: HomeProductSection): List<ProductItem> {
        return when (section) {
            HomeProductSection.RECOMMENDED -> productRepository.getRecommendations().data.orEmpty()
            HomeProductSection.HOT -> productRepository.getHotItems().data.orEmpty()
            HomeProductSection.FAVORITES -> getFavoriteProductsUseCase()
        }
    }
}
