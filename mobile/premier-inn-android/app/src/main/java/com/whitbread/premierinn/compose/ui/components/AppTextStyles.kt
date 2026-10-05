package com.whitbread.premierinn.compose.ui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R

val proximaNovaFontFamily = FontFamily(
    Font(R.font.proxima_nova_regular, FontWeight.Normal),
    Font(R.font.proxima_nova_bold, FontWeight.Bold),
    Font(R.font.proxima_nova_semibold, FontWeight.SemiBold),
    Font(R.font.proxima_nova_light, FontWeight.Light),
    Font(R.font.proxima_nova_extra_bold, FontWeight.ExtraBold),
    Font(R.font.proxima_nova_medium, FontWeight.Medium),
)

object AppTextStyles {
    val heading = TextStyle(
        fontFamily = proximaNovaFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 1.em
    )

    val body = TextStyle(
        fontSize = 18.sp,
        fontFamily = proximaNovaFontFamily,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    )

    val bodySmall = TextStyle(
        fontSize = 16.sp,
        fontFamily = proximaNovaFontFamily,
        fontWeight = FontWeight.Normal,
    )

    val smallBlack = TextStyle(
        color = Black,
        fontSize = 16.sp,
        fontFamily = proximaNovaFontFamily,
        fontWeight = FontWeight.Normal,
    )
}