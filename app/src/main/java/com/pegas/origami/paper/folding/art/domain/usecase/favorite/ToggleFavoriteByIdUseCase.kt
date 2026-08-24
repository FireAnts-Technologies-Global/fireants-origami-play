package com.pegas.origami.paper.folding.art.domain.usecase.favorite

import com.pegas.origami.paper.folding.art.data.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteByIdUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(
        id: Int,
        sourceId: Int,
        gameType: String,
        name: String,
        image: String,
        difficulty: String,
        stepCount: Int,
        estimatedTime: String
    ): Boolean {
        return repository.toggleFavorite(
            id = id,
            sourceId = sourceId,
            gameType = gameType,
            name = name,
            image = image,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }
}
