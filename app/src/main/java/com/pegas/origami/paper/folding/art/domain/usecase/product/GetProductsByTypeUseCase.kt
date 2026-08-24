package com.pegas.origami.paper.folding.art.domain.usecase.product

import com.pegas.origami.paper.folding.art.data.model.product.GameType
import com.pegas.origami.paper.folding.art.data.model.product.ProductItem
import com.pegas.origami.paper.folding.art.data.repository.ProductRepository
import javax.inject.Inject

class GetProductsByTypeUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(gameType: GameType): List<ProductItem> {
        return productRepository.getItemsByType(gameType.name).data.orEmpty()
    }
}
