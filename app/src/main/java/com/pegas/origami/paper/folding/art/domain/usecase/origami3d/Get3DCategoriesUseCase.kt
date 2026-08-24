package com.pegas.origami.paper.folding.art.domain.usecase.origami3d

import com.pegas.origami.paper.folding.art.data.model.origami.CategoryEntity
import com.pegas.origami.paper.folding.art.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DCategoriesUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(): List<CategoryEntity> =
        repository.getCategories()
}
