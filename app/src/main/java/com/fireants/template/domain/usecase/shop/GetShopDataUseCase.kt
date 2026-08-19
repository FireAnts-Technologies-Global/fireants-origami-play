package com.fireants.template.domain.usecase.shop

import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject

class GetShopDataUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(): List<PaperItem> {
        return gameRepository.getPapers()
    }
}
