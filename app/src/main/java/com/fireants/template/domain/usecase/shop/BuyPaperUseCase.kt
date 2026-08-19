package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.shop.ShopConfig
import com.fireants.template.data.model.shop.ShopResult
import com.fireants.template.data.repository.GameRepository
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class BuyPaperUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(paperId: Int): ShopResult {
        val config = ShopConfig()
        val paper = gameRepository.getPaper(paperId) ?: return ShopResult.PaperNotFound
        if (paper.isUnlocked) return ShopResult.AlreadyUnlocked
        
        if (userRepository.spendCoins(config.paperUnlockCost)) {
            userRepository.unlockPaper(paperId)
            return ShopResult.Success
        }
        
        return ShopResult.NotEnoughCoins
    }
}
