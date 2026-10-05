package com.whitbread.premierinn.hoteldetails.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R

private object DiscountCodeSectionConstants {
    val SECTION_MARGIN_HORIZONTAL = 16.dp
    val ICON_BOX_WIDTH = 32.dp
    val ICON_SIZE = 24.dp
    val ICON_PADDING = 0.dp
    val FONT_WEIGHT = FontWeight.Normal
    val FONT_FAMILY = FontFamily(Font(R.font.proxima_nova_regular))
    val TEXT_COLOR = Color(0xFF333333)
    val TEXT_SIZE = 16.sp
}

@Composable
fun DiscountCodeSection(
    hasAppliedCode: Boolean = false,
    isLoading: Boolean = false,
    onDiscountCodeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = DiscountCodeSectionConstants.SECTION_MARGIN_HORIZONTAL,
                end = DiscountCodeSectionConstants.SECTION_MARGIN_HORIZONTAL
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !isLoading,
                role = Role.Button,
                onClickLabel = if (hasAppliedCode) {
                    "Manage discount codes"
                } else {
                    "Add discount code"
                }
            ) {
                onDiscountCodeClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.width(DiscountCodeSectionConstants.ICON_BOX_WIDTH),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(DiscountCodeSectionConstants.ICON_SIZE),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tag),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(DiscountCodeSectionConstants.ICON_PADDING)
                        .size(DiscountCodeSectionConstants.ICON_SIZE),
                    tint = colorResource(id = R.color.premier_inn_purple)
                )
            }
        }

        Text(
            text = getDiscountCodeText(hasAppliedCode),
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = DiscountCodeSectionConstants.FONT_WEIGHT,
                color = DiscountCodeSectionConstants.TEXT_COLOR,
                fontFamily = DiscountCodeSectionConstants.FONT_FAMILY,
                fontSize = DiscountCodeSectionConstants.TEXT_SIZE
            )
        )

        if (!isLoading) {
            Icon(
                painter = painterResource(id = R.drawable.ic_right_arrow_dark_grey),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun getDiscountCodeText(
    hasAppliedCode: Boolean
): String {
    return when {
        hasAppliedCode -> stringResource(id = R.string.hotel_details_add_discount_code, 1)
        else -> stringResource(id = R.string.discount_code_link_text)
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscountCodeSectionPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("No discount code:", style = MaterialTheme.typography.labelMedium)
            DiscountCodeSection(
                hasAppliedCode = false
            )

            Text("With discount code:", style = MaterialTheme.typography.labelMedium)
            DiscountCodeSection(
                hasAppliedCode = true
            )

            Text("Loading state:", style = MaterialTheme.typography.labelMedium)
            DiscountCodeSection(
                hasAppliedCode = false,
                isLoading = true
            )
        }
    }
}