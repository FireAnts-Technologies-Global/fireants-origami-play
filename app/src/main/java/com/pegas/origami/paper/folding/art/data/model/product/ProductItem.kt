package com.pegas.origami.paper.folding.art.data.model.product

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductItem(
    @Json(name = "id") val id: Int,
    @Json(name = "categoryId") val categoryId: Int,
    @Json(name = "name") val name: String,
    @Json(name = "image") val image: String,
    @Json(name = "order") val order: Int,
    @Json(name = "hasVideo") val hasVideo: Boolean = false,
    @Json(name = "isPremium") val isPremium: Boolean = false,
    @Json(name = "isFavorite") val isFavorite: Boolean = false,
    @Json(name = "gameType") val gameType: GameType = GameType.ORIGAMI,
    @Json(name = "difficulty") val difficulty: String = "",
    @Json(name = "stepCount") val stepCount: Int = 0,
    @Json(name = "estimatedTime") val estimatedTime: String = "",
    @Json(name = "sourceId") val sourceId: Int = id
)
