package com.pegas.origami.paper.folding.art.domain.usecase.game

import com.pegas.origami.paper.folding.art.data.model.game.AutoFoldStep
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetFoldHintsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(levelId: Int): List<AutoFoldStep> =
        repository.getFoldHints(levelId)
}
