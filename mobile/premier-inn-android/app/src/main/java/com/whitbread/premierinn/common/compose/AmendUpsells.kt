package com.whitbread.premierinn.common.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendupsells.AmendUpsellsAction
import com.whitbread.premierinn.amend.amendupsells.RoomHeaderSummaryModel
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryPrice
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.LightGrey5
import com.whitbread.premierinn.compose.ui.components.Purple
import com.whitbread.premierinn.compose.ui.components.proximaNovaFontFamily
import com.whitbread.premierinn.data.common.EMPTY_STRING

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HotelInformationHeader(
    imageUrl: String,
    hotelName: String,
    dateAndNights: String,
    guestsAndRooms: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlideImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(70.dp),
            failure = placeholder(R.drawable.image_no_hotel)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = hotelName,
                fontSize = 18.sp,
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = dateAndNights,
                    fontSize = 16.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = guestsAndRooms,
                    fontSize = 16.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun HotelApprovedMeals(showApprovedMeals: Boolean, isDinnerAllowance: Boolean) {
    if (showApprovedMeals) {
        Text(
            text = if (isDinnerAllowance){
                stringResource(id = R.string.amend_extras_approved_meals_dinner_allowance_bb)
            } else {
                stringResource(id = R.string.amend_extras_approved_meals_bb)
            },
            fontSize = 16.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun RestaurantCarousel(restaurantImages: List<String>) {
    if (restaurantImages.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2
    ) {
        restaurantImages.size
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            GlideImage(
                model = restaurantImages[page % restaurantImages.size],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun RoomHeaderSummaryItem(
    ui: RoomHeaderSummaryModel,
    onAddUpsellClick: () -> Unit,
    onChangeUpsellClick: () -> Unit,
    modifier: Modifier = Modifier,
    isMeal: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .animateContentSize()
            .padding(bottom = 10.dp)
    ) {

        // Room header
        Text(
            text = ui.roomTitle,
            color = Color.Black,
            fontSize = 20.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 10.dp, top = 5.dp)
        )

        Text(
            text = ui.roomDescription,
            fontSize = 16.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 10.dp)
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = colorResource(R.color.base_black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )


        //Chosen meal info row
        if (ui.showChosenUpsellRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Tick icon
                if (ui.showTick) {
                    Image(
                        painter = painterResource(R.drawable.ic_tick_circle),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                    )
                }

                // Selected meal text
                Text(
                    text = ui.selectedUpsellText,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp, end = 10.dp)
                )

                // Action button (Add / Change)
                when {
                    ui.showAddUpsellButton -> {
                        AddUpsellsButton(
                            onClick = onAddUpsellClick,
                            buttonText = if (isMeal) stringResource(R.string.summary_add_meals) else stringResource(
                                R.string.summary_add_extras
                            )
                        )
                    }

                    ui.showChangeUpsellButton -> {
                        Text(
                            fontSize = 16.sp,
                            fontFamily = proximaNovaFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            text = stringResource(R.string.summary_change_selection),
                            color = colorResource(R.color.premier_inn_purple),
                            modifier = Modifier
                                .padding(5.dp)
                                .clickable(onClick = onChangeUpsellClick)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddUpsellsButton(
    onClick: () -> Unit,
    buttonText: String
) {
    GenericButton(
        buttonText, onClick = onClick,
        modifier = Modifier
            .width(130.dp)
            .height(44.dp),
        color = colorResource(R.color.light_grey),
        textColor = Color.Black
    )
}

@Composable
fun MenuAndAllergies(onAction: (AmendUpsellsAction) -> Unit) {
    Surface(modifier = Modifier.background(Color.White)) {
        Row(
            modifier =
                Modifier
                    .background(Color.White)
                    .padding(start = 0.dp, top = 10.dp, end = 0.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .height(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(id = R.string.summary_view_menus),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAction(AmendUpsellsAction.MenuAndAllergyInfoClicked(false)) },
                fontSize = 16.sp,
                color = Purple,
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )

            VerticalDivider(
                color = colorResource(id = R.color.light_grey),
                thickness = 1.dp,
                modifier = Modifier.fillMaxHeight()
            )

            Text(
                text = stringResource(id = R.string.summary_allergy_info),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAction(AmendUpsellsAction.MenuAndAllergyInfoClicked(true)) },
                fontSize = 16.sp,
                color = Purple,
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }

}

@Composable
fun ExtraItem(
    item: SummaryExtrasItem,
    showDivider: Boolean,
    roomIndex: Int,
    extraIndex: Int,
    onAction: (AmendUpsellsAction) -> Unit
) {
    var checked by remember { mutableStateOf(item.selected) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .animateContentSize()
    ) {

        //Extra offer section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 24.dp,
                    top = 20.dp
                ),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.name ?: EMPTY_STRING,
                    fontSize = 18.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.price.formattedPrice,
                    fontSize = 16.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onAction(AmendUpsellsAction.ExtraToggled(roomIndex, extraIndex, it))
                },
                modifier = Modifier.padding(top = 5.dp),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Purple,
                    checkedTrackColor = Purple,
                    uncheckedThumbColor = LightGrey5,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }

        //Description section
        Text(
            text = AnnotatedString.fromHtml(item.description ?: EMPTY_STRING),
            fontSize = 14.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(
                start = 16.dp,
                end = 20.dp,
                top = 10.dp,
                bottom = 20.dp
            )
        )
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun MealItem(
    item: SummaryMealItem,
    showDivider: Boolean,
    mealIndex: Int,
    roomIndex: Int,
    adultsNumber: Int,
    sumOfAllMeals: Int,
    onAction: (AmendUpsellsAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .animateContentSize()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            //Meal offer section
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.price.formattedPrice,
                    fontSize = 16.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold
                )

                // promo price?
                if (item.originalPrice != null) {
                    Text(
                        text = item.originalPrice.formattedPrice,
                        fontSize = 16.sp,
                        fontFamily = proximaNovaFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            //Counter section
            Counter(
                count = item.counter,
                mealIndex = mealIndex,
                roomIndex = roomIndex,
                adultsNumber = adultsNumber,
                sumOfAllMeals = sumOfAllMeals,
                onAction = onAction
            )
        }

        //Description section
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.description,
                fontSize = 16.sp,
                fontFamily = proximaNovaFontFamily,
                fontWeight = FontWeight.Normal
            )

            if (item.offerTag.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                OfferTag(text = item.offerTag)
            }

            if (item.kidsEatFree) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = stringResource(R.string.summary_kids_eat_breakfast_free),
                    fontSize = 12.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun OfferTag(text: String) {
    Row(
        modifier = Modifier
            .background(
                color = colorResource(R.color.information_blue_tint),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_tag),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = colorResource(id = R.color.teal)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.teal)
        )
    }
}

@Composable
private fun Counter(
    count: Int,
    mealIndex: Int,
    roomIndex: Int,
    adultsNumber: Int,
    sumOfAllMeals: Int,
    onAction: (AmendUpsellsAction) -> Unit
) {

    var counter by remember { mutableIntStateOf(count) }
    val isPlusEnabled = sumOfAllMeals < adultsNumber
    val isMinusEnabled = counter > 0

    val iconPlus =
        if (isPlusEnabled) R.drawable.criteria_plus_enabled else R.drawable.criteria_plus_disabled
    val iconMinus =
        if (isMinusEnabled) R.drawable.criteria_minus_enabled else R.drawable.criteria_minus_disabled

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (counter > 0) counter--
                    onAction(AmendUpsellsAction.MealDecrement(roomIndex, mealIndex))
                          },
                enabled = isMinusEnabled
            ) {
                Icon(
                    painter = painterResource(iconMinus),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }

            AnimatedContent(
                targetState = counter,
                label = "counter_animation"
            ) { value ->
                Text(
                    text = value.toString(),
                    fontSize = 20.sp,
                    fontFamily = proximaNovaFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            IconButton(
                onClick = {
                    counter++
                    onAction(AmendUpsellsAction.MealIncrement(roomIndex, mealIndex))
                          },
                enabled = isPlusEnabled
            ) {
                Icon(
                    painter = painterResource(iconPlus),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(id = R.string.adults),
            fontSize = 16.sp,
            fontFamily = proximaNovaFontFamily,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun LazyListScope.dividerItem(
    key: String,
    thickness: Dp = 10.dp
) {
    item(key = key) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = thickness,
            color = LightGrey5
        )
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier
            .clickable(enabled = false) {}
            .background(Color.Black.copy(alpha = 0.1f))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Purple,
            strokeWidth = 4.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HotelInformationHeaderPreview() {
    MaterialTheme {
        HotelInformationHeader(
            imageUrl = "",
            hotelName = "London Heathrow Airport (M4/J4)",
            dateAndNights = "20 Feb - 22 Feb",
            guestsAndRooms = "1 guest - 1 room"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddUpsellsButtonPreview() {
    MaterialTheme {
        AddUpsellsButton(
            onClick = {},
            buttonText = "+Add Meals"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomMealSummaryItemWithHeaderPreview() {
    MaterialTheme {
        RoomHeaderSummaryItem(
            ui = RoomHeaderSummaryModel(
                roomTitle = "Room 1",
                roomDescription = "1 adult, double room",
                showChosenUpsellRow = true,
                showTick = true,
                selectedUpsellText = "1 x This very tasty meal available - £3.33 \n1 x Another tasty meal available - £3.33",
                showAddUpsellButton = false,
                showChangeUpsellButton = true
            ),
            onAddUpsellClick = {},
            onChangeUpsellClick = {},
            isMeal = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoomHeaderSummaryItemNoMealsPreview() {
    MaterialTheme {
        RoomHeaderSummaryItem(
            ui = RoomHeaderSummaryModel(
                roomTitle = "Room 1",
                roomDescription = "1 adult, double room",
                showChosenUpsellRow = true,
                showTick = false,
                selectedUpsellText = "No meals chosen",
                showAddUpsellButton = true,
                showChangeUpsellButton = false
            ),
            onAddUpsellClick = {},
            onChangeUpsellClick = {},
            isMeal = true
        )
    }
}

@Preview(
    name = "Meal Item",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun MealItemPreview() {
    MaterialTheme {
        MealItem(
            item = SummaryMealItem(
                id = "1",
                name = "Unlimited Premier Inn Breakfast",
                price = SummaryPrice(
                    amount = 10.00f,
                    formattedPrice = "£10.00"
                ),
                originalPrice = SummaryPrice(
                    amount = 0.00f,
                    formattedPrice = "(£0.00 for 2 nights)"
                ),
                description = "Wake up to all your cooked and continental favourites with our delicious unlimited breakfast.",
                offerTag = "App discount",
                kidsEatFree = true,
                counter = 1
            ),
            showDivider = true,
            mealIndex = 0,
            roomIndex = 0,
            adultsNumber = 1,
            sumOfAllMeals = 0,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExtrasSectionPreview() {
    MaterialTheme {
        ExtraItem(
            item = SummaryExtrasItem(
                id = "extra_1",
                name = "Early check-in",
                price = SummaryPrice(amount = 10.00f, formattedPrice = "£10.00"),
                description = "Check in any time from 11am (normal check-in time is 3pm).",
                selected = false
            ),
            showDivider = true,
            roomIndex = 0,
            extraIndex = 0,
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MenuAllergensPreview() {
    MaterialTheme {
        MenuAndAllergies(
            onAction = {}
        )
    }
}
