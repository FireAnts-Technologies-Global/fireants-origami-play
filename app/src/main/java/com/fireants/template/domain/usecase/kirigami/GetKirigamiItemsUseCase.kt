package com.fireants.template.domain.usecase.kirigami

import com.fireants.template.data.model.kirigami.KirigamiItemEntity
import com.fireants.template.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiItemsUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(categoryId: Int? = null): List<KirigamiItemEntity> =
        repository.getItems(categoryId)
}
