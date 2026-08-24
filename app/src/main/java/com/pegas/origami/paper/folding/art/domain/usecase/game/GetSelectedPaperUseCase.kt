package com.pegas.origami.paper.folding.art.domain.usecase.game

import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetSelectedPaperUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): PaperItem? {
        return repository.getSelectedPaper()
    }
}
