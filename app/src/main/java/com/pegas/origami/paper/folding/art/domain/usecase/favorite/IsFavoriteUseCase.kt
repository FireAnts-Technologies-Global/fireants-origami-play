package com.pegas.origami.paper.folding.art.domain.usecase.favorite

import com.pegas.origami.paper.folding.art.data.repository.FavoriteRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(id: Int): Boolean = repository.isFavorite(id)
}
