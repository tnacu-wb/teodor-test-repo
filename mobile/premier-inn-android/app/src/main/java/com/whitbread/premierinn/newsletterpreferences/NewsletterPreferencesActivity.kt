package com.whitbread.premierinn.newsletterpreferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.NEWSLETTER_UPDATES
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.MY_PREMIER_INN
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.databinding.ActivityNewsletterPreferencesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewsletterPreferencesActivity: BaseActivity<ActivityNewsletterPreferencesBinding>() {

    @Inject lateinit var analytics: TrackingAnalytics

    override fun inflateBinding(inflater: LayoutInflater): ActivityNewsletterPreferencesBinding {
        return ActivityNewsletterPreferencesBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(getString(R.string.my_account_newsletters), true)

        val userEmailAddress = intent.getStringExtra(EMAIL_INPUT_KEY)
        binding.newsletterPrefsEmail.text = userEmailAddress

        analytics.track(NEWSLETTER_UPDATES, MY_PREMIER_INN)
    }

    companion object {
        const val EMAIL_INPUT_KEY = "email"

        @JvmStatic
        fun createIntent(context: Context, emailAddress: String): Intent {
            val intent = Intent(context, NewsletterPreferencesActivity::class.java)
            intent.putExtra(EMAIL_INPUT_KEY, emailAddress)
            return intent
        }
    }

}