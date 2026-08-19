package com.fireants.template.domain.usecase.game

import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetLevelUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(levelId: Int): LevelEntity =
        requireNotNull(repository.getLevel(levelId)) {
            "Level $levelId was not found"
        }
}
