package com.fireants.template.ui.component.game

import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.data.model.player.PlayerData
import com.fireants.template.data.model.game.AutoFoldStep

data class GameState(
    val player: PlayerData? = null,
    val currentLevel: LevelEntity? = null,
    val selectedPaper: PaperItem? = null,
    val foldHints: List<AutoFoldStep> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface GameEvent {
    data class ShowError(val message: String) : GameEvent
    object ShowHint : GameEvent
    data class LevelCompleted(val stars: Int, val coinsEarned: Int) : GameEvent
}
