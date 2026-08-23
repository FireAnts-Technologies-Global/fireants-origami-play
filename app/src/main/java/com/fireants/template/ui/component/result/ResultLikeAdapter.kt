package com.fireants.template.ui.component.result

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.databinding.ItemResultLikeBinding
import com.fireants.template.ui.bases.BaseListAdapter
import com.fireants.template.ui.component.main.ProductDisplayFormatter

class ResultLikeAdapter(
    private val onItemClick: (ProductItem) -> Unit,
    private val onFavoriteClick: (ProductItem) -> Unit
) : BaseListAdapter<ProductItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_result_like

    override fun setData(binding: ViewDataBinding, item: ProductItem, layoutPosition: Int) {
        if (binding is ItemResultLikeBinding) {
            binding.tvName.text = ProductDisplayFormatter.name(item)
            binding.ivHeart.setImageResource(
                if (item.isFavorite) R.drawable.ic_favourite_on else R.drawable.ic_favourite_off
            )
            if (item.image.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(binding.ivProduct)
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ProductItem, layoutPosition: Int) {
        if (binding is ItemResultLikeBinding) {
            binding.root.setOnClickListener { onItemClick(obj) }
            binding.ivHeart.setOnClickListener { onFavoriteClick(obj) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ProductItem>() {
        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem == newItem
    }
}
