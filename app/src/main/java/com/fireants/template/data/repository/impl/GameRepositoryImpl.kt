package com.fireants.template.data.repository.impl

import com.fireants.template.data.local.asset.GameAssetDataSource
import com.fireants.template.data.local.pref.OrigamiPreference
import com.fireants.template.data.model.game.AutoFoldStep
import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.repository.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val assetDataSource: GameAssetDataSource,
    private val preference: OrigamiPreference
) : GameRepository {

    override suspend fun getLevels(): List<LevelEntity> =
        assetDataSource.getLevels()
            .sortedBy { it.levelNumber }

    override suspend fun getLevel(levelId: Int): LevelEntity? =
        assetDataSource.getLevels()
            .firstOrNull { it.id == levelId }

    override suspend fun getPapers(): List<PaperItem> {
        val selectedPaperId = preference.selectedPaperId
        val unlockedPaperIds = preference.unlockedPaperIds

        return assetDataSource.getPapers().map { paper ->
            paper.copy(
                isUnlocked = paper.id in unlockedPaperIds,
                isSelected = paper.id == selectedPaperId
            )
        }
    }

    override suspend fun getFoldHints(levelId: Int): List<AutoFoldStep> =
        assetDataSource.getFoldHints()[levelId.toString()].orEmpty()
}
