package com.fireants.template.domain.usecase.origami

import com.fireants.template.data.model.origami.ItemEntity
import com.fireants.template.data.repository.OrigamiRepository
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(categoryId: Int? = null): List<ItemEntity> =
        repository.getItems(categoryId)
}
