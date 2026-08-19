package com.fireants.template.data.repository

import com.fireants.template.data.model.game.AutoFoldStep
import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.model.game.PaperItem

interface GameRepository {
    suspend fun getLevels(): List<LevelEntity>
    suspend fun getLevel(levelId: Int): LevelEntity?
    suspend fun getPapers(): List<PaperItem>
    suspend fun getSelectedPaper(): PaperItem?
    suspend fun getFoldHints(levelId: Int): List<AutoFoldStep>
    
    suspend fun getLevelProgressList(): List<com.fireants.template.data.model.game.LevelProgress>
    suspend fun getLevelProgress(levelId: Int): com.fireants.template.data.model.game.LevelProgress?
    suspend fun saveLevelProgress(progress: com.fireants.template.data.model.game.LevelProgress)
}
