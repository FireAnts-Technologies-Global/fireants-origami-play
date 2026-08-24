package com.pegas.origami.paper.folding.art.domain.usecase.player

import com.pegas.origami.paper.folding.art.data.model.player.PlayerData
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayerUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(initialHintCount: Int = 0): Flow<PlayerData> =
        repository.getPlayerFlow(initialHintCount)
}
