package com.pegas.origami.paper.folding.art.data.repository.impl

import android.content.Context
import com.fireants.adsdk.billing.AppPurchase
import com.pegas.origami.paper.folding.art.data.local.asset.GameAssetDataSource
import com.pegas.origami.paper.folding.art.data.local.pref.OrigamiPreference
import com.pegas.origami.paper.folding.art.data.model.game.AutoFoldStep
import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.repository.GameRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
        val isPremiumPurchased = AppPurchase.getInstance().isPurchased(context)

        return assetDataSource.getPapers().map { paper ->
            paper.copy(
                isUnlocked = isPremiumPurchased || paper.id in unlockedPaperIds,
                isSelected = paper.id == selectedPaperId
            )
        }
    }

    override suspend fun getPaper(paperId: Int): PaperItem? {
        return getPapers().firstOrNull { it.id == paperId }
    }

    override suspend fun getFoldHints(levelId: Int): List<AutoFoldStep> =
        assetDataSource.getFoldHints()[levelId.toString()].orEmpty()

    override suspend fun getSelectedPaper(): PaperItem? {
        return getPapers().firstOrNull { it.isSelected }
    }

    override suspend fun getLevelProgressList(): List<com.pegas.origami.paper.folding.art.data.model.game.LevelProgress> {
        return preference.getLevelProgressList()
    }

    override suspend fun getLevelProgress(levelId: Int): com.pegas.origami.paper.folding.art.data.model.game.LevelProgress? {
        return preference.getLevelProgress(levelId)
    }

    override suspend fun saveLevelProgress(progress: com.pegas.origami.paper.folding.art.data.model.game.LevelProgress) {
        preference.saveLevelProgress(progress)
    }
}
