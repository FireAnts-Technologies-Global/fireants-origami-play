package com.fireants.template.domain.usecase.origami

import com.fireants.template.data.model.origami.CategoryEntity
import com.fireants.template.data.repository.OrigamiRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(): List<CategoryEntity> =
        repository.getCategories()
}
