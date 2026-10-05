package com.whitbread.premierinn.landing.ui_compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.domain.graphql.common.entity.AppsContentCardDomain
import com.whitbread.premierinn.landing.model.CardNavigationModel


@Composable
fun DestinationCards(
    destinationCards: List<AppsContentCardDomain>,
    onCardClick: (CardNavigationModel) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(destinationCards) { item ->
            DestinationCard(item, onCardClick)
        }
    }
}

@Composable
fun ContentCards(
    contentCards: List<AppsContentCardDomain>,
    onCardClick: (CardNavigationModel) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(contentCards) { item ->
            ContentCard(item, onCardClick)
        }
    }
}

@Composable
fun PromoCards(
    promoCards: List<AppsContentCardDomain>,
    onCardClick: (CardNavigationModel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        promoCards.forEach {
            PromoCard(promoCard = it, onCardClick)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DestinationCard(
    destinationCard: AppsContentCardDomain,
    onCardClick: (CardNavigationModel) -> Unit
) {
    Column(
        modifier = Modifier
            .width(320.dp)
            .border(1.dp, colorResource(id = R.color.grey_light_x), RoundedCornerShape(8.dp))
            .clickable { onClickListener(onCardClick, destinationCard) }
    ) {
        Box {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(174.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                model = Urls.CONTENT_BASE_URL + destinationCard.imagePath,
                contentDescription = "destination card image"
            )

            if (destinationCard.imageTag.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = colorResource(id = R.color.teal_dark),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                    text = destinationCard.imageTag,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(R.font.proxima_nova_bold)),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = destinationCard.title,
                fontFamily = FontFamily(Font(R.font.proxima_nova_bold)),
                color = colorResource(id = R.color.premier_inn_purple),
                fontSize = 20.sp
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
            Text(
                text = destinationCard.subtitle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.proxima_nova_regular)),
                color = colorResource(id = R.color.grey_dark),
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ContentCard(contentCard: AppsContentCardDomain, onCardClick: (CardNavigationModel) -> Unit) {
    Box(
        modifier = Modifier
            .width(320.dp)
            .height(360.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClickListener(onCardClick, contentCard) }
    ) {
        GlideImage(
            model = Urls.CONTENT_BASE_URL + contentCard.imagePath,
            contentScale = ContentScale.Crop,
            contentDescription = "destination onCardClick image"
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = contentCard.title,
                fontFamily = FontFamily(Font(R.font.proxima_nova_bold)),
                color = Color.White,
                fontSize = 20.sp
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Text(
                text = contentCard.subtitle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.proxima_nova_regular)),
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PromoCard(promoCard: AppsContentCardDomain, onCardClick: (CardNavigationModel) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .border(1.dp, colorResource(id = R.color.grey_light_x), RoundedCornerShape(8.dp))
            .clickable { onClickListener(onCardClick, promoCard) }
    ) {
        Box {
            GlideImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
                model = Urls.CONTENT_BASE_URL + promoCard.imagePath,
                contentScale = ContentScale.Crop,
                contentDescription = "promo card image"
            )

            if (promoCard.imageTag.isNotBlank()) {
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = colorResource(id = R.color.premier_inn_purple),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                    text = promoCard.imageTag,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(R.font.proxima_nova_bold)),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Top)
        ) {
            Text(
                text = promoCard.title,
                fontFamily = FontFamily(Font(R.font.proxima_nova_bold)),
                color = colorResource(id = R.color.premier_inn_purple),
                fontSize = 20.sp
            )

            Text(
                text = promoCard.subtitle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.proxima_nova_regular)),
                color = colorResource(id = R.color.grey_dark),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun Heading(headingText: String) {
    Text(
        text = headingText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        fontFamily = FontFamily(Font(R.font.proxima_nova_extra_bold)),
        color = colorResource(id = R.color.premier_inn_purple),
        fontSize = 20.sp
    )
}

private fun onClickListener(
    onCardClick: (CardNavigationModel) -> Unit,
    homeCard: AppsContentCardDomain
) {
    onCardClick.invoke(
        CardNavigationModel(
            trackingId = homeCard.trackingId,
            linkPath = homeCard.linkPath,
            openLinkInApp = homeCard.openLinkInApp,
            latitude = homeCard.latitude,
            longitude = homeCard.longitude
        )
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
fun PreviewDestinationCard() {
    DestinationCard(
        destinationCard = AppsContentCardDomain(
            imagePath = "/content/dam/pi/websites/app/homepage/cornwall-app-homepage-320-174.jpg",
            imageTag = "Image tag",
            linkPath = "",
            openLinkInApp = true,
            subtitle = "Home to over 400 miles of coastline, you’ll find plenty of things to do in Cornwall, whatever the weather",
            title = "Cornwall",
            order = 0,
            trackingId = "",
            latitude = "",
            longitude = ""
        ),
        onCardClick = {}
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000
)
@Composable
fun PreviewContentCard() {
    ContentCard(
        contentCard = AppsContentCardDomain(
            imagePath = "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg",
            imageTag = "",
            linkPath = "",
            openLinkInApp = true,
            subtitle = "More comfort. More convenience. More connectivity. That’s our Premier Plus rooms.",
            title = "Premier Plus rooms",
            order = 0,
            trackingId = "",
            latitude = "",
            longitude = ""
        ),
        onCardClick = {}
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF,
    widthDp = 358
)
@Composable
fun PreviewPromoCard() {
    PromoCard(
        promoCard = AppsContentCardDomain(
            imagePath = "/content/dam/pi/websites/desktop/homepage/refresh/2022/family-500x320.jpg",
            imageTag = "Buy online",
            linkPath = "",
            openLinkInApp = true,
            subtitle = "Our hotels are perfect for comfy, affordable and fun family breaks, whatever the occasion.",
            title = "Family friendly",
            order = 0,
            trackingId = "",
            latitude = "",
            longitude = ""
        ),
        onCardClick = {}
    )
}
