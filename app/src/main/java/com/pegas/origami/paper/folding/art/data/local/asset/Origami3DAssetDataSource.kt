package com.pegas.origami.paper.folding.art.data.local.asset

import com.pegas.origami.paper.folding.art.data.model.origami.CategoryEntity
import com.pegas.origami.paper.folding.art.data.model.origami3d.Item3dOrigamiEntity
import com.pegas.origami.paper.folding.art.data.model.origami3d.Product3dOrigamiEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Origami3DAssetDataSource @Inject constructor(
    private val reader: AssetJsonReader
) {
    suspend fun getCategories(): List<CategoryEntity> =
        reader.read("origami3d/categories.json")

    suspend fun getItems(): List<Item3dOrigamiEntity> =
        reader.read("origami3d/items.json")

    suspend fun getProducts(): List<Product3dOrigamiEntity> =
        reader.read("origami3d/products.json")
}
