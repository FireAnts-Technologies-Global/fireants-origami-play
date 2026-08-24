package com.pegas.origami.paper.folding.art.domain.usecase.origami

import com.pegas.origami.paper.folding.art.data.model.translation.ItemTranslation
import com.pegas.origami.paper.folding.art.data.repository.OrigamiRepository
import javax.inject.Inject

class GetItemTranslationsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(): List<ItemTranslation> =
        repository.getItemTranslations()
}
