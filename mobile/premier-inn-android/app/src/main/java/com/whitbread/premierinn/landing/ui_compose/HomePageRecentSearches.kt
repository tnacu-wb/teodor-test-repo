package com.whitbread.premierinn.landing.ui_compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.data.common.BRAND_HUB
import com.whitbread.premierinn.data.common.BRAND_PI
import com.whitbread.premierinn.data.common.BRAND_PI_GERMANY
import com.whitbread.premierinn.data.common.BRAND_ZIP
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.landing.MessageProvider
import org.threeten.bp.LocalDate

@Composable
fun RecentSearches(
    recentSearches: List<RecentSearch>,
    onClearRecentSearchClick: () -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.recent_search_title),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.proxima_nova_extra_bold)),
                color = colorResource(id = R.color.premier_inn_purple),
                fontSize = 20.sp
            )
            Text(
                text = stringResource(id = R.string.clear_recent_searches),
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.proxima_nova_semibold)),
                color = colorResource(id = R.color.premier_inn_purple),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onClearRecentSearchClick.invoke() }
            )
        }
        recentSearches.forEach {
            RecentSearch(recentSearch = it, onRecentSearchClick = onRecentSearchClick)
        }
    }
}

@Composable
fun RecentSearch(
    recentSearch: RecentSearch,
    onRecentSearchClick: (RecentSearch) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecentSearchClick.invoke(recentSearch) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(id = setPlaceTypeImage(recentSearch.hotelBrand)),
                        contentDescription = "Search Icon",
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = recentSearch.searchTerm,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.proxima_nova_bold))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = formatDate(recentSearch.arrivalDate),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.proxima_nova_regular))
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.ic_navigation_arrow_right),
                        contentDescription = "Arrow",
                        tint = Color.Unspecified
                    )

                    Text(
                        text = formatDate(recentSearch.departureDate),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.proxima_nova_regular))
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        modifier = Modifier
                            .size(3.dp)
                            .align(Alignment.CenterVertically),
                        painter = painterResource(id = R.drawable.ic_small_circle),
                        contentDescription = "icon",
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = setRoomCriteria(
                            recentSearch.roomsCount,
                            recentSearch.adults.sum(),
                            recentSearch.children.sum().plus(recentSearch.infants.sum())
                        ),
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.proxima_nova_regular))
                    )

                }
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow),
                contentDescription = "Navigate",
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Divider(thickness = 1.dp, color = colorResource(id = R.color.grey_light))
    }
}

@Composable
private fun setRoomCriteria(
    roomsCount: Int,
    adults: Int,
    children: Int
): String {
    return if (children > 0) {
        MessageProvider(LocalContext.current.resources).guestsAndRooms(
            adults + children,
            roomsCount
        )
    } else {
        MessageProvider(LocalContext.current.resources).adultsAndRooms(adults, roomsCount)
    }
}

private fun formatDate(date: LocalDate) = date.format(DateFormat.WEEKDAY_DAY_MONTH)

private fun setPlaceTypeImage(hotelBrand: String?): Int {
    return when (hotelBrand) {
        BRAND_PI, BRAND_PI_GERMANY -> R.drawable.ic_hotel_purple
        BRAND_HUB -> R.drawable.ic_hub_hotel
        BRAND_ZIP -> R.drawable.ic_zip_logo
        else -> R.drawable.ic_search_grey
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF,
    widthDp = 358
)
@Composable
fun PreviewRecentSearch() {
    RecentSearch(
        recentSearch = RecentSearch(
            searchTerm = "Holborn",
            hotelCode = "LONHOL",
            latitude = 51.555553F,
            longitude = 0.0949867F,
            hotelBrand = "PI",
            arrivalDate = LocalDate.parse("2025-07-11"),
            departureDate = LocalDate.parse("2025-07-12"),
            roomsCount = 1,
            adults = listOf(1),
            children = listOf(0),
            infants = listOf(0),
            cots = listOf(false),
            roomTypeCodes = listOf("DB")
        ),
        onRecentSearchClick = {}
    )
}
