package com.whitbread.premierinn.reviewbooking;

import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;
import com.whitbread.premierinn.common.RoomBooking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewBookModelTest {

    private static final String HOTEL_IMAGE_REFERENCE = "pi/image/ref";
    private static final String HOTEL_NAME = "Tower Bridge";
    private final LocalDate arrivalDate = LocalDate.of(2020, 5, 6);
    private final int numAdults = 12;
    private final int numChildren = 9;

    @Mock private RoomBooking bookingRoomMock;
    @Mock private ReviewBookingInput reviewBookingInput;
    @Mock private BookingFlowInput bookingFlowInput;
    @Mock private PaymentDetailsInput paymentDetailsInput;
    @Mock private DeviceLocaleProvider deviceLocaleProvider;

    private ReviewBookModel reviewBookModel;

    @Before
    public void onSetup() {
        when(bookingFlowInput.arrivalDate()).thenReturn(arrivalDate);
        when(bookingFlowInput.numGuests()).thenReturn(numAdults + numChildren);
        when(bookingFlowInput.numNights()).thenReturn(1);
        when(reviewBookingInput.paymentDetailsInput()).thenReturn(paymentDetailsInput);
        when(paymentDetailsInput.bookingFlowInput()).thenReturn(bookingFlowInput);
        when(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK);
    }

    @Test
    public void testModelCreation() {
        when(bookingFlowInput.roomBookings()).thenReturn(Collections.singletonList(bookingRoomMock));
        when(bookingFlowInput.hotelImageReference()).thenReturn(HOTEL_IMAGE_REFERENCE);
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        float cardFeeValue = 1.0f;
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);
        reviewBookModel = new ReviewBookModel(reviewBookingInput, cardFeeValue,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");
        assertEquals(Urls.CONTENT_BASE_URL + HOTEL_IMAGE_REFERENCE, reviewBookModel.getHotelImageUrl());
        assertEquals(HOTEL_NAME, reviewBookModel.getHotelName());
        assertEquals("6 May", reviewBookModel.getArrivalDateFormatted());
        assertEquals("7 May", reviewBookModel.getDepartureDateFormatted());
        assertEquals(numAdults + numChildren, reviewBookModel.getGuests());
        assertEquals(1, reviewBookModel.getRooms());
        assertEquals(0f, reviewBookModel.getDonation().getAmount());
        assertEquals("GBP", reviewBookModel.getDonation().getCurrency());
        assertEquals(totalPrice, reviewBookModel.getTotalPrice());
    }

    @Test
    public void whenNoRoomsThenFlagAllRoomsAreAccessibleIsFalse() {
        when(bookingFlowInput.roomBookings()).thenReturn(Collections.singletonList(bookingRoomMock));
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        when(bookingFlowInput.roomBookings()).thenReturn(Collections.emptyList());
        float cardFeeValue = 1.0f;
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);
        reviewBookModel = new ReviewBookModel(reviewBookingInput, cardFeeValue,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(false, reviewBookModel.getAllRoomsAreAccessible());
    }

    private List<RoomBooking> mockRooms(RoomType... roomTypes) {
        List<RoomBooking> rooms = new ArrayList<>();
        for (RoomType type : roomTypes) {
            RoomBooking roomBooking = mock(RoomBooking.class);
            rooms.add(roomBooking);
        }
        return rooms;
    }

    @Test
    public void whenOnlyOneRoomIsAccessibleRoomThenFlagAllRoomsAreAccessibleAsFalse() {
        when(bookingFlowInput.roomBookings()).thenReturn(Collections.emptyList());
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedStdRooms = mockRooms(RoomType.DOUBLE);
        List<RoomBooking> mockedAccRooms = mockRooms(RoomType.ACCESSIBLE, RoomType.ACCESSIBLE);
        when(bookingFlowInput.roomBookings()).thenReturn(mockedStdRooms);
        when(bookingFlowInput.accessibleRoomBookings()).thenReturn(mockedAccRooms);

        float cardFeeValue = 1.0f;
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);
        reviewBookModel = new ReviewBookModel(reviewBookingInput, cardFeeValue,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(false, reviewBookModel.getAllRoomsAreAccessible());
    }

    @Test
    public void whenOneSingleRoomThenFlagAllRoomsAreAccessibleIsFalse() {
        when(bookingFlowInput.roomBookings()).thenReturn(Collections.singletonList(bookingRoomMock));
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedRooms = mockRooms(RoomType.SINGLE);
        when(bookingFlowInput.roomBookings()).thenReturn(mockedRooms);
        float cardFeeValue = 1.0f;
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);
        reviewBookModel = new ReviewBookModel(reviewBookingInput, cardFeeValue,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(false, reviewBookModel.getAllRoomsAreAccessible());
    }

    @Test
    public void whenOneSingleRoomAndOneIsAccessibleThenFlagAllRoomsAreAccessibleIsFalse() {
        when(bookingFlowInput.roomBookings()).thenReturn(Collections.singletonList(bookingRoomMock));
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedRooms = mockRooms(RoomType.SINGLE, RoomType.ACCESSIBLE);
        when(bookingFlowInput.roomBookings()).thenReturn(mockedRooms);
        float cardFeeValue = 1.0f;
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);
        reviewBookModel = new ReviewBookModel(reviewBookingInput, cardFeeValue,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(false, reviewBookModel.getAllRoomsAreAccessible());
    }

    @Test
    public void whenOnlyStdRoom_thenRoomsReturnedIsCorrect() {
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedRooms = mockRooms(RoomType.DOUBLE, RoomType.FAMILY);
        when(bookingFlowInput.roomBookings()).thenReturn(mockedRooms);
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);

        reviewBookModel = new ReviewBookModel(reviewBookingInput, 0f,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(2, reviewBookModel.getRooms());
    }

    @Test
    public void whenStdAndTwinRoom_thenRoomsReturnedIsCorrect() {
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedRooms = mockRooms(RoomType.DOUBLE);
        List<RoomBooking> mockedTwinRooms = mockRooms(RoomType.TWIN);

        when(bookingFlowInput.roomBookings()).thenReturn(mockedRooms);
        when(bookingFlowInput.twinRoomBookings()).thenReturn(mockedTwinRooms);
        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);

        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);

        reviewBookModel = new ReviewBookModel(reviewBookingInput, 0f,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(2, reviewBookModel.getRooms());
    }

    @Test
    public void whenStdAndAccRoom_thenRoomsReturnedIsCorrect() {
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);

        List<RoomBooking> mockedRooms = mockRooms(RoomType.DOUBLE, RoomType.SINGLE);
        List<RoomBooking> mockedAccRooms = mockRooms(RoomType.ACCESSIBLE);
        when(bookingFlowInput.roomBookings()).thenReturn(mockedRooms);
        when(bookingFlowInput.accessibleRoomBookings()).thenReturn(mockedAccRooms);

        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);

        reviewBookModel = new ReviewBookModel(reviewBookingInput, 0f,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(3, reviewBookModel.getRooms());
    }

    @Test
    public void whenAccAndTwin_thenRoomsReturnedIsCorrect() {
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedAccRooms = mockRooms(RoomType.ACCESSIBLE);
        List<RoomBooking> mockedTwinRooms = mockRooms(RoomType.TWIN);

        when(bookingFlowInput.accessibleRoomBookings()).thenReturn(mockedAccRooms);
        when(bookingFlowInput.twinRoomBookings()).thenReturn(mockedTwinRooms);

        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);

        reviewBookModel = new ReviewBookModel(reviewBookingInput, 0f,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(2, reviewBookModel.getRooms());
    }

    @Test
    public void whenOnlyAcc_thenRoomsReturnedIsCorrect() {
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedAccRooms = mockRooms(RoomType.ACCESSIBLE, RoomType.ACCESSIBLE, RoomType.ACCESSIBLE);

        when(bookingFlowInput.accessibleRoomBookings()).thenReturn(mockedAccRooms);

        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);

        reviewBookModel = new ReviewBookModel(reviewBookingInput, 0f,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(true, reviewBookModel.getAllRoomsAreAccessible());
        assertEquals(3, reviewBookModel.getRooms());
    }

    @Test
    public void whenOnlyTwin_thenRoomsReturnedIsCorrect() {
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        List<RoomBooking> mockedTwinRooms = mockRooms(RoomType.TWIN, RoomType.TWIN);
        when(bookingFlowInput.twinRoomBookings()).thenReturn(mockedTwinRooms);

        PriceDomain totalPrice = PriceDomain.Companion.createWithGBPCurrency(123.1f);
        when(bookingFlowInput.totalRoomsCost(false)).thenReturn(totalPrice);

        reviewBookModel = new ReviewBookModel(reviewBookingInput, 0f,
                PriceDomain.Companion.createDefault(), totalPrice, deviceLocaleProvider, "any pledge");

        assertEquals(2, reviewBookModel.getRooms());
    }

}