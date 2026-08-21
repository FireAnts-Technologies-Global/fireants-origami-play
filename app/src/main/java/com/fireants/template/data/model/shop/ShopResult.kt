package com.fireants.template.data.model.shop

sealed interface ShopResult {
    data object Success : ShopResult
    data class NotEnoughCoins(val missingAmount: Int) : ShopResult
    data class NotEnoughStars(val missingAmount: Int) : ShopResult
    data object AlreadyUnlocked : ShopResult
    data object PaperNotFound : ShopResult
    data object NotAvailableYet : ShopResult
}
