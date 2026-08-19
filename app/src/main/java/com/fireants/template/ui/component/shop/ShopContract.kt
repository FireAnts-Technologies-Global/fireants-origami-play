package com.fireants.template.ui.component.shop

import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.model.player.PlayerData
import com.fireants.template.data.model.shop.BagReward
import com.fireants.template.data.model.shop.BagStatus
import com.fireants.template.data.model.shop.ShopResult
import com.fireants.template.data.model.shop.TicketStatus

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
