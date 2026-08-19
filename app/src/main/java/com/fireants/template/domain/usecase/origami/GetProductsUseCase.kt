package com.fireants.template.domain.usecase.origami

import com.fireants.template.data.model.origami.ProductEntity
import com.fireants.template.data.repository.OrigamiRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(productId: Int): List<ProductEntity> =
        repository.getProducts(productId)
}
