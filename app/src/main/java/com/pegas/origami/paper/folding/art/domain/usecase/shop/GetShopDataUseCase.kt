package com.pegas.origami.paper.folding.art.domain.usecase.shop

import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetShopDataUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(): List<PaperItem> {
        return gameRepository.getPapers()
    }
}
