package com.whitbread.premierinn.compose.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R

object ButtonDimensions {
    val textSize = 18.sp
    val minHeight = 50.dp
    val cornerRadius = 6.dp
}

enum class ButtonType {
    Filled, Outlined
}

@Composable
fun GenericButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    buttonType: ButtonType = ButtonType.Filled,
    textColor: Color = Color.White
) {
    val shape = RoundedCornerShape(ButtonDimensions.cornerRadius)
    val minHeight = ButtonDimensions.minHeight
    val contentPadding = PaddingValues(vertical = 12.dp, horizontal = 0.dp)

    when (buttonType) {
        ButtonType.Filled -> {
            Button(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minHeight),
                shape = shape,
                colors = ButtonDefaults.buttonColors(containerColor = color),
                contentPadding = contentPadding,
                enabled = enabled
            ) {
                TextWithProximaNovaSemiBold(text = text, color = textColor)
            }
        }
        ButtonType.Outlined -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minHeight),
                shape = shape,
                border = BorderStroke(1.dp, color),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
                contentPadding = contentPadding
            ) {
                TextWithProximaNovaSemiBold(text = text, color = color)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ButtonGenericPreview() {
    GenericButton(text = stringResource(id = R.string.gdpr_accept_button_text), color = ButtonTeal)
}