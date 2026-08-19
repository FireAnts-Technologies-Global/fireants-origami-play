package com.fireants.template.ui.component.level

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.fireants.template.R
import com.fireants.template.databinding.ItemLevelBinding
import com.fireants.template.ui.bases.BaseListAdapter

class LevelAdapter(
    private val onLevelClick: (LevelItemUI) -> Unit
) : BaseListAdapter<LevelItemUI>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_level

    override fun setData(binding: ViewDataBinding, item: LevelItemUI, layoutPosition: Int) {
        if (binding is ItemLevelBinding) {
            binding.tvLevelNumber.text = item.level.levelNumber.toString()
            
            // Check if locked
            val isUnlocked = item.progress?.isUnlocked == true || !item.level.isLocked
            
            if (!isUnlocked) {
                // Locked state
                binding.ivLock.visibility = View.VISIBLE
                binding.tvLevelNumber.alpha = 0.3f
                binding.llStars.alpha = 0.3f
            } else {
                // Unlocked state
                binding.ivLock.visibility = View.GONE
                binding.tvLevelNumber.alpha = 1.0f
                binding.llStars.alpha = 1.0f
                
                // Set stars based on progress
                val earnedStars = item.progress?.stars ?: 0
                binding.ivStar1.setImageResource(if (earnedStars >= 1) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
                binding.ivStar2.setImageResource(if (earnedStars >= 2) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
                binding.ivStar3.setImageResource(if (earnedStars >= 3) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: LevelItemUI, layoutPosition: Int) {
        if (binding is ItemLevelBinding) {
            binding.root.setOnClickListener {
                val isUnlocked = obj.progress?.isUnlocked == true || !obj.level.isLocked
                if (isUnlocked) {
                    onLevelClick(obj)
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<LevelItemUI>() {
        override fun areItemsTheSame(oldItem: LevelItemUI, newItem: LevelItemUI): Boolean =
            oldItem.level.id == newItem.level.id

        override fun areContentsTheSame(oldItem: LevelItemUI, newItem: LevelItemUI): Boolean =
            oldItem == newItem
    }
}
