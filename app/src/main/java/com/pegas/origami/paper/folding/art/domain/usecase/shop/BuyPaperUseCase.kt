package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.ShopConfig
import com.pegas.origami.paper.folding.art.data.model.shop.ShopResult
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
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

        val currentCoins = userRepository.getPlayer().coins
        return ShopResult.NotEnoughCoins(config.paperUnlockCost - currentCoins)
    }
}
