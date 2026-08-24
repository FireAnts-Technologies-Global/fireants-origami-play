package com.pegas.origami.paper.folding.art.data.repository

import com.pegas.origami.paper.folding.art.data.model.game.AutoFoldStep
import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.model.game.PaperItem

interface GameRepository {
    suspend fun getLevels(): List<LevelEntity>
    suspend fun getLevel(levelId: Int): LevelEntity?
    suspend fun getPapers(): List<PaperItem>
    suspend fun getPaper(paperId: Int): PaperItem?
    suspend fun getSelectedPaper(): PaperItem?
    suspend fun getFoldHints(levelId: Int): List<AutoFoldStep>

    suspend fun getLevelProgressList(): List<com.pegas.origami.paper.folding.art.data.model.game.LevelProgress>
    suspend fun getLevelProgress(levelId: Int): com.pegas.origami.paper.folding.art.data.model.game.LevelProgress?
    suspend fun saveLevelProgress(progress: com.pegas.origami.paper.folding.art.data.model.game.LevelProgress)
}
