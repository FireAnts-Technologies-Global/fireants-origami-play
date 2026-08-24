package com.pegas.origami.paper.folding.art.data.model.shop

sealed interface BagReward {
    data class Paper(val paperId: Int) : BagReward
    data class Coin(val amount: Int) : BagReward
    data class Hint(val amount: Int) : BagReward
}
