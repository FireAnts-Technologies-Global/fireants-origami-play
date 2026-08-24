package com.pegas.origami.paper.folding.art.data.model.shop

data class ShopConfig(
    val paperUnlockCost: Int = 800,
    val rewardedCoinAmount: Int = 300,
    val smallCoinReward: Int = 50,
    val largeCoinReward: Int = 300,
    val fallbackCoinReward: Int = 400,
    val dailyRewardIntervalMillis: Long = 86_400_000L
)
