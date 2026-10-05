package com.whitbread.premierinn.amend.amendupsells

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendupsells.AmendUpsellsViewModel.*
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.compose.ExtraItem
import com.whitbread.premierinn.common.compose.HotelApprovedMeals
import com.whitbread.premierinn.common.compose.HotelInformationHeader
import com.whitbread.premierinn.common.compose.MealItem
import com.whitbread.premierinn.common.compose.MenuAndAllergies
import com.whitbread.premierinn.common.compose.ProgressBar
import com.whitbread.premierinn.common.compose.RestaurantCarousel
import com.whitbread.premierinn.common.compose.RoomHeaderSummaryItem
import com.whitbread.premierinn.common.compose.dividerItem
import com.whitbread.premierinn.compose.ui.components.AppBarTextWithProximaNovaSemiBold
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.LightGrey5
import com.whitbread.premierinn.compose.ui.components.Purple
import com.whitbread.premierinn.compose.ui.components.proximaNovaFontFamily
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryPrice
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.compose.ui.components.InfoBlue
import com.whitbread.premierinn.compose.ui.components.InfoBlue40
import com.whitbread.premierinn.compose.ui.components.InfoBlueTint
import com.whitbread.premierinn.compose.ui.components.InfoMessageBox
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.summary.toShortDateFormat
import kotlin.collections.lastIndex

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UpsellsScreen(
    state: AmendUpsellsState,
    onAction: (AmendUpsellsAction) -> Unit
) {
    val isAmendUpsellsFlow = true
    Scaffold(
        containerColor = LightGrey5,
        topBar = {
            TopAppBar(
                title = { AppBarTextWithProximaNovaSemiBold(text = stringResource(R.string.amend_meals_and_extras_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple, titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { onAction(AmendUpsellsAction.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Continue for Amend Upsells for now
            if (isAmendUpsellsFlow) {
                GenericButton(
                    color = Purple,
                    enabled = !state.isLoading,
                    onClick = {
                        onAction(AmendUpsellsAction.ContinueClicked)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(44.dp),
                    text = stringResource(id = R.string.button_text_continue)
                )
            } else {
                //show booking summary button
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            var expandedMealRoomIndex by rememberSaveable(state.roomList.size) {
                mutableIntStateOf(if (state.roomList.isNotEmpty()) 0 else -1)
            }

            var expandedExtrasRoomIndex by rememberSaveable(state.roomList.size) {
                mutableIntStateOf(if (state.roomList.isNotEmpty()) 0 else -1)
            }

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                item {
                    val guests = pluralStringResource(
                        R.plurals.guests,
                        state.numberOfGuests,
                        state.numberOfGuests
                    )
                    val rooms = pluralStringResource(
                        R.plurals.rooms,
                        state.numberOfRooms,
                        state.numberOfRooms
                    )

                    HotelInformationHeader(
                        imageUrl = Urls.CONTENT_BASE_URL + state.galleryImageSrc?.first(),
                        hotelName = state.hotelName ?: EMPTY_STRING,
                        dateAndNights = state.reservation?.let {
                            stringResource(
                                R.string.arrival_departure_dates,
                                it.arrival.toString().toShortDateFormat(),
                                it.departure.toString().toShortDateFormat()
                            )
                        } ?: EMPTY_STRING,
                        guestsAndRooms = stringResource(
                            R.string.guests_and_rooms,
                            guests,
                            rooms
                        )
                    )
                }
                if (!state.isLoading) {
                    val isSingleRoom = state.roomList.size == 1

                    if (state.isRestaurantClosed) {
                        item { InfoMessageBox(
                            modifier =  Modifier.padding(horizontal = 8.dp),
                            iconRes = R.drawable.ic_info,
                            iconTintColor = InfoBlue,
                            backgroundColor = InfoBlueTint,
                            borderColor = InfoBlue40,
                            text = stringResource(R.string.summary_restaurant_unavailable_description)) }
                    } else if (state.areAllMealsRestricted) {
                        item {
                            InfoMessageBox(
                                text = stringResource(R.string.summary_no_meals_approved_description),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                iconRes = R.drawable.ic_info,
                                iconTintColor = InfoBlue,
                                backgroundColor = InfoBlueTint,
                                borderColor = InfoBlue40
                            )
                        }
                    } else {
                        item { HotelApprovedMeals(state.showApprovedMeals, state.isDinnerAllowance) }
                        item { RestaurantCarousel(state.listOfGalleryUpsellImages) }

                        if (isSingleRoom) {
                            singleRoomMealSection(
                                room = state.roomList.first(),
                                onAction = onAction
                            )
                        } else {
                            multiRoomMeals(
                                rooms = state.roomList,
                                expandedRoomIndex = expandedMealRoomIndex,
                                onExpandRoom = { expandedMealRoomIndex = it },
                                onAction = onAction
                            )
                        }

                        item { MenuAndAllergies(onAction) }
                    }

                    if (!isAmendUpsellsFlow) {
                        item {
                            Text(
                                text = stringResource(R.string.summary_select_extras_title),
                                fontSize = 23.sp,
                                fontFamily = proximaNovaFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 30.dp, bottom = 16.dp, start = 16.dp)
                            )
                        }
                        if (isSingleRoom) {
                            singleRoomExtrasSection(
                                room = state.roomList.first(),
                                roomIndex = 0,
                                onAction = onAction
                            )
                        } else {
                            multiRoomExtras(
                                rooms = state.roomList,
                                expandedRoomIndex = expandedExtrasRoomIndex,
                                onExpandRoom = { expandedExtrasRoomIndex = it },
                                onAction = onAction
                            )
                        }
                    }
                }

            }
            if (state.isLoading) {
                ProgressBar()
            }
        }
    }
}

/**
 * @LazyListScope is the only way to do nested lists in LazyColumn
 */
fun LazyListScope.singleRoomMealSection(
    room: SummaryRoomItem,
    onAction: (AmendUpsellsAction) -> Unit
) {
    itemsIndexed(
        items = room.meals,
        key = { mealIndex, meal ->
            //  to create an id if not provided to prevent recomposition issues
            "single_room_meal_${mealIndex}_${meal.id}"
        }
    ) { mealIndex, meal ->
        MealItem(
            item = meal,
            showDivider = mealIndex != room.meals.lastIndex,
            mealIndex = mealIndex,
            roomIndex = 0,
            adultsNumber = room.adults,
            sumOfAllMeals = room.meals.sumOf { it.counter },
            onAction = onAction
        )
    }
}

fun LazyListScope.multiRoomMeals(
    rooms: List<SummaryRoomItem>,
    expandedRoomIndex: Int,
    onExpandRoom: (Int) -> Unit,
    onAction: (AmendUpsellsAction) -> Unit
) {
    rooms.forEachIndexed { roomIndex, room ->
        val isExpanded = roomIndex == expandedRoomIndex

        // As a test just for now
        val hasSelectedMeals = room.meals.any { it.counter > 0 }
        val selectedMeals = room.meals.filter { it.counter > 0 }


        // Collapsed: show chosen row; Expanded: hide chosen row
        val showChosenRow = !isExpanded

        // In collapsed state we will have the following scenarios:
        // - if hasSelectedMeals -> show tick + Change
        // - else -> show Add meals, no tick
        val showTick = showChosenRow && hasSelectedMeals
        val showChange = showChosenRow && hasSelectedMeals
        val showAdd = showChosenRow && !hasSelectedMeals


        // Room Header
        item(key = "room_meal_header_$roomIndex") {
            RoomHeaderSummaryItem(
                ui = RoomHeaderSummaryModel(
                    roomTitle = stringResource(R.string.summary_room_name, room.roomNumber),
                    roomDescription = roomDescription(room),
                    showChosenUpsellRow = showChosenRow,
                    showTick = showTick,
                    selectedUpsellText = selectedMeals(
                        room.children,
                        hasSelectedMeals,
                        selectedMeals
                    ),
                    showAddUpsellButton = showAdd,
                    showChangeUpsellButton = showChange
                ),
                onAddUpsellClick = { onExpandRoom(roomIndex) },
                onChangeUpsellClick = { onExpandRoom(roomIndex) },
                isMeal = true
            )
        }
        // Room Meals
        if (isExpanded) {
            itemsIndexed(
                items = room.meals,
                key = { mealIndex, meal ->
                    //  to create an id if not provided to prevent recomposition issues
                    "room_${roomIndex}_meal_${mealIndex}_${meal.id}"
                }
            ) { mealIndex, meal ->
                MealItem(
                    item = meal,
                    showDivider = mealIndex != room.meals.lastIndex,
                    mealIndex = mealIndex,
                    roomIndex = roomIndex,
                    adultsNumber = room.adults,
                    sumOfAllMeals = room.meals.sumOf { it.counter },
                    onAction = onAction
                )
            }
        }

        dividerItem("meal_divider_${roomIndex}")
    }
}

@Composable
private fun roomDescription(room: SummaryRoomItem): String {
    val adults = pluralStringResource(R.plurals.number_of_adults, room.adults, room.adults)
    val children = pluralStringResource(R.plurals.number_of_children, room.children, room.children)

    return if (room.children > 0) {
        stringResource(
            R.string.summary_room_details_text_with_adults_and_children,
            adults,
            children,
            room.roomType
        )
    } else {
        stringResource(R.string.summary_room_details_text_with_adults_only, adults, room.roomType)
    }
}

@Composable
private fun selectedMeals(
    childrenNumber: Int,
    hasSelectedMeals: Boolean,
    meals: List<SummaryMealItem>
): String {
    if (!hasSelectedMeals)
        return stringResource(R.string.summary_room_no_meals_chosen)
    var selectedMeals = meals.filter {
        it.counter > 0
    }.map {
        if (it.price.amount == 0F) {
            stringResource(R.string.summary_room_free_meals, it.name)
        } else
            stringResource(
                R.string.summary_room_chosen_meals,
                it.counter,
                it.name,
                it.price.formattedPrice
            )
    }.joinToString("\n")

    if (childrenNumber > 0 && meals.any { it.kidsEatFree }) {
        selectedMeals += "\n${stringResource(R.string.summary_kids_eat_breakfast_free)}"
    }
    return selectedMeals
}

@Composable
private fun selectedExtras(hasSelectedExtras: Boolean, extras: List<SummaryExtrasItem>): String {
    if (!hasSelectedExtras)
        return stringResource(R.string.summary_room_no_extras_chosen)
    return extras.filter { it.selected }.map {
        stringResource(
            R.string.summary_room_chosen_extras,
            it.name ?: EMPTY_STRING,
            it.price.formattedPrice
        )
    }.joinToString(separator = "\n")
}


//created host composable for previewing LazyListScope functions
@Composable
private fun SingleRoomMealsPreviewHost(
    room: SummaryRoomItem
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        singleRoomMealSection(
            room = room,
            onAction = {}
        )
    }
}

@Composable
private fun MultiRoomMealsPreviewHost(
    rooms: List<SummaryRoomItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        multiRoomMeals(
            rooms = rooms,
            expandedRoomIndex = 0,
            onExpandRoom = {},
            onAction = {}
        )
    }
}

fun LazyListScope.singleRoomExtrasSection(
    room: SummaryRoomItem,
    roomIndex: Int = 0,
    onAction: (AmendUpsellsAction) -> Unit
) {
    itemsIndexed(
        items = room.extras,
        key = { extraIndex, extra ->
            //  to create an id if not provided to prevent recomposition issues
            "single_room_extra_${extraIndex}_${extra.id}"
        }
    ) { extraIndex, extra ->
        ExtraItem(
            item = extra,
            showDivider = extraIndex != room.extras.lastIndex,
            roomIndex = roomIndex,
            extraIndex = extraIndex,
            onAction = onAction
        )
    }
}

//created host composables for previewing purposes
@Composable
private fun SingleRoomExtrasSectionHost(
    room: SummaryRoomItem
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        singleRoomExtrasSection(
            room = room,
            roomIndex = 0,
            onAction = {}
        )
    }
}


fun LazyListScope.multiRoomExtras(
    rooms: List<SummaryRoomItem>,
    expandedRoomIndex: Int,
    onExpandRoom: (Int) -> Unit,
    onAction: (AmendUpsellsAction) -> Unit
) {
    rooms.forEachIndexed { roomIndex, room ->
        val isExpanded = roomIndex == expandedRoomIndex

        // As a test just for now
        val hasSelectedExtras = room.extras.any { it.selected }
        val selectedExtras = room.extras.filter { it.selected }

        // Collapsed: show chosen row; Expanded: hide chosen row
        val showChosenRow = !isExpanded

        // In collapsed state we will have the following scenarios:
        // - if hasSelectedExtras -> show tick + Change
        // - else -> show Add extras, no tick
        val showTick = showChosenRow && hasSelectedExtras
        val showChange = showChosenRow && hasSelectedExtras
        val showAdd = showChosenRow && !hasSelectedExtras

        // Room Header
        item(key = "room_extra_header_$roomIndex") {
            RoomHeaderSummaryItem(
                ui = RoomHeaderSummaryModel(
                    roomTitle = stringResource(R.string.summary_room_name, room.roomNumber),
                    roomDescription = roomDescription(room),
                    showChosenUpsellRow = showChosenRow,
                    showTick = showTick,
                    selectedUpsellText = selectedExtras(hasSelectedExtras, selectedExtras),
                    showAddUpsellButton = showAdd,
                    showChangeUpsellButton = showChange
                ),
                onAddUpsellClick = { onExpandRoom(roomIndex) },
                onChangeUpsellClick = { onExpandRoom(roomIndex) },
                isMeal = false
            )
        }
        // Room Extras
        if (isExpanded) {
            itemsIndexed(
                items = room.extras,
                key = { extraIndex, extra ->
                    //  to create an id if not provided to prevent recomposition issues
                    "room_${roomIndex}_extra_${extraIndex}_${extra.id}"
                }
            ) { extraIndex, extra ->
                ExtraItem(
                    item = extra,
                    showDivider = extraIndex != room.extras.lastIndex,
                    roomIndex = roomIndex,
                    extraIndex = extraIndex,
                    onAction = onAction
                )
            }
        }

        dividerItem("divider_extra_${roomIndex}")
    }
}

//created host composables for previewing purposes
@Composable
fun MultiRoomExtrasSectionHost() {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        multiRoomExtras(
            rooms = mockSummaryRoomItems,
            expandedRoomIndex = 0,
            onExpandRoom = {},
            onAction = {}
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun UpsellsScreenPreview() {
    UpsellsScreen(
        state = AmendUpsellsState(
            isLoading = false,
            showApprovedMeals = true,
            listOfGalleryUpsellImages = mutableListOf(
                "https://www.premierinn.com/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Breakfast Cooked WHB24136.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Breakfast Continental WHB2413.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/grab-go/122027 Grab and Go breakfast - Website Tiles_1.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/grab-go/122027 Grab and Go breakfast - Website Tiles_3.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Meal Deal R2 WHB24136 - cropped.jpg"
            ),
        ),
        onAction = {}
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun UpsellsScreenWithRestaurantClosedPreview() {
    UpsellsScreen(
        state = AmendUpsellsState(
            listOfGalleryUpsellImages = mutableListOf(
                "https://www.premierinn.com/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Breakfast Cooked WHB24136.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Breakfast Continental WHB2413.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/grab-go/122027 Grab and Go breakfast - Website Tiles_1.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/grab-go/122027 Grab and Go breakfast - Website Tiles_3.jpg",
                "https://www.premierinn.com/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Meal Deal R2 WHB24136 - cropped.jpg"
            ),
            isRestaurantClosed = true
        ),
        onAction = {}
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun UpsellsScreenNoMealsPreview() {
    UpsellsScreen(
        state = AmendUpsellsState(
            isLoading = false,
            showApprovedMeals = false,
            areAllMealsRestricted = true
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun MultiRoomMealsPreview() {
    MaterialTheme {
        MultiRoomMealsPreviewHost(
            rooms = mockSummaryRoomItems
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun SingleRoomMealsPreview() {
    MaterialTheme {
        SingleRoomMealsPreviewHost(
            room = mockSummaryRoomItems.first()
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun SingleRoomExtrasPreview() {
    MaterialTheme {
        SingleRoomExtrasSectionHost(
            room = mockSummaryRoomItems.first()
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun MultiRoomExtrasPreview() {
    MaterialTheme {
        MultiRoomExtrasSectionHost()
    }
}

data class RoomHeaderSummaryModel(
    val roomTitle: String,
    val roomDescription: String,
    val showChosenUpsellRow: Boolean,
    val showTick: Boolean,
    val selectedUpsellText: String,
    val showAddUpsellButton: Boolean,
    val showChangeUpsellButton: Boolean
)

private val mockSummaryMealItems = listOf(
    SummaryMealItem(
        id = "meal_1",
        name = "Unlimited Premier Inn Breakfast",
        price = SummaryPrice(amount = 10.00f, formattedPrice = "£10.00"),
        originalPrice = SummaryPrice(amount = 0.00f, formattedPrice = "(£0.00 for 2 nights)"),
        description = "Wake up to all your cooked and continental favourites with our delicious unlimited breakfast.",
        offerTag = "App discount",
        kidsEatFree = true,
        counter = 0
    ),
    SummaryMealItem(
        id = "meal_2",
        name = "Continental Breakfast",
        price = SummaryPrice(amount = 7.50f, formattedPrice = "£7.50"),
        description = "Tuck into a tasty range of chilled juices, cereals, pastries, fruit and more.",
        offerTag = EMPTY_STRING,
        kidsEatFree = false,
        counter = 0
    )
)

private val mockSummaryExtrasItems = listOf(
    SummaryExtrasItem(
        id = "extra_1",
        name = "Ultimate WI-FI",
        price = SummaryPrice(amount = 5.00f, formattedPrice = "£5.00"),
        description = "Our most reliable Wi-Fi yet, enjoy an even speedier connection with Ultimate Wi-Fi.",
        selected = false
    ),
    SummaryExtrasItem(
        id = "extra_2",
        name = "Early check-in",
        price = SummaryPrice(amount = 7.50f, formattedPrice = "£7.50"),
        description = "Need to check in earlier? Start your stay from 11am instead of the usual 3pm.",
        selected = false
    )
)

private val mockSummaryRoomItems = listOf(
    SummaryRoomItem(
        roomNumber = "1",
        roomType = "Standard Double Room",
        adults = 2,
        children = 0,
        meals = mockSummaryMealItems,
        extras = mockSummaryExtrasItems
    ),
    SummaryRoomItem(
        roomNumber = "2",
        roomType = "Twin Room",
        adults = 2,
        children = 1,
        meals = mockSummaryMealItems,
        extras = mockSummaryExtrasItems
    ),
    SummaryRoomItem(
        roomNumber = "3",
        roomType = "Single Room",
        adults = 1,
        children = 0,
        meals = mockSummaryMealItems,
        extras = mockSummaryExtrasItems
    )
)
