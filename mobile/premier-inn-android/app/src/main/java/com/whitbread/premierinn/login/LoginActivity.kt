package com.whitbread.premierinn.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.viewbinding.ViewBinding
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.accountlogin.AccountLoginViewContainer
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.databinding.ActivityAccountLoginBinding
import com.whitbread.premierinn.databinding.ActivityLoginBinding
import com.whitbread.premierinn.resetpassword.ResetPasswordActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ViewBinding?>() {
    @Inject
    lateinit var presenter: LoginPresenter
    private lateinit var resetPasswordRelay: Relay<String>
    private var screen: Screen? = null
    private var activityLoginBinding: ActivityLoginBinding? = null
    private var activityAccountLoginBinding: ActivityAccountLoginBinding? = null

    override fun inflateBinding(inflater: LayoutInflater): ViewBinding =
        if (screen?.screen == ScreenType.BOOKING_FLOW_LOGIN.name) {
            activityLoginBinding = ActivityLoginBinding.inflate(inflater)
            activityLoginBinding!!
        } else {
            activityAccountLoginBinding = ActivityAccountLoginBinding.inflate(inflater)
            activityAccountLoginBinding!!
        }

    override fun getToolbar(): Toolbar {
        return if (activityLoginBinding != null) {
            activityLoginBinding?.toolbar!!
        } else {
            activityAccountLoginBinding!!.toolbar
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenParcelable = intent.getParcelableExtra<ScreenParcelable?>(
            SCREEN_TYPE_VALUE_INTENT
        )
        screenParcelable?.let {
            screen = Screen(screenParcelable.screen, screenParcelable.trackingScreen)
        }
        super.onCreate(savedInstanceState)
        presenter.setScreenState(screen?.trackingScreen ?: "")
        resetPasswordRelay = PublishRelay.create()
        setToolbar(getString(R.string.log_in), true)
        presenter.attachView(if (screen?.screen == ScreenType.BOOKING_FLOW_LOGIN.name)
                LoginViewContainer(this, resetPasswordRelay, activityLoginBinding)
            else
                AccountLoginViewContainer(this, resetPasswordRelay, activityAccountLoginBinding)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_privacy_policy_badge, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == ResetPasswordActivity.RESET_PASSWORD_RESULT_REQUEST_CODE && resultCode == RESULT_OK) {
            val email = intent?.getStringExtra(ResetPasswordActivity.RESULT_INTENT_EMAIL_KEY)
            if (email != null) {
                resetPasswordRelay.accept(email)
            }
        }
    }

    companion object {
        const val ACTIVITY_RESULT_REQUEST_CODE: Int = 346
        private const val SCREEN_TYPE_VALUE_INTENT = "screen_type"
        @JvmStatic
        fun createIntent(context: Context, screen: Screen): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(
                SCREEN_TYPE_VALUE_INTENT,
                ScreenParcelable(screen.screen, screen.trackingScreen)
            )
            return intent
        }
    }
}
