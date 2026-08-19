package com.fireants.template.domain.usecase.kirigami

import com.fireants.template.data.model.kirigami.KirigamiProductEntity
import com.fireants.template.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiProductsUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(productId: Int): List<KirigamiProductEntity> =
        repository.getProducts(productId)
}
