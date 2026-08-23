package com.fireants.template.ui.component.main

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.product.ProductItem
import com.fireants.template.databinding.ItemBannerCardBinding
import com.fireants.template.ui.bases.BaseListAdapter

class BannerAdapter(
    private val onItemClick: (ProductItem) -> Unit = {}
) : BaseListAdapter<ProductItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_banner_card

    override fun setData(binding: ViewDataBinding, item: ProductItem, layoutPosition: Int) {
        if (binding is ItemBannerCardBinding) {
            binding.tvBannerTitle.text = ProductDisplayFormatter.name(item)
            binding.tvBannerType.text = ProductDisplayFormatter.metadata(item)

            if (item.image.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.image)
                    .into(binding.ivBanner)
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ProductItem, layoutPosition: Int) {
        if (binding is ItemBannerCardBinding) {
            binding.root.setOnClickListener { onItemClick(obj) }
            binding.btnBannerAction.setOnClickListener { onItemClick(obj) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ProductItem>() {
        override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
            oldItem == newItem
    }
}
