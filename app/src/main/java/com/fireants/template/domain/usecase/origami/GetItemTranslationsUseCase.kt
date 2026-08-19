package com.fireants.template.domain.usecase.origami

import com.fireants.template.data.model.translation.ItemTranslation
import com.fireants.template.data.repository.OrigamiRepository
import javax.inject.Inject

class GetItemTranslationsUseCase @Inject constructor(
    private val repository: OrigamiRepository
) {
    suspend operator fun invoke(): List<ItemTranslation> =
        repository.getItemTranslations()
}
