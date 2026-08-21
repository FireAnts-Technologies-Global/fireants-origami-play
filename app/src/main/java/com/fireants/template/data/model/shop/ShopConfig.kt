package com.fireants.template.data.model.shop

data class ShopConfig(
    val paperUnlockCost: Int = 800,
    val rewardedCoinAmount: Int = 300,
    val smallCoinReward: Int = 50,
    val largeCoinReward: Int = 300,
    val fallbackCoinReward: Int = 400,
    val dailyRewardIntervalMillis: Long = 5_000L
)
