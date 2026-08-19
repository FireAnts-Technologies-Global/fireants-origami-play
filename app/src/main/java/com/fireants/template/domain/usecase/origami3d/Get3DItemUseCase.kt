package com.fireants.template.domain.usecase.origami3d

import com.fireants.template.data.model.origami3d.Item3dOrigamiEntity
import com.fireants.template.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DItemUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(itemId: Int): Item3dOrigamiEntity =
        requireNotNull(repository.getItem(itemId)) {
            "Item $itemId was not found"
        }
}
