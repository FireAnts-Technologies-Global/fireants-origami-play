package com.pegas.origami.paper.folding.art.ui.component.game

import com.pegas.origami.paper.folding.art.data.model.game.AutoFoldStep
import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.data.model.player.PlayerData

data class GameState(
    val player: PlayerData? = null,
    val currentLevel: LevelEntity? = null,
    val nextLevelId: Int? = null,
    val selectedPaper: PaperItem? = null,
    val foldHints: List<AutoFoldStep> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface GameEvent {
    data class ShowError(val message: String) : GameEvent
    object ShowHint : GameEvent
    object OpenStore : GameEvent
    data class LevelCompleted(val stars: Int, val coinsEarned: Int) : GameEvent
}
