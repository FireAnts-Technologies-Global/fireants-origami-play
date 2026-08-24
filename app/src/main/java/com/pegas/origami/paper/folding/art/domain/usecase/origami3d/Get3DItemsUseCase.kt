package com.pegas.origami.paper.folding.art.domain.usecase.origami3d

import com.pegas.origami.paper.folding.art.data.model.origami3d.Item3dOrigamiEntity
import com.pegas.origami.paper.folding.art.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DItemsUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(categoryId: Int? = null): List<Item3dOrigamiEntity> =
        repository.getItems(categoryId)
}
