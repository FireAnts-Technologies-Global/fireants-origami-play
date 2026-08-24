package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.ShopResult
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class BuyHintUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(amount: Int, cost: Int): ShopResult {
        if (userRepository.spendCoins(cost)) {
            userRepository.addHints(amount)
            return ShopResult.Success
        }
        val currentCoins = userRepository.getPlayer().coins
        return ShopResult.NotEnoughCoins(cost - currentCoins)
    }
}
