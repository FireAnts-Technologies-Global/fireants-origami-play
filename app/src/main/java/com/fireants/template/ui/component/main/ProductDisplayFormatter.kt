package com.fireants.template.ui.component.main

import com.fireants.template.data.model.product.ProductItem

object ProductDisplayFormatter {

    fun name(item: ProductItem): String {
        return item.name
            .split("_")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                if (word.equals("3d", ignoreCase = true)) {
                    "3D"
                } else {
                    word.replaceFirstChar { char -> char.uppercaseChar() }
                }
            }
    }

    fun metadata(item: ProductItem): String {
        return if (item.stepCount > 0 && item.difficulty.isNotBlank()) {
            "${item.stepCount} ${stepLabel(item.stepCount)} • ${item.difficulty}"
        } else {
            item.gameType.folderName
                .replaceFirstChar { it.uppercaseChar() }
                .replace("3d", "3D")
        }
    }

    private fun stepLabel(stepCount: Int): String {
        return if (stepCount == 1) "step" else "steps"
    }
}
