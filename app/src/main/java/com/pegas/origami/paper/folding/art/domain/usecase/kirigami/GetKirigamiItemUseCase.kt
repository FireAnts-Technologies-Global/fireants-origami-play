package com.pegas.origami.paper.folding.art.domain.usecase.kirigami

import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiItemEntity
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiItemUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(itemId: Int): KirigamiItemEntity =
        requireNotNull(repository.getItem(itemId)) {
            "Item $itemId was not found"
        }
}
