package com.fireants.template.ui.component.shop

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.databinding.ItemShopPaperBinding
import com.fireants.template.ui.bases.BaseListAdapter

class PaperAdapter(
    private val onBuyClick: (PaperItem) -> Unit,
    private val onSelectClick: (PaperItem) -> Unit
) : BaseListAdapter<PaperItem>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_shop_paper

    override fun setData(binding: ViewDataBinding, item: PaperItem, layoutPosition: Int) {
        if (binding is ItemShopPaperBinding) {
            binding.tvName.text = "Paper #${item.id}"
            
            Glide.with(binding.root.context)
                .load("file:///android_asset/" + item.imagePreview)
                .into(binding.ivPreview)
            
            when {
                item.isSelected -> {
                    binding.btnAction.text = "Selected"
                    binding.btnAction.isEnabled = false
                    binding.btnAction.alpha = 0.5f
                }
                item.isUnlocked -> {
                    binding.btnAction.text = "Select"
                    binding.btnAction.isEnabled = true
                    binding.btnAction.alpha = 1.0f
                }
                else -> {
                    // Locked
                    binding.btnAction.text = "${item.price} Coins"
                    binding.btnAction.isEnabled = true
                    binding.btnAction.alpha = 1.0f
                }
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: PaperItem, layoutPosition: Int) {
        if (binding is ItemShopPaperBinding) {
            binding.btnAction.setOnClickListener {
                if (!obj.isSelected && obj.isUnlocked) {
                    onSelectClick(obj)
                } else if (!obj.isUnlocked) {
                    onBuyClick(obj)
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<PaperItem>() {
        override fun areItemsTheSame(oldItem: PaperItem, newItem: PaperItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PaperItem, newItem: PaperItem): Boolean =
            oldItem == newItem
    }
}
