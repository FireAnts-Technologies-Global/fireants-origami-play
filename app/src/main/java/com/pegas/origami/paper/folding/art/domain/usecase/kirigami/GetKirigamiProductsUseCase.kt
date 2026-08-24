package com.pegas.origami.paper.folding.art.domain.usecase.kirigami

import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiProductEntity
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiProductsUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(productId: Int): List<KirigamiProductEntity> =
        repository.getProducts(productId)
}
