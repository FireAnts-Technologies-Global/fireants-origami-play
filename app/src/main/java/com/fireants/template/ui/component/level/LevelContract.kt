package com.fireants.template.ui.component.level

import com.fireants.template.data.model.game.LevelEntity
import com.fireants.template.data.model.game.LevelProgress
import com.fireants.template.data.model.player.PlayerData

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
