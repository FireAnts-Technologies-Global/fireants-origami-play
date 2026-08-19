package com.fireants.template.data.repository

import com.fireants.template.data.model.origami.CategoryEntity
import com.fireants.template.data.model.origami3d.Item3dOrigamiEntity
import com.fireants.template.data.model.origami3d.Product3dOrigamiEntity

interface Origami3DRepository {
    suspend fun getCategories(): List<CategoryEntity>
    suspend fun getItems(categoryId: Int? = null): List<Item3dOrigamiEntity>
    suspend fun getItem(itemId: Int): Item3dOrigamiEntity?
    suspend fun getProducts(productId: Int): List<Product3dOrigamiEntity>
}
