package com.fireants.template.ui.component.main

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.databinding.ItemProductCardBinding
import com.fireants.template.ui.bases.BaseListAdapter

class ProductItemAdapter(
    private val onItemClick: (ProductItem) -> Unit = {}
) : BaseListAdapter<ProductItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_product_card

    override fun setData(binding: ViewDataBinding, item: ProductItem, layoutPosition: Int) {
        if (binding is ItemProductCardBinding) {
            binding.tvName.text = ProductDisplayFormatter.name(item)
            binding.tvType.text = ProductDisplayFormatter.metadata(item)

            if (item.image.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(binding.ivProduct)
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ProductItem, layoutPosition: Int) {
        if (binding is ItemProductCardBinding) {
            binding.root.setOnClickListener { onItemClick(obj) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ProductItem>() {
        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem == newItem
    }
}
