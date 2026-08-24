package com.pegas.origami.paper.folding.art.data.model.translation

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ItemTranslation(
    val name: String,
    val en: String,
    val vi: String,
    val es: String,
    val fr: String,
    val de: String,
    val `in`: String,
    val it: String,
    val nl: String,
    val pt: String,
    val ro: String,
    val ru: String,
    val hi: String,
    val ja: String,
    val ko: String
)
