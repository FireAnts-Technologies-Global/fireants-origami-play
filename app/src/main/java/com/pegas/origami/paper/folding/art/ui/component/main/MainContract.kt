package com.pegas.origami.paper.folding.art.ui.component.main

import com.pegas.origami.paper.folding.art.domain.model.product.ProductData

data class MainState(
    val productData: ProductData? = null,
    val isLoading: Boolean = false
)

sealed interface MainEvent {
    data class ShowError(val message: String) : MainEvent
}