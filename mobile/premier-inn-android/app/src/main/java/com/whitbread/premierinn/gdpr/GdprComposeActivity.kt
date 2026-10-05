package com.whitbread.premierinn.gdpr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import com.whitbread.premierinn.compose.ui.components.ButtonTeal
import com.whitbread.premierinn.compose.ui.components.DarkGrey
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.HorizontalSpace
import com.whitbread.premierinn.compose.ui.components.Purple
import com.whitbread.premierinn.compose.ui.components.TextBody
import com.whitbread.premierinn.compose.ui.components.TextBodySmall
import com.whitbread.premierinn.compose.ui.components.TextHeading
import com.whitbread.premierinn.compose.ui.components.VerticalSpace
import com.whitbread.premierinn.compose.ui.theme.PremierInnHolBornAndroidTheme
import com.whitbread.premierinn.firsttimedownload.FirstTimeDownloadActivity
import com.whitbread.premierinn.utils.openUrlWithFallback
import dagger.hilt.android.AndroidEntryPoint

//TODO: Rename this class
@AndroidEntryPoint
class GdprComposeActivity : BaseComponentActivity() {

    val viewModel: GdprComposeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PremierInnHolBornAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PrivacyScreen(onAcceptPrivacyPolicy = viewModel::onAcceptPrivacyPolicyClicked, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PrivacyScreen(onAcceptPrivacyPolicy: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val privacyPolicyUrl = stringResource(R.string.privacy_policy_web_url)
    val toolbarColorResId = R.color.premier_inn_purple

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(vertical = 40.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.weight(1f))
        TextHeading(text = stringResource(R.string.gdpr_your_privacy), color = Purple)
        VerticalSpace(Spacing.medium)
        TextBody(text = stringResource(R.string.gdpr_your_privacy_message), color = DarkGrey)
        VerticalSpace(Spacing.extraLarge)
        PrivacyOption(icon = R.drawable.ic_technologies, text = stringResource(R.string.my_account_gdpr)) {  launchGdprDataUseActivity(context) }
        VerticalSpace(Spacing.small)
        PrivacyOption(icon = R.drawable.outline_lock_24, text = stringResource(R.string.my_account_privacy_policy)) {
            openUrlWithFallback(context, privacyPolicyUrl)
        }
        VerticalSpace(Spacing.extraLarge)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            GenericButton(text = stringResource(id = R.string.gdpr_accept_button_text), color = ButtonTeal, onClick = {
                onAcceptPrivacyPolicy()
                launchFirstTimeDownloadActivity(context)
            })
        }
    }
}

fun launchGdprDataUseActivity(context: Context) {
    val intent = GdprDataUseActivity.createIntent(context)
    context.startActivity(intent)
}

fun launchFirstTimeDownloadActivity(context: Context){
    val intent = Intent(context, FirstTimeDownloadActivity::class.java)
    context.startActivity(intent)
    (context as? GdprComposeActivity)?.finish()
}

@Composable
fun PrivacyOption(icon: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = icon), contentDescription = text, tint = Purple, modifier = Modifier.size(30.dp))
            HorizontalSpace(Spacing.small)
            TextBodySmall(text = text, color = DarkGrey)
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow) ,
            contentDescription = "Arrow",
            tint = Color.Gray
        )
    }
}

object Spacing {
    val small = 8.dp
    val tenDp = 10.dp
    val medium = 12.dp
    val extraLarge = 32.dp
}

@Preview(showBackground = true)
@Composable
fun PrivacyScreenPreview() {
    PremierInnHolBornAndroidTheme {
        PrivacyScreen(onAcceptPrivacyPolicy = {})
    }
}

fun createIntent(context: Context): Intent {
    return Intent(context, GdprComposeActivity::class.java)
}