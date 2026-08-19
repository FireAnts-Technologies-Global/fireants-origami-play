package com.fireants.template.domain.usecase.origami3d

import com.fireants.template.data.model.origami.CategoryEntity
import com.fireants.template.data.repository.Origami3DRepository
import javax.inject.Inject

class Get3DCategoriesUseCase @Inject constructor(
    private val repository: Origami3DRepository
) {
    suspend operator fun invoke(): List<CategoryEntity> =
        repository.getCategories()
}
