package com.fireants.template.ui.component.shop

import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.model.shop.BagStatus

sealed interface ShopItem {
    object GetCoins : ShopItem
    data class LuckyBag(val bagStatus: BagStatus?) : ShopItem
    object BuyHints : ShopItem
    object PaperTitle : ShopItem
    data class Paper(val paper: PaperItem) : ShopItem
}
