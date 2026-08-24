package com.pegas.origami.paper.folding.art.domain.usecase.origami

import com.pegas.origami.paper.folding.art.data.model.origami.ItemEntity
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import javax.inject.Inject

class GetItemUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(itemId: Int): ItemEntity =
        requireNotNull(repository.getItem(itemId)) {
            "Item $itemId was not found"
        }
}
