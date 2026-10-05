package com.whitbread.premierinn.roomcriteria.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.RoomType.ACCESSIBLE
import com.whitbread.premierinn.domain.common.RoomType.DOUBLE
import com.whitbread.premierinn.domain.common.RoomType.SINGLE
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.roomcriteria.RoomCriteriaOrderedCollection
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import com.whitbread.premierinn.roomcriteria.RoomCriteriaActivity.Companion.ROOM_CRITERIA_SELECTION
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.AccessibleCotConstraint
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.MaxAdultConstraintEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.MaxChildrenConstraintEvent
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel.MaxInfantsConstraint
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class BaseRoomCriteriaViewModelTest {

    @get:Rule
    val rxJavaRule = RxJavaTestRule()
    private val getStringResource = mockk<GetStringResource>()
    private val criteriaMessageProvider = mockk<RoomCriteriaHelper>()

    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true){
        every { get<List<RoomCriteria>>(ROOM_CRITERIA_SELECTION) } returns null
    }

    @Test
    fun `should return default state as pre-selected initial state`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testObserver = viewModel.states().test()

        testObserver
                .assertValueCount(1)
                .assertValue {
                    with(it.getRoom(1)) {
                        this.numberOfAdults == 2 &&
                        this.numberOfChildren == 0 &&
                        this.numberOfInfants == 0 &&
                        this.roomType == DOUBLE
                    }
                }
    }

    @Test
    fun onAdultsNumberIncreased() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 1, children = 0, infants = 0, roomType = DOUBLE))

        val testObserver = viewModel.states().test()

        viewModel.onAdultsNumberIncreased(1)

        testObserver
                .assertValueAt(1) { it.getRoom(1).numberOfAdults == 2 }
    }

    @Test
    fun `onAdultsNumberIncreased failed due to MaxAdultConstraintEvent`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()
        val testEventObserver = viewModel.events().test()

        viewModel.onAdultsNumberIncreased(1)

        testStateObserver.assertValueCount(1)
        testEventObserver.assertValue { it is MaxAdultConstraintEvent && it.roomId == 1 }
    }

    @Test
    fun onAdultsNumberDecreased() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onAdultsNumberDecreased(1)

        testStateObserver.assertValueAt(1) { it.getRoom(1).numberOfAdults == 1 }
    }

    @Test
    fun `onAdultsNumberDecreased failed due to minimum range constraint reached`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 1, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onAdultsNumberDecreased(1)

        testStateObserver.assertValueCount(1).assertValueAt(0) {
            it.getRoom(1).numberOfAdults == 1
        }
    }

    @Test
    fun onChildrenNumberIncreased() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testObserver = viewModel.states().test()

        viewModel.onChildrenNumberIncreased(1)

        testObserver.assertValueAt(1) { it.getRoom(1).numberOfChildren == 1 }
    }

    @Test
    fun `onChildrenNumberIncreased failed due to MaxConstraintEvent`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()
        val testEventObserver = viewModel.events().test()

        viewModel.onChildrenNumberIncreased(1)
        viewModel.onChildrenNumberIncreased(1)
        viewModel.onChildrenNumberIncreased(1)

        testStateObserver.assertValueAt(2) { it.getRoom(1).numberOfChildren == 2 }
        testEventObserver.assertValue { it is MaxChildrenConstraintEvent && it.roomId == 1 }
    }

    @Test
    fun onChildrenNumberDecreased() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 2, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onChildrenNumberDecreased(1)

        testStateObserver.assertValueAt(1) { it.getRoom(1).numberOfChildren == 1 }
    }

    @Test
    fun `onChildrenNumberDecreased failed due to minimum range constraint reached`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onChildrenNumberDecreased(1)

        testStateObserver.assertValueCount(1).assertValueAt(0) {
            it.getRoom(1).numberOfChildren == 0
        }
    }

    @Test
    fun onInfantsNumberIncreased() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testObserver = viewModel.states().test()

        viewModel.onInfantsNumberIncreased(1)

        testObserver.assertValueAt(1) { it.getRoom(1).numberOfInfants == 1 }
    }

    @Test
    fun `onInfantsNumberIncreased failed due to Max Range Constraint Reached`() {

        every { criteriaMessageProvider.maxInfantsErrorTitle } returns "some message"
        every { criteriaMessageProvider.getMaxInfantsErrorMessage(any()) } returns "some message"
        every { getStringResource(any()) } returns "some message"

        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()
        val testEventObserver = viewModel.events().test()

        viewModel.onInfantsNumberIncreased(1)
        viewModel.onInfantsNumberIncreased(1)

        testStateObserver.assertValueCount(2).assertValueAt(1) { it.getRoom(1).numberOfInfants == 1 }
        testEventObserver.assertValue { it is MaxInfantsConstraint && it.roomId == 1 }
    }

    @Test
    fun onInfantsNumberDecreased() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 1, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onInfantsNumberDecreased(1)

        testStateObserver.assertValueAt(1) { it.getRoom(1).numberOfInfants == 0 }
    }

    @Test
    fun `onInfantsNumberDecreased failed due to minimum range constraint reached`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onInfantsNumberDecreased(1)

        testStateObserver.assertValueCount(1).assertValueAt(0) {
            it.getRoom(1).numberOfInfants == 0
        }
    }

    @Test
    fun `onCotEnabled returns state with cot Included`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, cotIncluded = false))

        val testStateObserver = viewModel.states().test()

        viewModel.onCotEnabled(1, true)

        testStateObserver.assertValueCount(2).assertValueAt(1) { it.getRoom(1).includeCot }
    }

    @Test
    fun `onCotEnabled returns state with cot not included`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 0, cotIncluded = true))

        val testStateObserver = viewModel.states().test()

        viewModel.onCotEnabled(1, false)

        testStateObserver.assertValueCount(2).assertValueAt(1) { !it.getRoom(1).includeCot }
    }

    @Test
    fun `onCotEnabled attempt when roomtype is ACCESSIBLE returns an AccessibleCotConstraint event`() {

        every { criteriaMessageProvider.accessibleCotTitle } returns "some message"
        every { criteriaMessageProvider.getAccessibleCotMessage(any()) } returns "some message"
        every { getStringResource(any()) } returns "some message"

        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 2, children = 0, infants = 1, cotIncluded = true, roomType = ACCESSIBLE))

        val testStateObserver = viewModel.states().test()
        val testEventObserver = viewModel.events().test()

        viewModel.onCotEnabled(1, true)

        testStateObserver.assertValueCount(1)

        testEventObserver.assertValue {
            it is AccessibleCotConstraint
        }
    }


    @Test
    fun `onRoomTypeChanged allowed return state with roomType selected`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 1, children = 0, infants = 0, cotIncluded = false, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onRoomTypeChanged(1, SINGLE)

        testStateObserver.assertValueCount(2)
                .assertValueAt(1) { it.getRoom(1).roomType == SINGLE }
    }

    @Test
    fun `onRoomTypeChanged not allowed return state with roomType selected`() {
        val viewModel = BaseRoomCriteriaViewModel(
                getStringResource = getStringResource,
                criteriaMessageProvider = criteriaMessageProvider,
                initState = createRoom1(adults = 1, children = 0, infants = 0, cotIncluded = true, roomType = DOUBLE))

        val testStateObserver = viewModel.states().test()

        viewModel.onRoomTypeChanged(1, SINGLE)

        testStateObserver.assertValueCount(1)
                .assertValueAt(0) { it.getRoom(1).roomType == DOUBLE }
    }

    @Test
    fun `when business user reaches room limit then add room button is hidden and info box is shown`() {
        val context = mockk<android.content.Context>()
        val resources = mockk<android.content.res.Resources>()
        every { context.resources } returns resources
        every { resources.getQuantityString(any(), any(), any()) } returns ""
        every { context.getString(any()) } returns ""
        every { context.getString(any(), any()) } returns ""
        every { context.getString(any(), any(), any()) } returns ""

        val businessPersistenceManager =
            mockk<com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager>()
        mockk<com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager>()
        val getStringResource = mockk<GetStringResource>()
        val isCustomerLoggedIn = mockk<IsCustomerLoggedIn>()
        val analytics =
            mockk<com.whitbread.premierinn.common.analytics.TrackingAnalytics>(relaxed = true)

        every { getStringResource(Key.NON_CHARGEABLE_PHONE_DESC) } returns "Non-chargeable phone description"
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns "Group bookings number"

        val businessRoomLimit = 1
        val groupBookingsNumber = "0800 123 456"
        val expectedTitle = "Unable to add more rooms"
        val expectedMessage =
            "For a group booking of $businessRoomLimit to 9 rooms, call us on $groupBookingsNumber."

        // Mock business logic
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "business@company.com"
        every { businessPersistenceManager.getMaxRoomsInnBusiness() } returns businessRoomLimit
        every {
            context.getString(com.whitbread.premierinn.R.string.max_rooms_title)
        } returns expectedTitle
        every {
            context.getString(
                com.whitbread.premierinn.R.string.max_rooms_message_up_to_9_rooms,
                businessRoomLimit,
                groupBookingsNumber
            )
        } returns expectedMessage

        val roomCriteriaHelper = mockk<RoomCriteriaHelper>()
        every { roomCriteriaHelper.getMaxRoomsErrorMessage(any()) } returns expectedMessage
        every { roomCriteriaHelper.isMaxRoomsGroupFormRequired() } returns false

        // Mock GetStringResource for group bookings number
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns groupBookingsNumber

        // Set the room limit for the test scenario
        RoomCriteriaOrderedCollection.setMaxRooms(businessRoomLimit)

        val viewModel = RoomCriteriaViewModel(
            savedStateHandle = savedStateHandle,
            getStringResource = getStringResource,
            roomCriteriaHelper = roomCriteriaHelper,
            analytics = analytics,
            isCustomerLoggedIn = isCustomerLoggedIn
        )

        // Subscribe to events before triggering the action
        val testObserver = viewModel.events().test()
        viewModel.onRoomAdded()
        val events = testObserver.values()
        assertTrue("No events emitted when room limit is reached.", events.isNotEmpty())
        val event = events.last()
        assertTrue(event is RoomCriteriaViewModel.MaxRoomConstraintEvent)
        event as RoomCriteriaViewModel.MaxRoomConstraintEvent
        assertEquals(expectedMessage, event.message)
        assertEquals(groupBookingsNumber, event.phoneNumber)
        assertEquals(false, event.isGroupFormRequired)
        // Optionally, check that the number of rooms did not increase
        assertEquals(businessRoomLimit, viewModel.currentState().getRooms().size)
    }

    @Test
    fun `business customer cannot exceed custom room limit`() {
        val businessRoomLimit = 1
        RoomCriteriaOrderedCollection.setMaxRooms(businessRoomLimit)
        val getStringResource = mockk<GetStringResource>()
        every { getStringResource(Key.NON_CHARGEABLE_PHONE_DESC) } returns "Non-chargeable phone description"
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns "Group bookings number"
        val isCustomerLoggedIn = mockk<IsCustomerLoggedIn>()
        val analytics =
            mockk<com.whitbread.premierinn.common.analytics.TrackingAnalytics>(relaxed = true)
        val roomCriteriaHelper = mockk<RoomCriteriaHelper>()
        every { roomCriteriaHelper.getMaxRoomsErrorMessage(any()) } returns "Max rooms error message"
        every { roomCriteriaHelper.isMaxRoomsGroupFormRequired() } returns false
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true

        val viewModel = RoomCriteriaViewModel(
            savedStateHandle = savedStateHandle,
            getStringResource = getStringResource,
            roomCriteriaHelper = roomCriteriaHelper,
            analytics = analytics,
            isCustomerLoggedIn = isCustomerLoggedIn
        )
        val testObserver = viewModel.events().test()
        viewModel.onRoomAdded()
        val events = testObserver.values()
        assertTrue(events.isNotEmpty())
        val event = events.last()
        assertTrue(event is RoomCriteriaViewModel.MaxRoomConstraintEvent)
        assertEquals(businessRoomLimit, viewModel.currentState().getRooms().size)
    }

    private companion object RoomCriteriaStateFactory {
        fun createRoom1(adults: Int,
                        children: Int,
                        infants: Int,
                        cotIncluded: Boolean = false,
                        roomType: RoomType = DOUBLE): RoomCriteriaState {
            return RoomCriteriaState(roomCriteriaCollection = RoomCriteriaOrderedCollection.createFromExisting(
                    listOf(RoomCriteria(
                            roomNumber = 1,
                            numberOfAdults = adults,
                            numberOfChildren = children,
                            numberOfInfants = infants,
                            includeCot = cotIncluded,
                            roomType = roomType
                    ))
            ))
        }
    }
}