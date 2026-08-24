package com.pegas.origami.paper.folding.art.ui.component.shop

import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.model.player.PlayerData
import com.pegas.origami.paper.folding.art.data.model.shop.BagReward
import com.pegas.origami.paper.folding.art.data.model.shop.BagStatus
import com.pegas.origami.paper.folding.art.data.model.shop.ShopResult
import com.pegas.origami.paper.folding.art.data.model.shop.TicketStatus

data class ShopState(
    val player: PlayerData? = null,
    val papers: List<PaperItem> = emptyList(),
    val bagStatus: BagStatus? = null,
    val ticketStatus: TicketStatus? = null
)

sealed interface ShopEvent {
    data class ShowMessage(val result: ShopResult) : ShopEvent
    data class OnBagsOpened(val rewards: List<BagReward>) : ShopEvent
}
