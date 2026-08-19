package com.fireants.template.domain.usecase.origami

import com.fireants.template.data.model.origami.ItemEntity
import com.fireants.template.data.repository.OrigamiRepository
import javax.inject.Inject

class GetItemUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(itemId: Int): ItemEntity =
        requireNotNull(repository.getItem(itemId)) {
            "Item $itemId was not found"
        }
}
