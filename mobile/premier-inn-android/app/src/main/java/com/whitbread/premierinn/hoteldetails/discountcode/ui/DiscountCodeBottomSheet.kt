package com.whitbread.premierinn.hoteldetails.discountcode.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.compose.ui.components.ButtonTeal
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeAction
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeBottomSheetState
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.CORNER_RADIUS
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.FONT_FAMILY
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.HEADER_COLOR
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.HEADER_FONT_FAMILY
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.HEADER_FONT_SIZE
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.HEADER_FONT_WEIGHT
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.HEADER_LINE_HEIGHT
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.INPUT_FONT_SIZE
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.INPUT_FONT_WEIGHT
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.INPUT_LINE_HEIGHT
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.MESSAGE_FONT_SIZE
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.MESSAGE_FONT_WEIGHT
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.MESSAGE_LINE_HEIGHT
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.PADDING_HORIZONTAL
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.PADDING_VERTICAL
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.SPACING_LARGE
import com.whitbread.premierinn.hoteldetails.discountcode.ui.DiscountCodeBottomSheetConstants.SPACING_MEDIUM

private object DiscountCodeBottomSheetConstants {
    val CORNER_RADIUS = 16.dp
    val PADDING_HORIZONTAL = 24.dp
    val PADDING_VERTICAL = 10.dp
    val SPACING_LARGE = 24.dp
    val SPACING_MEDIUM = 16.dp

    val FONT_FAMILY = FontFamily(Font(R.font.proxima_nova_medium))

    // Header/Button style
    val HEADER_FONT_SIZE = 18.sp
    val HEADER_LINE_HEIGHT = 21.6.sp
    val HEADER_FONT_WEIGHT = FontWeight.SemiBold
    val HEADER_FONT_FAMILY = FontFamily(Font(R.font.proxima_nova_semibold))
    val HEADER_COLOR = Color(0xFF000000)

    // Label/Input
    val INPUT_FONT_SIZE = 18.sp
    val INPUT_LINE_HEIGHT = 24.sp
    val INPUT_FONT_WEIGHT = FontWeight.Medium

    // Success/Error
    val MESSAGE_FONT_SIZE = 16.sp
    val MESSAGE_LINE_HEIGHT = 24.sp
    val MESSAGE_FONT_WEIGHT = FontWeight.Normal

    // Tag
    val TAG_FONT_SIZE = 14.sp
    val TAG_LINE_HEIGHT = 20.sp
    val TAG_FONT_WEIGHT = FontWeight.Bold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountCodeBottomSheetContent(
    state: DiscountCodeBottomSheetState,
    onAction: (DiscountCodeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
        shape = RoundedCornerShape(
            topStart = CORNER_RADIUS,
            topEnd = CORNER_RADIUS
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 84.dp)
            ) {
                HeaderSection(onAction)
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color(0xFFC6C6C8)
                )
                Spacer(modifier = Modifier.height(PADDING_VERTICAL))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = PADDING_HORIZONTAL,
                            vertical = 0.dp
                        )
                ) {
                    Spacer(modifier = Modifier.height(SPACING_LARGE))

                    InputFieldSection(
                        discountCode = state.discountCode,
                        appliedPromoCode = if (state.isSuccess) state.appliedPromoCode else null,
                        isLoading = state.isLoading,
                        isSuccess = state.isSuccess,
                        errorType = state.errorType,
                        errorMessage = state.errorMessage,
                        successMessage = state.successMessage,
                        onCodeChange = { code ->
                            onAction(DiscountCodeAction.UpdateDiscountCode(code.uppercase()))
                        },
                        onApply = {
                            keyboardController?.hide()
                            if (state.discountCode.isNotEmpty()) {
                                onAction(DiscountCodeAction.ApplyDiscountCode(state.discountCode))
                            }
                        },
                        onRemoveDiscount = { code ->
                            onAction(DiscountCodeAction.RemoveDiscountCode(code))
                        }
                    )

                    Spacer(modifier = Modifier.height(SPACING_MEDIUM))

                    if (!state.appliedPromoCode.isNullOrEmpty()) {
                        HotelDiscountTag(
                            label = state.appliedPromoCode,
                            onRemove = { onAction(DiscountCodeAction.RemoveDiscountCode(state.appliedPromoCode)) }
                        )
                        Spacer(modifier = Modifier.height(SPACING_MEDIUM))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = PADDING_HORIZONTAL, vertical = 16.dp)
            ) {
                ActionButton(
                    isSuccess = state.isSuccess,
                    isLoading = state.isLoading,
                    discountCode = state.discountCode,
                    onApply = {
                        keyboardController?.hide()
                        if (state.discountCode.isNotEmpty()) {
                            onAction(DiscountCodeAction.ApplyDiscountCode(state.discountCode))
                        }
                    },
                    onContinue = {
                        onAction(DiscountCodeAction.Continue)
                    }
                )
            }
        }
    }
}

@Composable
private fun InputFieldSection(
    discountCode: String,
    appliedPromoCode: String?,
    isLoading: Boolean,
    isSuccess: Boolean,
    errorType: DiscountCodeAction.DiscountAppliedError.ErrorType?,
    errorMessage: String?,
    successMessage: String?,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit,
    onRemoveDiscount: (String) -> Unit
) {
    Column {
        TextField(
            value = discountCode,
            onValueChange = onCodeChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            label = {
                Text(
                    text = stringResource(R.string.discount_code_hint),
                    style = TextStyle(
                        fontSize = INPUT_FONT_SIZE,
                        lineHeight = INPUT_LINE_HEIGHT,
                        fontFamily = FONT_FAMILY,
                        fontWeight = INPUT_FONT_WEIGHT,
                    )
                )
            },
            placeholder = null,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onApply() }),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = INPUT_FONT_SIZE,
                fontWeight = FontWeight.Normal,
                fontFamily = FONT_FAMILY
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = if (errorType != null) Color(0xFFC62828) else colorResource(
                    id = R.color.premier_inn_purple
                ),
                unfocusedIndicatorColor = if (errorType != null) Color(0xFFC62828) else Color(
                    0xFFBDBDBD
                ),
                disabledIndicatorColor = Color(0xFFE0E0E0),
                focusedTextColor = Color(0xFF212121),
                unfocusedTextColor = Color(0xFF212121),
                cursorColor = if (errorType != null) Color(0xFFC62828) else colorResource(id = R.color.premier_inn_purple),
                focusedLabelColor = colorResource(id = R.color.premier_inn_purple),
                unfocusedLabelColor = Color(0xFF757575)
            )
        )

        if ((isSuccess && appliedPromoCode != null) || errorType != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                if (isSuccess && appliedPromoCode != null) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tick_circle),
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = successMessage.orEmpty(),
                        color = Color(0xFF4CAF50),
                        style = TextStyle(
                            fontSize = MESSAGE_FONT_SIZE,
                            lineHeight = MESSAGE_LINE_HEIGHT,
                            fontFamily = FONT_FAMILY,
                            fontWeight = MESSAGE_FONT_WEIGHT
                        )
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = null,
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = errorMessage ?: when (errorType) {
                            DiscountCodeAction.DiscountAppliedError.ErrorType.MULTIPLE_REDEEM ->
                                stringResource(R.string.discount_code_multiple_redeem_error)
                            else -> stringResource(R.string.discount_code_general_error)
                        },
                        color = Color(0xFFC62828),
                        style = TextStyle(
                            fontSize = MESSAGE_FONT_SIZE,
                            lineHeight = MESSAGE_LINE_HEIGHT,
                            fontFamily = FONT_FAMILY,
                            fontWeight = MESSAGE_FONT_WEIGHT
                        )
                    )
                    // Show remove button if there's an applied code
                    if (!appliedPromoCode.isNullOrEmpty()) {
                        IconButton(
                            onClick = { onRemoveDiscount(appliedPromoCode) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                                contentDescription = "Remove discount",
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun HeaderSection(onAction: (DiscountCodeAction) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PADDING_VERTICAL),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.discount_code_title),
            style = TextStyle(
                fontSize = HEADER_FONT_SIZE,
                lineHeight = HEADER_LINE_HEIGHT,
                fontFamily = HEADER_FONT_FAMILY,
                fontWeight = HEADER_FONT_WEIGHT,
                color = HEADER_COLOR,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )

        IconButton(
            onClick = { onAction(DiscountCodeAction.Close) },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    isSuccess: Boolean,
    isLoading: Boolean,
    discountCode: String,
    onApply: () -> Unit,
    onContinue: () -> Unit
) {
    val buttonText = when {
        isLoading -> EMPTY_STRING
        isSuccess -> stringResource(R.string.discount_code_continue_button)
        else -> stringResource(R.string.discount_code_apply_button)
    }
    val onClick = if (isSuccess && discountCode.isEmpty()) onContinue else onApply
    val enabled =
        if (isSuccess && discountCode.isEmpty()) true else (!isLoading && discountCode.isNotBlank())

    Box(modifier = Modifier.fillMaxWidth()) {
        GenericButton(
            text = buttonText,
            color = ButtonTeal,
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
fun HotelDiscountTag(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                start = 8.dp,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_tag),
                contentDescription = null,
                tint = colorResource(id = R.color.teal_dark),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.proxima_nova_bold)),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.teal_dark)
                )
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = "Remove discount",
                    tint = colorResource(id = R.color.teal_dark),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// Preview functions
@Preview(showBackground = true, name = "Input State")
@Composable
fun DiscountCodeInputPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "",
                isLoading = false
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun DiscountCodeSuccessPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "",
                isSuccess = true,
                appliedPromoCode = "SAVE20",
                successMessage = "Promo code applied"
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Invalid Code Error - Backend Message")
@Composable
fun DiscountCodeInvalidErrorPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "INVALID",
                errorType = DiscountCodeAction.DiscountAppliedError.ErrorType.INVALID,
                errorMessage = "Invalid code, please check and try again"
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Code Already Applied - Backend Message")
@Composable
fun DiscountCodeAlreadyAppliedPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "APPS7SSS6F",
                errorType = DiscountCodeAction.DiscountAppliedError.ErrorType.CODE_ALREADY_APPLIED,
                errorMessage = "Promo code already applied"
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Multiple Codes Error")
@Composable
fun DiscountCodeMultipleRedeemPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "NEWCODE",
                appliedPromoCode = "SAVE20",
                errorType = DiscountCodeAction.DiscountAppliedError.ErrorType.MULTIPLE_REDEEM,
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Code Expired - Backend Message")
@Composable
fun DiscountCodeExpiredPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "EXPIRED",
                errorType = DiscountCodeAction.DiscountAppliedError.ErrorType.INVALID,
                errorMessage = "The promo code has expired"
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Code Unavailable - Backend Message")
@Composable
fun DiscountCodeUnavailablePreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "UNAVAIL",
                errorType = DiscountCodeAction.DiscountAppliedError.ErrorType.INVALID,
                errorMessage = "Promo code unavailable for this stay"
            ),
            onAction = { }
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun DiscountCodeLoadingPreview() {
    MaterialTheme {
        DiscountCodeBottomSheetContent(
            state = DiscountCodeBottomSheetState(
                discountCode = "SAVE20",
                isLoading = true
            ),
            onAction = { }
        )
    }
}