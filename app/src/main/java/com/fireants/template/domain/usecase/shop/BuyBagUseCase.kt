package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.ShopResult
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class BuyBagUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(amount: Int, cost: Int): ShopResult {
        if (cost == 0) return ShopResult.Success
        if (userRepository.spendCoins(cost)) {
            return ShopResult.Success
        }
        return ShopResult.NotEnoughCoins
    }
}
