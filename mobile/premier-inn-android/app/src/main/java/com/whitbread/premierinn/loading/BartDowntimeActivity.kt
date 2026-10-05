package com.whitbread.premierinn.loading

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.data.common.toBartDownInfoDomain
import com.whitbread.premierinn.data.remote.BartDownInfo
import com.whitbread.premierinn.databinding.ActivityBartDowntimeBinding
import com.whitbread.premierinn.domain.common.BartDownInfoDomain
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BartDowntimeActivity : BaseActivity<ActivityBartDowntimeBinding>() {
    private var bartDownInfoDomain: BartDownInfoDomain? = null

    @Inject lateinit var getStringResource: GetStringResource

    override fun inflateBinding(inflater: LayoutInflater): ActivityBartDowntimeBinding {
        return ActivityBartDowntimeBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bartDownInfoDomain = retrieveBartDownTimeInfoFromFirebase(getStringResource)

        bartDownInfoDomain?.let {
            binding.bartDownTitle.text =  it.bartDowntimeTitle
            binding.bartDownDescription.text = it.bartDowntimeDescription
            binding.bartDownMakeBooking.text = it.bartDowntimeMakeBooking
            binding.bartDownExistingBooking.text = it.bartDowntimeExistingBooking
        }
    }

    private fun retrieveBartDownTimeInfoFromFirebase(getStringResource: GetStringResource): BartDownInfoDomain {
        return Gson().fromJson(
            getStringResource.invoke(ContentManagedResourceRepository.Key.BART_DOWNTIME_INFO),
            BartDownInfo::class.java
        ).toBartDownInfoDomain()
    }

    override fun attachInternetInfoView(): Boolean {
        return false
    }
    
    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, BartDowntimeActivity::class.java)
        }
    }
}