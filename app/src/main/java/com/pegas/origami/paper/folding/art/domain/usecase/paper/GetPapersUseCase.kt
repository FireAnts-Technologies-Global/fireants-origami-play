package com.pegas.origami.paper.folding.art.domain.usecase.paper

import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import javax.inject.Inject

class GetPapersUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(): List<PaperItem> =
        repository.getPapers()
}
