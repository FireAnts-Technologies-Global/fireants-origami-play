package com.fireants.template.domain.usecase.kirigami

import com.fireants.template.data.model.translation.CategoryTranslation
import com.fireants.template.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiCategoryTranslationsUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(): List<CategoryTranslation> =
        repository.getCategoryTranslations()
}
