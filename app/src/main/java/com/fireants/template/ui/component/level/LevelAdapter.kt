package com.fireants.template.ui.component.level

import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.fireants.template.R
import com.fireants.template.databinding.ItemLevelBinding
import com.fireants.template.ui.bases.BaseListAdapter
import com.fireants.template.ui.bases.ext.goneView
import com.fireants.template.ui.bases.ext.visibleView

class LevelAdapter(
    private val onLevelClick: (LevelItemUI) -> Unit
) : BaseListAdapter<LevelItemUI>(DiffCallback) {

    override fun getItemLayout(viewType: Int): Int = R.layout.item_level

    override fun setData(binding: ViewDataBinding, item: LevelItemUI, layoutPosition: Int) {
        if (binding is ItemLevelBinding) {
            val context = binding.root.context
            binding.tvLevelNumber.text =
                context.getString(R.string.number_format, item.level.levelNumber)
//            val isUnlocked = item.progress?.isUnlocked == true || !item.level.isLocked
            val isUnlocked = true
            if (!isUnlocked) {
                binding.container.setBackgroundResource(R.drawable.bg_level_lock)
                binding.ivLock.visibleView()
                binding.llStars.goneView()
                binding.tvTitleLevel.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_787878
                    )
                )
                binding.tvLevelNumber.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_888888
                    )
                )
            } else {
                binding.container.setBackgroundResource(R.drawable.bg_level_unlock)
                binding.ivLock.goneView()
                binding.llStars.visibleView()
                binding.tvTitleLevel.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_5A5A5A
                    )
                )
                binding.tvLevelNumber.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_1A1A1A
                    )
                )
                val earnedStars = item.progress?.stars ?: 0
                binding.ivStar1.setImageResource(if (earnedStars >= 1) R.drawable.ic_star_on else R.drawable.ic_star_off)
                binding.ivStar2.setImageResource(if (earnedStars >= 2) R.drawable.ic_star_on else R.drawable.ic_star_off)
                binding.ivStar3.setImageResource(if (earnedStars >= 3) R.drawable.ic_star_on else R.drawable.ic_star_off)
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: LevelItemUI, layoutPosition: Int) {
        if (binding is ItemLevelBinding) {
            binding.root.setOnClickListener {
//                val isUnlocked = obj.progress?.isUnlocked == true || !obj.level.isLocked
                val isUnlocked = true
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
