package com.pegas.origami.paper.folding.art.domain.usecase.origami

import com.pegas.origami.paper.folding.art.data.model.origami.ItemEntity
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(categoryId: Int? = null): List<ItemEntity> =
        repository.getItems(categoryId)
}
