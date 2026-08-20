package com.fireants.template.ui.component.main

import com.fireants.template.domain.model.product.ProductData

data class MainState(
    val productData: ProductData? = null,
    val isLoading: Boolean = false
)

sealed interface MainEvent {
    data class ShowError(val message: String) : MainEvent
}