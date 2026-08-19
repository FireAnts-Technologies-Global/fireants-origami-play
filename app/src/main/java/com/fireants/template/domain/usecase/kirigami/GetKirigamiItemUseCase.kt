package com.fireants.template.domain.usecase.kirigami

import com.fireants.template.data.model.kirigami.KirigamiItemEntity
import com.fireants.template.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiItemUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(itemId: Int): KirigamiItemEntity =
        requireNotNull(repository.getItem(itemId)) {
            "Item $itemId was not found"
        }
}
