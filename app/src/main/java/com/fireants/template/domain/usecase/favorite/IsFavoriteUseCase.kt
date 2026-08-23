package com.fireants.template.domain.usecase.favorite

import com.fireants.template.data.repository.FavoriteRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(id: Int): Boolean = repository.isFavorite(id)
}
