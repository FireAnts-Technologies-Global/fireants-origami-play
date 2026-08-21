package com.fireants.template.ui.component.shop

import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.fireants.template.R
import com.fireants.template.data.model.game.PaperItem
import com.fireants.template.databinding.ItemShopBuyHintsBinding
import com.fireants.template.databinding.ItemShopGetCoinsBinding
import com.fireants.template.databinding.ItemShopLuckyBagBinding
import com.fireants.template.databinding.ItemShopPaperBinding
import com.fireants.template.ui.bases.BaseListAdapter

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
            is ShopItem.GetCoins -> R.layout.item_shop_get_coins
            is ShopItem.LuckyBag -> R.layout.item_shop_lucky_bag
            is ShopItem.BuyHints -> R.layout.item_shop_buy_hints
            is ShopItem.Paper -> R.layout.item_shop_paper
        }
    }

    override fun getItemLayout(viewType: Int): Int = viewType

    override fun setData(binding: ViewDataBinding, item: ShopItem, layoutPosition: Int) {
        when (item) {
            is ShopItem.GetCoins -> {}
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
            is ShopItem.Paper -> {
                if (binding is ItemShopPaperBinding) {
                    binding.tvName.text = "Paper #${item.paper.id}"

                    Glide.with(binding.root.context)
                        .load("file:///android_asset/" + item.paper.imagePreview)
                        .into(binding.ivPreview)

                    when {
                        item.paper.isSelected -> {
                            binding.btnAction.text = "Selected"
                            binding.btnAction.isEnabled = false
                            binding.btnAction.alpha = 0.5f
                        }

                        item.paper.isUnlocked -> {
                            binding.btnAction.text = "Select"
                            binding.btnAction.isEnabled = true
                            binding.btnAction.alpha = 1.0f
                        }

                        else -> {
                            binding.btnAction.text = "${item.paper.price} Coins"
                            binding.btnAction.isEnabled = true
                            binding.btnAction.alpha = 1.0f
                        }
                    }
                }
            }
        }
    }

    override fun onClickViews(binding: ViewDataBinding, obj: ShopItem, layoutPosition: Int) {
        when (obj) {
            is ShopItem.GetCoins -> {
                if (binding is ItemShopGetCoinsBinding) {
                    binding.btnWatchAd.setOnClickListener { listener.onWatchAdClick() }
                }
            }

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

            is ShopItem.Paper -> {
                if (binding is ItemShopPaperBinding) {
                    binding.btnAction.setOnClickListener {
                        if (!obj.paper.isSelected && obj.paper.isUnlocked) {
                            listener.onSelectPaperClick(obj.paper)
                        } else if (!obj.paper.isUnlocked) {
                            listener.onBuyPaperClick(obj.paper)
                        }
                    }
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ShopItem>() {
        override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
            if (oldItem is ShopItem.Paper && newItem is ShopItem.Paper) {
                return oldItem.paper.id == newItem.paper.id
            }
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean =
            oldItem == newItem
    }
}
