package com.whitbread.premierinn.ciol.views.compose

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.compose.ui.components.proximaNovaFontFamily


@Composable
fun PreStayOutlinedTextField(
    modifier: Modifier,
    label: String,
    text: String,
    shouldShowError: Boolean,
    errorText: String,
    enabled: Boolean = true,
    onItemValueChanged: (newValue: String) -> Unit,
    onItemClicked: () -> Unit = {}
) {
    OutlinedTextField(
        value = text,
        onValueChange = { inputTextEntered: String ->
            onItemValueChanged(inputTextEntered)
        },
        label = {
            Text(
                text = label,
                color = if (shouldShowError) Color.Red else Color.Black,
                fontSize = 15.sp,
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.ExtraLight
            )
        },
        enabled = enabled,
        isError = shouldShowError,
        modifier = modifier.clickable {
            onItemClicked()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = if (shouldShowError) Color.Red else Color.Black,
            unfocusedBorderColor = colorResource(R.color.light_grey_3),
            errorBorderColor = if (shouldShowError) Color.Red else Color.Black,
            disabledTextColor = Color.Black
        ),
        supportingText = {
            if (shouldShowError) {
                Text(
                    text = errorText,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    )
}
