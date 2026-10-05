package com.whitbread.premierinn.compose.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R

@Composable
fun InfoMessageBox(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int? = null,
    iconTintColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color = Color.Black,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        iconRes?.let { icon ->
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTintColor
            )
        }

        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.Normal,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun InfoMessageBoxBluePreview() {
    InfoMessageBox(
        text = "We're sorry, the restaurant at this hotel is closed on your selected dates.",
        iconRes = R.drawable.ic_info,
        iconTintColor = InfoBlue,
        backgroundColor = InfoBlueTint,
        borderColor = InfoBlue40,
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun InfoMessageBoxWarningPreview() {
    InfoMessageBox(
        text = "We're sorry, the restaurant at this hotel is closed on your selected dates.",
        iconRes = R.drawable.notifications_alert,
        iconTintColor = Red,
        backgroundColor = OrangeTint,
        borderColor = Red,
        modifier = Modifier.padding(16.dp)
    )
}