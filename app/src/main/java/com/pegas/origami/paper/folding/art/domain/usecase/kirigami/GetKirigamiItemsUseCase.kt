package com.pegas.origami.paper.folding.art.domain.usecase.kirigami

import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiItemEntity
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiItemsUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(categoryId: Int? = null): List<KirigamiItemEntity> =
        repository.getItems(categoryId)
}
