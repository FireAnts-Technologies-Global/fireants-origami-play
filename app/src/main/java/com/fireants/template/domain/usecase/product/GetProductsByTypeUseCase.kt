package com.fireants.template.domain.usecase.product

import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.data.repository.ProductRepository
import javax.inject.Inject

class GetProductsByTypeUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(gameType: GameType): List<ProductItem> {
        return productRepository.getItemsByType(gameType.name).data.orEmpty()
    }
}
