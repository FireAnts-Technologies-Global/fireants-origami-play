package com.fireants.template.data.model.product

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "code") val code: Int = 200,
    @Json(name = "message") val message: String = "OK",
    @Json(name = "data") val data: T? = null
)
