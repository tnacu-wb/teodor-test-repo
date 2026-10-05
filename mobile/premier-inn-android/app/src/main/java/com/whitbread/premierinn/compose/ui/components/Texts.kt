package com.whitbread.premierinn.compose.ui.components

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R

@Composable
fun TextHeading(text: String, modifier: Modifier = Modifier, color: Color) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        style = AppTextStyles.heading
    )
}

@Composable
fun TextBody(text: String, color: Color, modifier: Modifier = Modifier, maxLines: Int = Int.MAX_VALUE) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        style = AppTextStyles.body,
        maxLines = maxLines
    )
}

@Composable
fun TextBodySmall(text: String, modifier: Modifier = Modifier, color: Color) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        style = AppTextStyles.bodySmall
    )
}

@Composable
fun TextWithProximaNovaSemiBold(text: String, color: Color = Color.Black) {
    Text(
        color = color,
        text = text,
        modifier = Modifier.offset(y = (-2).dp),
        style = TextStyle(
            fontSize = ButtonDimensions.textSize,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.SemiBold,
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
fun AppBarTextWithProximaNovaSemiBold(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 20.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.SemiBold,
        ), textAlign = TextAlign.Center
    )
}

@Preview(showBackground = true)
@Composable
fun TextHeadingPreview() {
    TextHeading(text = stringResource(id = R.string.gdpr_your_privacy), color = Purple)
}

@Preview(showBackground = true)
@Composable
fun TextBodyPreview() {
    TextBody(text = stringResource(id = R.string.gdpr_your_privacy_message), color = DarkGrey)
}

@Preview(showBackground = true)
@Composable
fun TextSmallPurplePreview() {
    TextBodySmall(text = stringResource(id = R.string.my_account_gdpr), color = Color.Black)
}

@Preview(showBackground = true)
@Composable
fun ButtonTextPreview() {
    TextWithProximaNovaSemiBold(text = stringResource(id = R.string.my_account_gdpr))
}
