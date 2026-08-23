package com.fireants.template.data.local.asset

import com.fireants.template.data.model.kirigami.KirigamiItemEntity
import com.fireants.template.data.model.origami.ItemEntity
import com.fireants.template.data.model.origami3d.Item3dOrigamiEntity
import com.fireants.template.data.model.product.GameType
import com.fireants.template.data.model.product.ProductItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductAssetDataSource @Inject constructor(
    private val origamiAssetDataSource: OrigamiAssetDataSource,
    private val kirigamiAssetDataSource: KirigamiAssetDataSource,
    private val origami3DAssetDataSource: Origami3DAssetDataSource
) {

    suspend fun getItems(): List<ProductItem> {
        return buildList {
            addAll(origamiAssetDataSource.getItems().map { it.toProductItem(GameType.ORIGAMI) })
            addAll(kirigamiAssetDataSource.getItems().map { it.toProductItem(GameType.KIRIGAMI) })
            addAll(origami3DAssetDataSource.getItems().map { it.toProductItem(GameType.ORIGAMI_3D) })
        }
    }

    private fun ItemEntity.toProductItem(gameType: GameType): ProductItem {
        return ProductItem(
            id = uniqueId(id, gameType),
            categoryId = categoryId,
            name = name,
            image = assetImagePath(image),
            order = order,
            showReward = showReward,
            hasVideo = hasVideo,
            gameType = gameType,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }

    private fun KirigamiItemEntity.toProductItem(gameType: GameType): ProductItem {
        return ProductItem(
            id = uniqueId(id, gameType),
            categoryId = categoryId,
            name = name,
            image = assetImagePath(image),
            order = order,
            showReward = showReward,
            hasVideo = hasVideo,
            gameType = gameType,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }

    private fun Item3dOrigamiEntity.toProductItem(gameType: GameType): ProductItem {
        return ProductItem(
            id = uniqueId(id, gameType),
            categoryId = categoryId,
            name = name,
            image = assetImagePath(image),
            order = order,
            showReward = showReward,
            hasVideo = hasVideo,
            gameType = gameType,
            difficulty = difficulty,
            stepCount = stepCount,
            estimatedTime = estimatedTime
        )
    }

    private fun uniqueId(id: Int, gameType: GameType): Int {
        return id + when (gameType) {
            GameType.ORIGAMI -> 10_000
            GameType.KIRIGAMI -> 20_000
            GameType.ORIGAMI_3D -> 30_000
        }
    }

    private fun assetImagePath(image: String): String {
        return if (image.startsWith(ASSET_PREFIX)) image else ASSET_PREFIX + image
    }

    companion object {
        private const val ASSET_PREFIX = "file:///android_asset/"
    }
}
