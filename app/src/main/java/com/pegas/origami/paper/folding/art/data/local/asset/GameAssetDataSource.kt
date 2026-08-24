package com.pegas.origami.paper.folding.art.data.local.asset

import com.pegas.origami.paper.folding.art.data.model.game.AutoFoldStep
import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameAssetDataSource @Inject constructor(
    private val reader: AssetJsonReader
) {
    private var cachedLevels: List<LevelEntity>? = null
    private var cachedPapers: List<PaperItem>? = null
    private var cachedFoldHints: Map<String, List<AutoFoldStep>>? = null

    suspend fun getLevels(): List<LevelEntity> {
        if (cachedLevels == null) {
            cachedLevels = reader.read(LEVELS_PATH)
        }
        return cachedLevels!!
    }

    suspend fun getPapers(): List<PaperItem> {
        if (cachedPapers == null) {
            cachedPapers = reader.read(PAPERS_PATH)
        }
        return cachedPapers!!
    }

    suspend fun getFoldHints(): Map<String, List<AutoFoldStep>> {
        if (cachedFoldHints == null) {
            cachedFoldHints = reader.read(FOLD_HINTS_PATH)
        }
        return cachedFoldHints!!
    }

    companion object {
        private const val LEVELS_PATH = "game/levels.json"
        private const val PAPERS_PATH = "game/papers.json"
        private const val FOLD_HINTS_PATH = "game/fold_hints.json"
    }
}
