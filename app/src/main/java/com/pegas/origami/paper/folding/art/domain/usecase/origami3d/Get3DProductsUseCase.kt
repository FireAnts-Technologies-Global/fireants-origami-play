package com.pegas.origami.paper.folding.art.domain.usecase.origami3d

import com.pegas.origami.paper.folding.art.data.model.origami3d.Product3dOrigamiEntity
import com.pegas.origami.paper.folding.art.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DProductsUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(productId: Int): List<Product3dOrigamiEntity> =
        repository.getProducts(productId)
}
