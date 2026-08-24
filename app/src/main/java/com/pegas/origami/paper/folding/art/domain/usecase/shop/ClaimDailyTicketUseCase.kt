package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.ShopConfig
import com.pegas.origami.paper.folding.art.data.model.shop.ShopResult
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class ClaimDailyTicketUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(nowMillis: Long): ShopResult {
        val config = ShopConfig()
        val lastClaim = userRepository.getLastClaimTicketTime()
        if (nowMillis - lastClaim >= config.dailyRewardIntervalMillis) {
            userRepository.setLastClaimTicketTime(nowMillis)
            userRepository.addTickets(1)
            return ShopResult.Success
        }
        return ShopResult.NotAvailableYet
    }
}
