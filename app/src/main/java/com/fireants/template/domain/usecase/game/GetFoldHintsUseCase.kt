package com.fireants.template.domain.usecase.game

import com.fireants.template.data.model.game.AutoFoldStep
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetFoldHintsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(levelId: Int): List<AutoFoldStep> =
        repository.getFoldHints(levelId)
}
