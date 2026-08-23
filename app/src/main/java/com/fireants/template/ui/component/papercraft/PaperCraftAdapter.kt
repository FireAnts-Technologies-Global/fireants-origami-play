package com.fireants.template.ui.component.papercraft

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.databinding.ItemPaperCraftCardBinding
import com.fireants.template.ui.bases.BaseListAdapter
import com.fireants.template.ui.component.main.ProductDisplayFormatter

class PaperCraftAdapter(
    private val onItemClick: (ProductItem) -> Unit,
    private val onFavoriteClick: (ProductItem) -> Unit
) : BaseListAdapter<ProductItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_paper_craft_card

    override fun setData(binding: ViewDataBinding, item: ProductItem, layoutPosition: Int) {
        if (binding is ItemPaperCraftCardBinding) {
            binding.tvName.text = ProductDisplayFormatter.name(item)
            binding.tvStepBadge.text = "${item.stepCount} ${if (item.stepCount == 1) "Step" else "Steps"}"
            binding.tvMeta.text = metadata(item)
            binding.ivHeart.setImageResource(
                if (item.isFavorite) R.drawable.ic_favourite_on else R.drawable.ic_favourite_off
            )

            Glide.with(binding.root.context)
                .load(item.image)
                .fitCenter()
                .into(binding.ivProduct)
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ProductItem, layoutPosition: Int) {
        if (binding is ItemPaperCraftCardBinding) {
            binding.root.setOnClickListener { onItemClick(obj) }
            binding.ivHeart.setOnClickListener { onFavoriteClick(obj) }
        }
    }

    private fun metadata(item: ProductItem): SpannableString {
        val text = "${item.difficulty} • ${item.estimatedTime}"
        val span = SpannableString(text)
        span.setSpan(
            ForegroundColorSpan(difficultyColor(item.difficulty)),
            0,
            item.difficulty.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return span
    }

    private fun difficultyColor(difficulty: String): Int {
        return when (difficulty) {
            "Easy" -> Color.parseColor("#00B6AD")
            "Medium" -> Color.parseColor("#FF9A2E")
            "Hard" -> Color.parseColor("#FF527B")
            else -> Color.parseColor("#6E629B")
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ProductItem>() {
        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem == newItem
    }
}
