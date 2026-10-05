package com.whitbread.premierinn.landing.ui_compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.landing.model.CardNavigationModel
import com.whitbread.premierinn.landing.model.BottomSheetState

@Composable
fun HomePageBottomSheet(
    state: BottomSheetState,
    onCardClick: (CardNavigationModel) -> Unit,
    onClearRecentSearchClick: () -> Unit,
    onRecentSearchClick: (RecentSearch) -> Unit,
    onNotificationLinkClick: (link: String, shouldOpenInApp: Boolean) -> Unit
) {
    val displayNotification = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .nestedScroll(rememberNestedScrollInteropConnection())
    ) {

        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 4.dp),
            painter = painterResource(id = R.drawable.home_page_bottom_sheet_drag_rectangle),
            contentDescription = "home page bottom sheet drag icon"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            state.contentDomain?.let {
                it.notification?.let { notification ->
                    if (displayNotification.value) {
                        item {
                            HomeNotification(notification, onNotificationLinkClick) { displayNotification.value = false }
                        }
                    }
                }
            }

            if (state.recentSearches.isNotEmpty()) {
                item {
                    RecentSearches(
                        recentSearches = state.recentSearches,
                        onClearRecentSearchClick = onClearRecentSearchClick,
                        onRecentSearchClick = onRecentSearchClick
                    )
                }
            }

            state.contentDomain?.let {
                if (it.heading.isNotBlank()) {
                    item {
                        Heading(it.heading)
                    }
                }

                item {
                    DestinationCards(it.destinationCards, onCardClick)
                }

                item {
                    ContentCards(it.contentCards, onCardClick)
                }

                item {
                    PromoCards(it.promoCards, onCardClick)
                }
            }
        }
    }
}
