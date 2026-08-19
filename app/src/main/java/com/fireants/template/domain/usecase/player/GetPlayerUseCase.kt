package com.fireants.template.domain.usecase.player

import kotlinx.coroutines.flow.Flow

import com.fireants.template.data.model.player.PlayerData
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class GetPlayerUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(initialHintCount: Int = 0): Flow<PlayerData> =
        repository.getPlayerFlow(initialHintCount)
}
