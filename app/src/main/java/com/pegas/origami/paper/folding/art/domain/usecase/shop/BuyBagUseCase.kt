package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.ShopResult
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class BuyBagUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(amount: Int, cost: Int): ShopResult {
        if (cost == 0) return ShopResult.Success
        if (userRepository.spendStars(cost)) {
            return ShopResult.Success
        }
        val currentStars = userRepository.getPlayer().stars
        return ShopResult.NotEnoughStars(cost - currentStars)
    }
}
