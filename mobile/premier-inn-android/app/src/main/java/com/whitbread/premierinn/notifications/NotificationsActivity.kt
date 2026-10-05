package com.whitbread.premierinn.notifications

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.databinding.ActivityNotificationsBinding
import com.whitbread.premierinn.landing.LandingActivityIntent.create
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsActivity : BaseActivity<ActivityNotificationsBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ActivityNotificationsBinding {
        return ActivityNotificationsBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationsInfo = intent.getParcelableExtra<NotificationsInfo>(EXTRA_NOTIFICATION_INPUT)
        val isDeeplinkIntent = intent.getBooleanExtra(EXTRA_IS_DEEPLINK_INTENT, false)

        binding.notificationTitle.text = notificationsInfo?.notificationTitle
        binding.notificationBody.text = notificationsInfo?.notificationBody
        binding.notificationAcceptButtonText.setText(notificationsInfo?.notificationCTA)

        binding.notificationAcceptButtonText.setOnClickListener {
            returnResult(isDeeplinkIntent)
        }

        binding.notificationProgress.hide()

        showContent()
    }

    private fun returnResult(isDeeplinkIntent: Boolean) {
        if (isDeeplinkIntent) {
            startActivity(create(this))
        } else {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }

    private fun showContent() {
        binding.notificationTitle.visibility = View.VISIBLE
        binding.notificationBody.visibility = View.VISIBLE
        binding.notificationAcceptButtonText.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val EXTRA_NOTIFICATION_INPUT = "notification_input"
        private const val EXTRA_IS_DEEPLINK_INTENT = "is_deeplink_intent"

        @JvmStatic
        fun createIntent(
            context: Context,
            input: NotificationsInfo,
            isDeeplinkIntent: Boolean = false
        ): Intent {
            return Intent(context, NotificationsActivity::class.java).apply {
                putExtra(EXTRA_NOTIFICATION_INPUT, input)
                putExtra(EXTRA_IS_DEEPLINK_INTENT, isDeeplinkIntent)
            }
        }
    }
}
