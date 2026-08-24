package com.pegas.origami.paper.folding.art.domain.usecase.favorite

import com.pegas.origami.paper.folding.art.data.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteIdsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(): Set<Int> = repository.getFavoriteIds()
}
