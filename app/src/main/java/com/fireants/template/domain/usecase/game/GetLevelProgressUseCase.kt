package com.fireants.template.domain.usecase.game

import com.fireants.template.data.model.game.LevelProgress
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetLevelProgressUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(levelId: Int): LevelProgress? {
        return repository.getLevelProgress(levelId)
    }
}
