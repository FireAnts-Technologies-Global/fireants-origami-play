package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.model.player.PlayerData
import com.fireants.template.data.repository.GameRepository
import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

data class ShopData(
    val player: PlayerData,
    val papers: List<PaperItem>
)

class GetShopDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(): ShopData {
        return ShopData(
            player = userRepository.getPlayer(),
            papers = gameRepository.getPapers()
        )
    }
}
