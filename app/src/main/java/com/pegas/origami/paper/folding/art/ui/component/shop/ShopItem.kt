package com.pegas.origami.paper.folding.art.ui.component.shop

import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.model.shop.BagStatus

sealed interface ShopItem {
    //    object GetCoins : ShopItem
    data class LuckyBag(val bagStatus: BagStatus?) : ShopItem
    object BuyHints : ShopItem
    data class PaperGroup(val papers: List<PaperItem>) : ShopItem
}
