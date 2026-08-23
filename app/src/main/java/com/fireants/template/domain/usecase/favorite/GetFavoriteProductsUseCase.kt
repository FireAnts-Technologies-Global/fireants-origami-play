package com.fireants.template.domain.usecase.favorite

import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.data.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteProductsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(): List<ProductItem> = repository.getFavorites()
}
