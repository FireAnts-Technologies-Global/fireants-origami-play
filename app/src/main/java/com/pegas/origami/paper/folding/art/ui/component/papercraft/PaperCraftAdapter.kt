package com.pegas.origami.paper.folding.art.ui.component.papercraft

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.billing.PremiumAccessManager
import com.pegas.origami.paper.folding.art.data.model.product.ProductItem
import com.pegas.origami.paper.folding.art.databinding.ItemPaperCraftCardBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseListAdapter
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView
import com.pegas.origami.paper.folding.art.ui.bases.ext.visibleView
import com.pegas.origami.paper.folding.art.ui.component.main.ProductDisplayFormatter

class PaperCraftAdapter(
    private val onItemClick: (ProductItem) -> Unit,
    private val onFavoriteClick: (ProductItem) -> Unit
) : BaseListAdapter<ProductItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_paper_craft_card

    override fun setData(binding: ViewDataBinding, item: ProductItem, layoutPosition: Int) {
        if (binding is ItemPaperCraftCardBinding) {
            binding.tvName.text = ProductDisplayFormatter.name(item)
            val context = binding.root.context
            val stepLabel = context.getString(
                if (item.stepCount == 1) R.string.step_singular else R.string.step_plural
            )
            val shouldShowPremiumIcon =
                item.isPremium && !PremiumAccessManager.isPremium(binding.root.context)
            if (shouldShowPremiumIcon) binding.ivVip.visibleView() else binding.ivVip.goneView()

            binding.tvStepBadge.text = context.getString(
                R.string.step_count_format,
                item.stepCount,
                stepLabel
            )
            binding.tvMeta.text = metadata(context, item)
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

    private fun metadata(context: Context, item: ProductItem): SpannableString {
        val text = context.getString(R.string.metadata_format, item.difficulty, item.estimatedTime)
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
