package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.ShopResult
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class BuyBagUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(amount: Int, cost: Int): ShopResult {
        if (userRepository.spendStars(cost)) {
            // Usually we would increase a bag count, but the logic seems to open it immediately
            // so this just deducts stars to allow opening.
            return ShopResult.Success
        }
        return ShopResult.NotEnoughStars
    }
}
