package com.fireants.template.data.model.shop

sealed interface ShopResult {
    data object Success : ShopResult
    data object NotEnoughCoins : ShopResult
    data object NotEnoughStars : ShopResult
    data object AlreadyUnlocked : ShopResult
    data object PaperNotFound : ShopResult
    data object NotAvailableYet : ShopResult
}
