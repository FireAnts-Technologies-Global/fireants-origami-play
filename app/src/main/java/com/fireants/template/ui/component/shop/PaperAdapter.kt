package com.fireants.template.ui.component.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.databinding.ItemShopPaperBinding

class PaperAdapter(
    private val onBuyClick: (PaperItem) -> Unit,
    private val onSelectClick: (PaperItem) -> Unit
) : ListAdapter<PaperItem, PaperAdapter.PaperViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaperViewHolder {
        val binding = ItemShopPaperBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaperViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PaperViewHolder(
        private val binding: ItemShopPaperBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaperItem) {
            binding.tvName.text = "Paper #${item.id}"
            
            // In a real implementation, you would load the image using Glide/Coil based on item.imagePreview
            // For now, it stays a gray box.
            
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
                    binding.btnAction.setOnClickListener { onSelectClick(item) }
                }
                else -> {
                    // Locked
                    binding.btnAction.text = "${item.price} Coins" // or use ShopConfig.paperUnlockCost
                    binding.btnAction.isEnabled = true
                    binding.btnAction.alpha = 1.0f
                    binding.btnAction.setOnClickListener { onBuyClick(item) }
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
