package com.pegas.origami.paper.folding.art.domain.usecase.product

import com.pegas.origami.paper.folding.art.data.model.product.ProductItem
import com.pegas.origami.paper.folding.art.data.repository.ProductRepository
import com.pegas.origami.paper.folding.art.domain.model.product.HomeProductSection
import com.pegas.origami.paper.folding.art.domain.usecase.favorite.GetFavoriteProductsUseCase
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
