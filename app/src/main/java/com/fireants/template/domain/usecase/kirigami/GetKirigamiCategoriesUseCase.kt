package com.fireants.template.domain.usecase.kirigami

import com.fireants.template.data.model.kirigami.KirigamiCategoryEntity
import com.fireants.template.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiCategoriesUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(): List<KirigamiCategoryEntity> =
        repository.getCategories()
}
