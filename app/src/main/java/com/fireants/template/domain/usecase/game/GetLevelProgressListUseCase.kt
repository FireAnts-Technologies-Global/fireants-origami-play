package com.fireants.template.domain.usecase.game

import com.fireants.template.data.model.game.LevelProgress
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetLevelProgressListUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): List<LevelProgress> {
        return repository.getLevelProgressList()
    }
}
