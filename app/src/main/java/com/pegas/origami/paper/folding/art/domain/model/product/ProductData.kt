package com.pegas.origami.paper.folding.art.domain.model.product

import com.pegas.origami.paper.folding.art.data.model.product.ProductItem

data class ProductData(
    val banners: List<ProductItem>,
    val recommendations: List<ProductItem>,
    val hotItems: List<ProductItem>,
    val favorites: List<ProductItem> = emptyList()
)
