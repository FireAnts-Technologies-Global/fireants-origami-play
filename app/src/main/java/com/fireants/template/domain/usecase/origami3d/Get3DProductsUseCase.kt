package com.fireants.template.domain.usecase.origami3d

import com.fireants.template.data.model.origami3d.Product3dOrigamiEntity
import com.fireants.template.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DProductsUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(productId: Int): List<Product3dOrigamiEntity> =
        repository.getProducts(productId)
}
