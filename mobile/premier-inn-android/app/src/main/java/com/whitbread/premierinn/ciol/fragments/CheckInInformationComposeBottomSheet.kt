package com.whitbread.premierinn.ciol.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.compose.ui.components.ButtonTeal
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.proximaNovaFontFamily


@Composable
fun CheckInInformationBottomSheetContent(
    headerTitle: String,
    headerSubTitle: String,
    understoodMessage: String,
    buttonText: String,
    onCloseClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.white))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel Image
            Image(
                painter = painterResource(id = R.drawable.ic_map_hotel_head),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.width(4.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = headerTitle,
                    style = TextStyle(
                        fontFamily = proximaNovaFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = colorResource(id = R.color.full_black)
                )
                Text(
                    text = headerSubTitle,
                    style = TextStyle(
                        fontFamily = proximaNovaFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = colorResource(id = R.color.full_black)
                )
            }

            // Close Button
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.grey_3).copy(alpha = 0.12F))
                    .clickable { onCloseClick() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close_dark),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = colorResource(id = R.color.grey_lavender)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Understood Message
        Text(
            text = understoodMessage,
            style = TextStyle(
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            color = colorResource(id = R.color.grey_dark)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Understood Button
        GenericButton(
            text = buttonText,
            color = ButtonTeal,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            onClick = onButtonClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CheckInInformationBottomSheetContentPreview() {
    CheckInInformationBottomSheetContent(
        headerTitle = "You're almost there",
        headerSubTitle = "Check-in information",
        understoodMessage = "Please confirm your check-in details.",
        buttonText = "Confirm check-in",
        onCloseClick = {},
        onButtonClick = {}
    )
}

