package com.pegas.origami.paper.folding.art.domain.usecase.origami

import com.pegas.origami.paper.folding.art.data.model.origami.ProductEntity
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(productId: Int): List<ProductEntity> =
        repository.getProducts(productId)
}
