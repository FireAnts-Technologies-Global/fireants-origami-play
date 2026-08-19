package com.fireants.template.ui.component.game

import com.fireants.template.domain.usecase.game.CompleteLevelUseCase
import com.fireants.template.domain.usecase.game.GetFoldHintsUseCase
import com.fireants.template.domain.usecase.game.GetLevelsUseCase
import com.fireants.template.domain.usecase.game.GetSelectedPaperUseCase
import com.fireants.template.domain.usecase.player.GetPlayerUseCase
import com.fireants.template.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getFoldHintsUseCase: GetFoldHintsUseCase,
    private val getSelectedPaperUseCase: GetSelectedPaperUseCase,
    private val getPlayerUseCase: GetPlayerUseCase,
    private val useHintsUseCase: GetFoldHintsUseCase,
    private val completeLevelUseCase: CompleteLevelUseCase
) : BaseViewModel()