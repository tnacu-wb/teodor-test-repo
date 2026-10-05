package com.whitbread.premierinn.threeCp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT
import com.whitbread.premierinn.common.EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.databinding.ActivityThreeCpBinding
import com.whitbread.premierinn.utils.openUrlWithFallback
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

const val EXTRA_GPAY_RESP = "EXTRA_GPAY_RESP"

@AndroidEntryPoint
class ThreeCpCustomTabActivity : BaseActivity<ActivityThreeCpBinding>() {

    private lateinit var url: String
    private lateinit var ipageSessionId: String
    private var hasBeenRestarted = false

    override fun inflateBinding(inflater: LayoutInflater): ActivityThreeCpBinding {
        return ActivityThreeCpBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }


    companion object {
        @JvmStatic
        fun Context.createThreeCpIntentForGPay(input: ThreeCpInput): Intent {
            return Intent(this, ThreeCpCustomTabActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT, input)
            }
        }

        fun createThreeCpPayAndCheckInIntentForGPay(context: Context, input: ThreeCpGooglePayInput): Intent {
            return Intent(context, ThreeCpCustomTabActivity::class.java).apply {
                putExtra(EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY, input)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        retrieveIntentExtras()

        if (!isFinishing) {
            launchCustomTab()
        }

        if(savedInstanceState == null) {
            hasBeenRestarted = false
        }

    }

    override fun onRestart() {
        super.onRestart()
        hasBeenRestarted = true
    }

    override fun onResume() {
        super.onResume()
        if (hasBeenRestarted) {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setResult(RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_GPAY_RESP, intent?.data?.queryParameterNames?.mapNotNull { "$it=\"${intent.data?.getQueryParameter(it)}\"" }?.joinToString("\n\n"))
            })
        finish()

    }

    private fun retrieveIntentExtras() {
        val intentModel = intent.extras?.let { extras ->
            if (extras.containsKey(EXTRA_THREE_CP_INPUT)) {
                extras.get(EXTRA_THREE_CP_INPUT) as? ThreeCpInput ?: error("Intent Argument $EXTRA_THREE_CP_INPUT is missing")
            } else if (extras.containsKey(EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY)) {
                extras.get(EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY) as? ThreeCpGooglePayInput
                    ?: error("Intent Argument $EXTRA_THREE_CP_INPUT_PAY_AND_CHECK_IN_G_PAY is missing")
            } else {
                null
            }
        }

        when (intentModel) {
            is ThreeCpInput -> initialiseComponents(intentModel.iPageSessionIdForGPay, intentModel.providerUrl)
            is ThreeCpGooglePayInput -> initialiseComponents(intentModel.iPageSessionIdForGPay, intentModel.providerUrl)
            null -> {
                Toast.makeText(this, getString(R.string.review_booking_error_title), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun initialiseComponents(iPageSessionIdForGPay: String, providerUrl: String) {
        ipageSessionId = URLEncoder.encode(iPageSessionIdForGPay, "UTF-8")
        val baseurl = "$providerUrl/iPage/Service/_2006_05_v1_0_1/service.aspx?XXX_IPGSESSION_XXX="
        url = baseurl + ipageSessionId
    }

    private fun launchCustomTab() {
        openUrlWithFallback(this, url)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.title = getString(R.string.ipage_toolbar_title)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }
}