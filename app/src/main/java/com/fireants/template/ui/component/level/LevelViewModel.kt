package com.fireants.template.ui.component.level

import com.fireants.template.data.model.game.LevelProgress
import com.fireants.template.domain.usecase.game.GetLevelProgressUseCase
import com.fireants.template.domain.usecase.game.GetLevelsUseCase
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LevelViewModel @Inject constructor(
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getLevelProgress: GetLevelProgressUseCase,
    private val getPlayerUseCase: GetPlayerUseCase
) : BaseViewModel()