package com.fireants.template.data.local.asset

import com.fireants.template.data.model.game.AutoFoldStep
import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.model.game.PaperItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameAssetDataSource @Inject constructor(
    private val reader: AssetJsonReader
) {
    suspend fun getLevels(): List<LevelEntity> =
        reader.read(LEVELS_PATH)

    suspend fun getPapers(): List<PaperItem> =
        reader.read(PAPERS_PATH)

    suspend fun getFoldHints(): Map<String, List<AutoFoldStep>> =
        reader.read(FOLD_HINTS_PATH)

    companion object {
        private const val LEVELS_PATH = "game/levels.json"
        private const val PAPERS_PATH = "game/papers.json"
        private const val FOLD_HINTS_PATH = "game/fold_hints.json"
    }
}
