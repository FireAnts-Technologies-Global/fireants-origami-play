package com.pegas.origami.paper.folding.art.domain.usecase.game

import com.pegas.origami.paper.folding.art.data.model.game.LevelProgress
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetLevelProgressListUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): List<LevelProgress> {
        return repository.getLevelProgressList()
    }
}
