package com.fireants.template.domain.usecase.paper

import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetPapersUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): List<PaperItem> =
        repository.getPapers()
}
