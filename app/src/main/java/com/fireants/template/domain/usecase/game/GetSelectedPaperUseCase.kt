package com.fireants.template.domain.usecase.game

import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetSelectedPaperUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): PaperItem? {
        return repository.getSelectedPaper()
    }
}
