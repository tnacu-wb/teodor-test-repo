package com.whitbread.premierinn.firsttimedownload

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.activity.BaseComponentActivity
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.compose.ui.components.ButtonTeal
import com.whitbread.premierinn.compose.ui.components.ButtonType
import com.whitbread.premierinn.compose.ui.components.DarkGrey
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.HorizontalSpace
import com.whitbread.premierinn.compose.ui.components.LightGrey
import com.whitbread.premierinn.compose.ui.theme.PremierInnHolBornAndroidTheme
import com.whitbread.premierinn.compose.ui.components.Purple
import com.whitbread.premierinn.compose.ui.components.TextBody
import com.whitbread.premierinn.compose.ui.components.TextBodySmall
import com.whitbread.premierinn.compose.ui.components.TextHeading
import com.whitbread.premierinn.compose.ui.components.VerticalSpace
import com.whitbread.premierinn.createaccount.CreateAccountActivity
import com.whitbread.premierinn.gdpr.Spacing
import com.whitbread.premierinn.loading.LoadingActivity
import com.whitbread.premierinn.login.LoginActivity
import com.whitbread.premierinn.login.Screen
import com.whitbread.premierinn.login.ScreenType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstTimeDownloadActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PremierInnHolBornAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FirstTimeDownLoadScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }

    @Composable
    fun FirstTimeDownLoadScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current

        val navigateToLoading = {
            this.startActivity(Intent(context, LoadingActivity::class.java))
            this.finish()
        }

        val createAccountLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                navigateToLoading()
            }
        }

        val loginLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                navigateToLoading()
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { navigateToLoading() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cross_gray),
                        tint = LightGrey,
                        contentDescription = stringResource(id = R.string.cancel_dialog)
                    )
                }
            }

            Box(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.Companion
                        .align(Alignment.BottomStart)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    TextHeading(
                        text = stringResource(id = R.string.from_booking_to_bed), color = Purple
                    )
                    VerticalSpace(height = 15.dp)
                    TextBody(
                        text = stringResource(id = R.string.create_account_header), color = DarkGrey
                    )
                    VerticalSpace(height = 20.dp)
                    SubHeaderWithTickMark(text = stringResource(id = R.string.track_booking_history))
                    VerticalSpace(height = 20.dp)
                    SubHeaderWithTickMark(text = stringResource(id = R.string.amend_your_bookings))
                    VerticalSpace(height = 20.dp)
                    SubHeaderWithTickMark(text = stringResource(id = R.string.save_time_on_bookings))
                    VerticalSpace(height = 30.dp)
                    GenericButton(
                        text = stringResource(id = R.string.create_account_button_text),
                        color = ButtonTeal,
                        buttonType = ButtonType.Filled,
                        onClick = {
                            createAccountLauncher.launch(
                                Intent(context, CreateAccountActivity::class.java)
                            )
                        }
                    )
                    VerticalSpace(height = 20.dp)
                    GenericButton(
                        text = stringResource(id = R.string.log_in),
                        color = Purple,
                        buttonType = ButtonType.Outlined,
                        onClick = {
                            loginLauncher.launch(
                                LoginActivity.createIntent(
                                    context,
                                    Screen(
                                        ScreenType.ACCOUNT_LOGIN.name,
                                        AnalyticsConstants.ScreenState.LOG_IN
                                    )
                                )
                            )
                        }
                    )
                    VerticalSpace(height = 20.dp)
                    TextBodySmall(
                        text = stringResource(id = R.string.continue_without_login),
                        color = Purple,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable { navigateToLoading() }
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    @Composable
    fun SubHeaderWithTickMark(text: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.align(Alignment.Top),
                painter = painterResource(id = R.drawable.baseline_check_24),
                contentDescription = text,
                tint = Purple
            )
            HorizontalSpace(Spacing.tenDp)
            TextBody(text = text, color = DarkGrey)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun FirstTimeDownLoadScreenPreview() {
        FirstTimeDownLoadScreen()
    }

    @Preview(showBackground = true)
    @Composable
    fun SubHeaderWithTickMarkPreview() {
        SubHeaderWithTickMark("")
    }
}