package com.pegas.origami.paper.folding.art.domain.usecase.game

import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetLevelUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(levelId: Int): LevelEntity =
        requireNotNull(repository.getLevel(levelId)) {
            "Level $levelId was not found"
        }
}
