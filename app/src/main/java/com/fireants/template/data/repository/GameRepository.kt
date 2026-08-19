package com.fireants.template.data.repository

import com.fireants.template.data.model.game.AutoFoldStep
import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.model.game.PaperItem

interface GameRepository {
    suspend fun getLevels(): List<LevelEntity>
    suspend fun getLevel(levelId: Int): LevelEntity?
    suspend fun getPapers(): List<PaperItem>
    suspend fun getFoldHints(levelId: Int): List<AutoFoldStep>
}
