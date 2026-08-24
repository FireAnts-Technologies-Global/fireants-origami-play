package com.pegas.origami.paper.folding.art.domain.usecase.game

import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): List<LevelEntity> =
        repository.getLevels()
}
