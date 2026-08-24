package com.pegas.origami.paper.folding.art.domain.usecase.kirigami

import com.pegas.origami.paper.folding.art.data.model.kirigami.KirigamiCategoryEntity
import com.pegas.origami.paper.folding.art.data.repository.KirigamiRepository
import javax.inject.Inject

class GetKirigamiCategoriesUseCase @Inject constructor(
    private val repository: KirigamiRepository
) {
    suspend operator fun invoke(): List<KirigamiCategoryEntity> =
        repository.getCategories()
}
