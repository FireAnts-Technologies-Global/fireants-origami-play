package com.fireants.template.domain.model.product

import com.fireants.template.data.model.product.ProductItem

data class ProductData(
    val banners: List<ProductItem>,
    val recommendations: List<ProductItem>,
    val hotItems: List<ProductItem>
)
