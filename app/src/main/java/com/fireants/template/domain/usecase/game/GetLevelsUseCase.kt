package com.fireants.template.domain.usecase.game

import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetLevelsUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): List<LevelEntity> =
        repository.getLevels()
}
