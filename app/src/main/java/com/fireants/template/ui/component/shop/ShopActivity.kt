package com.fireants.template.ui.component.shop

import androidx.activity.viewModels
import com.fireants.template.R
import com.fireants.template.databinding.ActivityShopBinding
import com.fireants.template.ui.bases.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopActivity : BaseActivity<ActivityShopBinding>() {

    private val viewModel: ShopViewModel by viewModels()

    override fun getLayoutActivity(): Int {
        return R.layout.activity_shop
    }

}