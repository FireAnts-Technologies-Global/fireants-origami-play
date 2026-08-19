package com.fireants.template.domain.usecase.origami

import com.fireants.template.data.model.translation.CategoryTranslation
import com.fireants.template.data.repository.OrigamiRepository
import javax.inject.Inject

class GetCategoryTranslationsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(): List<CategoryTranslation> =
        repository.getCategoryTranslations()
}
