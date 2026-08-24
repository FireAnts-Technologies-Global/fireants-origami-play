package com.pegas.origami.paper.folding.art.data.model.game

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LevelProgress(
    val levelId: Int,
    val isCompleted: Boolean = false,
    val stars: Int = 0,
    val bestMoves: Int = Int.MAX_VALUE,
    val isUnlocked: Boolean = false
)
