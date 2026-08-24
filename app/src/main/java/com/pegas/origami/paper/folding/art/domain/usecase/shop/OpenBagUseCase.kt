package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.shop.BagReward
import com.pegas.origami.paper.folding.art.data.model.shop.ShopConfig
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import com.pegas.origami.paper.folding.art.data.repository.UserRepository
import javax.inject.Inject

class OpenBagUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(): BagReward {
        val config = ShopConfig()
        val allPapers = gameRepository.getPapers()
        val unlockedIds = userRepository.getUnlockedPaperIds()
        val lockedPapers = allPapers.filter { it.id !in unlockedIds }

        val rand = kotlin.random.Random.nextInt(100)

        return if (rand < 35) {
            if (lockedPapers.isNotEmpty()) {
                val paper = lockedPapers.random()
                userRepository.unlockPaper(paper.id)
                BagReward.Paper(paper.id)
            } else {
                userRepository.addCoins(config.fallbackCoinReward)
                BagReward.Coin(config.fallbackCoinReward)
            }
        } else if (rand < 70) {
            userRepository.addCoins(config.smallCoinReward)
            BagReward.Coin(config.smallCoinReward)
        } else if (rand < 85) {
            userRepository.addCoins(config.largeCoinReward)
            BagReward.Coin(config.largeCoinReward)
        } else {
            userRepository.addHints(1)
            BagReward.Hint(1)
        }
    }
}
