package com.pegas.origami.paper.folding.art.domain.usecase.kirigami

import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiCategoryTranslationsUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(): List<CategoryTranslation> =
        repository.getCategoryTranslations()
}
