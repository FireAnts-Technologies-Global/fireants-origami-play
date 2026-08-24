package com.pegas.origami.paper.folding.art.domain.usecase.origami

import com.pegas.origami.paper.folding.art.data.model.translation.CategoryTranslation
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import javax.inject.Inject

class GetCategoryTranslationsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(): List<CategoryTranslation> =
        repository.getCategoryTranslations()
}
