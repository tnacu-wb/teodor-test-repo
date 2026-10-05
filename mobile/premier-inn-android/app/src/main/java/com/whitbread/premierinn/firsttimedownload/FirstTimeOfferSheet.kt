package com.whitbread.premierinn.firsttimedownload

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.proximaNovaFontFamily
import com.whitbread.premierinn.common.ParcelablePromoContent
import com.whitbread.premierinn.common.ParcelableHomepageBanner
import com.whitbread.premierinn.common.ParcelableSrpBanner
import com.whitbread.premierinn.common.ParcelableTerms

@Composable
fun FirstTimeOfferSheet(
    promoContent: ParcelablePromoContent,
    onClose: () -> Unit,
    onTermsClick: () -> Unit
) {
    val purple = colorResource(id = R.color.premier_inn_purple)
    val lightPurple = colorResource(id = R.color.premier_inn_purple_variation_1)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val ovalWidth = width * 1.3f
            val ovalHeight = height / 6f  // Smaller height = flatter curve
            val ovalTop = height / 2.3f     // Vertical position of the curve

            drawRect(
                color = lightPurple,
                size = Size(width, ovalTop)
            )

            drawRect(
                color = purple,
                topLeft = Offset(0f, ovalTop),
                size = Size(width, height - ovalTop)
            )

            val path = Path().apply {
                addOval(
                    Rect(
                        offset = Offset((size.width - ovalWidth) / 2, ovalTop - ovalHeight / 2),
                        size = Size(ovalWidth, ovalHeight)
                    )
                )
            }

            clipPath(path, clipOp = ClipOp.Intersect) {
                drawRect(
                    color = purple,
                    topLeft = Offset(0f, ovalTop - ovalHeight / 2),
                    size = Size(ovalWidth, ovalHeight)
                )
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Close",
                tint = Color.Unspecified
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.height(130.dp),
                painter = painterResource(id = R.drawable.ic_moon_stars_large),
                contentDescription = "Main Icon",
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(113.dp))

            Text( // Title
                text = promoContent.homepageBanner.title,
                style = TextStyle(
                    fontSize = 26.sp,
                    color = Color.White,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text( // Date
                text = buildAnnotatedString {
                    append(promoContent.homepageBanner.datePrefixText)
                    append(" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(promoContent.homepageBanner.date)
                    }
                },
                style = TextStyle(
                    fontSize = 18.sp,
                    color = Color.White,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(5.dp))


            PulsingText(promoContent)

            Text( // Description
                text = promoContent.homepageBanner.offerDescription,
                style = TextStyle(
                    fontSize = 24.sp,
                    color = colorResource(R.color.moon_yellow),
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Black
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            GenericButton(
                onClick = { onClose() },
                color = Color.White,
                text = promoContent.homepageBanner.buttonText,
                textColor = colorResource(id = R.color.premier_inn_purple),
                modifier = Modifier
                    .width(329.dp)
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text( // Disclaimer
                text = promoContent.homepageBanner.disclaimer,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Normal
                )
            )

            Text( // Terms and conditions
                text = promoContent.homepageBanner.terms.text,
                modifier = Modifier.clickable(onClick = onTermsClick ),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Normal,
                    textDecoration = TextDecoration.Underline
                )
            )
        }
    }
}

@Composable
private fun PulsingText(promoContent: ParcelablePromoContent) {
    val bigTextSizeSp = 120.sp
    val bigTextSizeConvertedToDp = with(LocalDensity.current) { bigTextSizeSp.toDp() }

    var animateToTarget by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animateToTarget) 1.2f else 1f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutCubic),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        repeat(3) {
            animateToTarget = true
            kotlinx.coroutines.delay(600)
            animateToTarget = false
            kotlinx.coroutines.delay(600)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.scale(scale)
    ) {
        Text( // Discount Amount
            text = promoContent.homepageBanner.discountAmount,
            color = colorResource(R.color.moon_yellow),
            fontSize = 120.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.Black,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both
                )
            )

        )

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.height(bigTextSizeConvertedToDp)
        ) {
            Text( // Percent Sign
                text = promoContent.homepageBanner.discountPercentageSign,
                color = colorResource(R.color.moon_yellow),
                fontSize = 60.sp,
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.Black,
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, placeable.height / 16)
                    }
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text( // Discount Text
                text = promoContent.homepageBanner.discountText,
                color = colorResource(R.color.moon_yellow),
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                fontFamily = proximaNovaFontFamily,
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, -placeable.height / 4)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PulsingTextPreview() {
    PulsingText(
        promoContent = PreviewData.promoContent()
    )
}

@Preview(showBackground = true)
@Composable
fun FirstTimeOfferSheetPreview() {
    FirstTimeOfferSheet(
        promoContent = PreviewData.promoContent(),
        onClose = {},
        onTermsClick = {}
    )
}

object PreviewData {
    @Composable
    fun promoContent() = ParcelablePromoContent(
        homepageBanner = ParcelableHomepageBanner(
            title = stringResource(id = R.string.offer_sheet_title),
            datePrefixText = stringResource(id = R.string.offer_sheet_date_prefix_text),
            date = stringResource(id = R.string.offer_sheet_offer_date),
            discountAmount = stringResource(id = R.string.offer_sheet_discount_amount),
            discountPercentageSign = stringResource(id = R.string.offer_sheet_discount_percentage_sign),
            discountText = stringResource(id = R.string.offer_sheet_discount_text),
            offerDescription = stringResource(id = R.string.offer_sheet_offer_description),
            buttonText = stringResource(id = R.string.offer_sheet_button_text),
            disclaimer = stringResource(id = R.string.offer_sheet_terms_description),
            terms = ParcelableTerms(
                text = stringResource(id = R.string.offer_sheet_terms_and_conditions),
                url = ""
            )
        ),
        srpBanner = ParcelableSrpBanner(
            title = "",
            date = "",
            subTitle = "",
            terms = ParcelableTerms(
                text = "",
                url = ""
            )
        ),
    )
}
