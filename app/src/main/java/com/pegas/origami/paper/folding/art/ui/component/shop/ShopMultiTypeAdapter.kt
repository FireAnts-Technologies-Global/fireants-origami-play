package com.pegas.origami.paper.folding.art.ui.component.shop

import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.pegas.origami.paper.folding.art.R
import com.pegas.origami.paper.folding.art.data.model.game.PaperItem
import com.pegas.origami.paper.folding.art.databinding.ItemShopBuyHintsBinding
import com.pegas.origami.paper.folding.art.databinding.ItemShopLuckyBagBinding
import com.pegas.origami.paper.folding.art.databinding.ItemShopPaperBinding
import com.pegas.origami.paper.folding.art.databinding.ItemShopPaperGroupBinding
import com.pegas.origami.paper.folding.art.ui.bases.BaseListAdapter
import com.pegas.origami.paper.folding.art.ui.bases.ext.goneView

interface ShopInteractionListener {
    fun onWatchAdClick()
    fun onClaimFreeBagClick()
    fun onWatchAdBagClick()
    fun onBuy1BagClick()
    fun onBuy10BagsClick()
    fun onBuy1HintClick()
    fun onBuy3HintsClick()
    fun onBuy5HintsClick()
    fun onSelectPaperClick(paper: PaperItem)
    fun onBuyPaperClick(paper: PaperItem)
}

class ShopMultiTypeAdapter(
    private val listener: ShopInteractionListener
) : BaseListAdapter<ShopItem>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
//            is ShopItem.GetCoins -> R.layout.item_shop_get_coins
            is ShopItem.LuckyBag -> R.layout.item_shop_lucky_bag
            is ShopItem.BuyHints -> R.layout.item_shop_buy_hints
            is ShopItem.PaperGroup -> R.layout.item_shop_paper_group
        }
    }

    override fun getItemLayout(viewType: Int): Int = viewType

    override fun setData(binding: ViewDataBinding, item: ShopItem, layoutPosition: Int) {
        when (item) {
//            is ShopItem.GetCoins -> {}
            is ShopItem.LuckyBag -> {
                if (binding is ItemShopLuckyBagBinding) {
                    val status = item.bagStatus
                    if (status != null && status.canClaim) {
                        binding.btnClaimFreeBag.isEnabled = true
                        binding.lnClaim.setBackgroundResource(R.drawable.bg_claim)
                        binding.tvClam.text = context?.getString(R.string.claim)
                        context?.let {
                            binding.tvClam.setTextColor(
                                ContextCompat.getColor(
                                    it,
                                    R.color.color_4FEF44
                                )
                            )
                        }
                    } else if (status != null) {
                        binding.btnClaimFreeBag.isEnabled = false
                        binding.lnClaim.setBackgroundResource(R.drawable.bg_time)
                        val totalSeconds = status.remainingMillis / 1000
                        val hours = totalSeconds / 3600
                        val mins = (totalSeconds % 3600) / 60
                        val secs = totalSeconds % 60
                        binding.tvClam.text = String.format("%02d:%02d:%02d", hours, mins, secs)
                        context?.let {
                            binding.tvClam.setTextColor(
                                ContextCompat.getColor(
                                    it,
                                    R.color.color_EF4444
                                )
                            )
                        }
                    } else {
                        binding.btnClaimFreeBag.isEnabled = false
                        binding.lnClaim.setBackgroundResource(R.drawable.bg_claim)
                        binding.tvClam.text = context?.getString(R.string.claim)
                        context?.let {
                            binding.tvClam.setTextColor(
                                ContextCompat.getColor(
                                    it,
                                    R.color.color_4FEF44
                                )
                            )
                        }
                    }
                }
            }

            is ShopItem.BuyHints -> {}
            is ShopItem.PaperGroup -> {
                if (binding is ItemShopPaperGroupBinding) {
                    var innerAdapter = binding.rvPapers.adapter as? InnerPaperAdapter
                    if (innerAdapter == null) {
                        innerAdapter = InnerPaperAdapter(listener)
                        binding.rvPapers.layoutManager = GridLayoutManager(binding.root.context, 4)
                        binding.rvPapers.adapter = innerAdapter

                        val animator = binding.rvPapers.itemAnimator
                        if (animator is androidx.recyclerview.widget.SimpleItemAnimator) {
                            animator.supportsChangeAnimations = false
                        }
                    }
                    innerAdapter.submitList(item.papers)
                }
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ShopItem, layoutPosition: Int) {
        when (obj) {
//            is ShopItem.GetCoins -> {
//                if (binding is ItemShopGetCoinsBinding) {
//                    binding.btnWatchAd.setOnClickListener { listener.onWatchAdClick() }
//                }
//            }

            is ShopItem.LuckyBag -> {
                if (binding is ItemShopLuckyBagBinding) {
                    binding.btnClaimFreeBag.setOnClickListener { listener.onClaimFreeBagClick() }
                    binding.btnWatchAdBag.setOnClickListener { listener.onWatchAdBagClick() }
                    binding.btnBuy1Bag.setOnClickListener { listener.onBuy1BagClick() }
                    binding.btnBuy10Bags.setOnClickListener { listener.onBuy10BagsClick() }
                }
            }

            is ShopItem.BuyHints -> {
                if (binding is ItemShopBuyHintsBinding) {
                    binding.btnBuy1Hint.setOnClickListener { listener.onBuy1HintClick() }
                    binding.btnBuy3Hints.setOnClickListener { listener.onBuy3HintsClick() }
                    binding.btnBuy5Hints.setOnClickListener { listener.onBuy5HintsClick() }
                }
            }

            is ShopItem.PaperGroup -> {}
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ShopItem>() {
        override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean =
            oldItem == newItem
    }
}

class InnerPaperAdapter(
    private val listener: ShopInteractionListener
) : BaseListAdapter<PaperItem>(DiffCallback) {

    override fun getItemViewType(position: Int): Int = R.layout.item_shop_paper
    override fun getItemLayout(viewType: Int): Int = R.layout.item_shop_paper

    override fun setData(binding: ViewDataBinding, item: PaperItem, layoutPosition: Int) {
        if (binding is ItemShopPaperBinding) {
            Glide.with(binding.root.context)
                .load("file:///android_asset/" + item.imagePreview)
                .into(binding.ivPreview)

            when {
                item.isSelected -> {
                    binding.tvStatus.text = context?.getString(R.string.selected)
                    binding.imgCoin.goneView()
                    binding.container.setBackgroundResource(R.drawable.bg_paper_selected)

                }

                item.isUnlocked -> {
                    binding.tvStatus.text = context?.getString(R.string.unlocked)
                    binding.imgCoin.goneView()
                    binding.container.setBackgroundResource(R.drawable.bg_paper_unselected)
                }
                else -> {
                    binding.container.setBackgroundResource(R.drawable.bg_paper_unselected)
                }
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: PaperItem, layoutPosition: Int) {
        if (binding is ItemShopPaperBinding) {
            binding.container.setOnClickListener {
                if (!obj.isSelected && obj.isUnlocked) {
                    listener.onSelectPaperClick(obj)
                } else if (!obj.isUnlocked) {
                    listener.onBuyPaperClick(obj)
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<PaperItem>() {
        override fun areItemsTheSame(oldItem: PaperItem, newItem: PaperItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PaperItem, newItem: PaperItem) = oldItem == newItem
    }
}
