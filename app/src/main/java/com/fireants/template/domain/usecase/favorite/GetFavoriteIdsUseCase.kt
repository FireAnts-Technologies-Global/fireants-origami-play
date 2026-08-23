package com.fireants.template.domain.usecase.favorite

import com.fireants.template.data.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteIdsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(): Set<Int> = repository.getFavoriteIds()
}
