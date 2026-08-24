package com.pegas.origami.paper.folding.art.data.local.asset

import android.content.Context
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@Singleton
class AssetJsonReader @Inject constructor(
    @ApplicationContext val context: Context,
    val moshi: Moshi
) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified T> read(
        path: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): T = withContext(dispatcher) {
        val json = context.assets
            .open(path)
            .bufferedReader()
            .use { it.readText() }

        moshi.adapter<T>(typeOf<T>().javaType).fromJson(json)!!
    }
}
