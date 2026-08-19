package com.fireants.template.domain.usecase.origami3d

import com.fireants.template.data.model.origami3d.Item3dOrigamiEntity
import com.fireants.template.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DItemsUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(categoryId: Int? = null): List<Item3dOrigamiEntity> =
        repository.getItems(categoryId)
}
