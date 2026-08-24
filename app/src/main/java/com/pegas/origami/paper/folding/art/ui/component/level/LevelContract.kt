package com.pegas.origami.paper.folding.art.ui.component.level

import com.pegas.origami.paper.folding.art.data.model.game.LevelEntity
import com.pegas.origami.paper.folding.art.data.model.game.LevelProgress
import com.pegas.origami.paper.folding.art.data.model.player.PlayerData

data class LevelItemUI(
    val level: LevelEntity,
    val progress: LevelProgress?
)

data class LevelState(
    val player: PlayerData? = null,
    val levelItems: List<LevelItemUI> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface LevelEvent {
    data class ShowError(val message: String) : LevelEvent
}
