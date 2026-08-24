package com.pegas.origami.paper.folding.art.ui.component.main

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.adsdk.billing.AppPurchase
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.data.model.product.ProductItem
import com.pegas.origami.paper.folding.art.databinding.ItemBannerCardBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseListAdapter
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView
import com.pegas.origami.paper.folding.art.ui.bases.ext.visibleView

class BannerAdapter(
    private val onItemClick: (ProductItem) -> Unit = {}
) : BaseListAdapter<ProductItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_banner_card

    override fun setData(binding: ViewDataBinding, item: ProductItem, layoutPosition: Int) {
        if (binding is ItemBannerCardBinding) {
            binding.tvBannerTitle.text = ProductDisplayFormatter.name(item)
            binding.tvBannerType.text = ProductDisplayFormatter.bannerMetadata(item)
            val shouldShowPremiumIcon =
                item.isPremium && !AppPurchase.getInstance().isPurchased(binding.root.context)
            if (shouldShowPremiumIcon) binding.imgVip.visibleView() else binding.imgVip.goneView()
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
