package com.whitbread.premierinn.ciol

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.fragments.PayAndCheckInFragment
import com.whitbread.premierinn.ciol.fragments.PreStayEditItemFragment
import com.whitbread.premierinn.ciol.fragments.PreStayFragment
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.databinding.ActivityCheckInOnlineBinding
import com.whitbread.premierinn.postcodefinder.ParcelableAddress
import com.whitbread.premierinn.postcodefinder.PostcodeFinderActivity
import com.whitbread.premierinn.postcodefinder.toAddress
import dagger.hilt.android.AndroidEntryPoint

const val PRE_STAY_KEY = "preStayKey"
const val CHECK_IN_ONLINE_ACTIVITY_REQUEST_KEY = 10002

@AndroidEntryPoint
class CheckInOnlineActivity : BaseActivity<ActivityCheckInOnlineBinding>() {


    internal var preStayModel: PreStayUiModel? = null

    override fun inflateBinding(inflater: LayoutInflater): ActivityCheckInOnlineBinding {
        return ActivityCheckInOnlineBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_chevron_left_white
            )
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val model = intent.getParcelableExtra<PreStayUiModel>(PRE_STAY_KEY)

        savedInstanceState ?: run {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PreStayFragment.newInstance(model))
                .commit()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    when (val currentFragment =
                        supportFragmentManager.findFragmentById(R.id.container)) {
                        is PayAndCheckInFragment -> currentFragment.onBackPressed()
                        else -> supportFragmentManager.popBackStack()
                    }
                } else {
                    preStayModel?.let {
                        setResult(
                            RESULT_CANCELED,
                            Bundle().apply {
                                putParcelable(PRE_STAY_KEY, preStayModel)
                            }.let { bundle ->
                                Intent().putExtras(bundle)
                            }
                        )
                    }
                    finish()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when(requestCode) {
            PostcodeFinderActivity.POSTCODE_ADDRESS_RESULT_REQUEST_CODE -> {
                intent?.getParcelableExtra<ParcelableAddress>(PostcodeFinderActivity.SELECTED_POSTCODE_ADDRESS_KEY)?.let { postcodeAddressSelected ->
                    val fragment = supportFragmentManager.findFragmentById(R.id.container)
                    when {
                        fragment is PayAndCheckInFragment && fragment.isVisible -> {
                            fragment.onBillingAddressGenerated(postcodeAddressSelected.toAddress())
                        }
                        fragment is PreStayEditItemFragment && fragment.isVisible -> {
                            fragment.onPostcodeAddressSelected(postcodeAddressSelected)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        supportFragmentManager.findFragmentById(R.id.container).let { fragment ->
            if (fragment is PayAndCheckInFragment) {
                fragment.handleOnNewIntent(intent)
            }
        }
    }

    fun setupToolbarTitle(title: String) {
        binding.toolbarTitle.text = title
    }

    fun setOnToolbarIconClickListener(action: (() -> Unit)?) {
        binding.toolbarIcon.setOnClickListener {
            action?.invoke()
        }
    }

    fun changeToolbarIconVisibility(isVisible: Boolean) {
        binding.toolbarIcon.isVisible = isVisible
    }
}
