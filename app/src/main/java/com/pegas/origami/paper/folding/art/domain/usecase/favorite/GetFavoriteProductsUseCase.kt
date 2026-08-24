package com.pegas.origami.paper.folding.art.domain.usecase.favorite

import com.pegas.origami.paper.folding.art.data.model.product.ProductItem
import com.pegas.origami.paper.folding.art.data.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoriteProductsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(): List<ProductItem> = repository.getFavorites()
}
