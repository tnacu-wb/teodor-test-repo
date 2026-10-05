package com.whitbread.premierinn.summarybreakdown;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.hoteldetails.DailyRateInput;
import com.whitbread.premierinn.common.RoomBooking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class SummaryBreakdownRoomTest {

    @Mock
    RoomBooking bookingRoomMock;
    @Mock
    DailyRateInput dailyRateMock;
    @Mock
    PriceDomain totalCostMock;
    @Mock
    DeviceLocaleProvider deviceLocaleProviderMock;

    private final LocalDate arrivalDate = LocalDate.of(2012, 10, 4);
    private final int adults = 1;
    private final int children = 1;
    private final boolean cot = true;
    private final String type = RoomType.DOUBLE.getCode();
    private final float amount = 123.4f;
    private final String currency = "EUR";
    private List<RoomBooking> bookingRooms;

    @Before
    public void onSetup() {
        bookingRooms = Collections.singletonList(bookingRoomMock);
        List<DailyRateInput> dailyRates = Collections.singletonList(dailyRateMock);
        when(bookingRoomMock.getDailyRates()).thenReturn(dailyRates);
        when(deviceLocaleProviderMock.getDeviceLocale()).thenReturn(Locale.UK);
    }

    @Test
    public void testCreateSummaryBreakdownRooms() throws ParseException {
        when(dailyRateMock.getDate()).thenReturn(arrivalDate);
        when(bookingRoomMock.getCot()).thenReturn(cot);
        when(bookingRoomMock.getAdults()).thenReturn(1);
        when(bookingRoomMock.getChildren()).thenReturn(1);

        when(bookingRoomMock.totalRoomPrice(false)).thenReturn(totalCostMock);
        when(totalCostMock.getAmount()).thenReturn(amount);
        when(totalCostMock.getCurrency()).thenReturn(currency);

        when(bookingRoomMock.getType()).thenReturn(RoomType.DOUBLE.getCode());
        List<SummaryBreakdownRoom> summaryBreakdownRooms = SummaryBreakdownRoom
                .createSummaryBreakdownRooms(bookingRooms, null,
                        null, arrivalDate, false, deviceLocaleProviderMock);
        SummaryBreakdownRoom summaryBreakdownRoom = summaryBreakdownRooms.get(0);

        assertEquals("4 Oct", summaryBreakdownRoom.formattedArrivalDate());
        assertEquals("5 Oct", summaryBreakdownRoom.formattedDepartureDate());
        assertEquals(type, summaryBreakdownRoom.roomType());
        assertEquals(cot, summaryBreakdownRoom.cot());
        assertEquals(adults, summaryBreakdownRoom.adults());
        assertEquals(children, summaryBreakdownRoom.children());
        // JVM PriceDomain format gives inconsistent results, so check both options
        assertTrue(Arrays.asList("€123.40", "EUR123.40").contains(summaryBreakdownRoom.currencyAndPrice()));
    }

    @Test
    public void getRoomDescriptionStringId() throws ParseException {
        when(dailyRateMock.getDate()).thenReturn(arrivalDate);
        when(bookingRoomMock.getCot()).thenReturn(cot);
        when(bookingRoomMock.getAdults()).thenReturn(1);
        when(bookingRoomMock.getChildren()).thenReturn(1);

        when(bookingRoomMock.totalRoomPrice(false)).thenReturn(totalCostMock);
        when(totalCostMock.getAmount()).thenReturn(amount);
        when(totalCostMock.getCurrency()).thenReturn(currency);

        when(bookingRoomMock.getType()).thenReturn(RoomType.DOUBLE.getCode());
        List<SummaryBreakdownRoom> summaryBreakdownRooms = SummaryBreakdownRoom
                .createSummaryBreakdownRooms(bookingRooms, null,
                        null, arrivalDate, false, deviceLocaleProviderMock);
        assertEquals(R.string.criteria_room_type_double, summaryBreakdownRooms.get(0).getRoomDescriptionStringId());

        when(bookingRoomMock.getType()).thenReturn(RoomType.SINGLE.getCode());
        summaryBreakdownRooms = SummaryBreakdownRoom.createSummaryBreakdownRooms(bookingRooms,
                null, null, arrivalDate, false, deviceLocaleProviderMock);
        assertEquals(R.string.criteria_room_type_single, summaryBreakdownRooms.get(0).getRoomDescriptionStringId());

        when(bookingRoomMock.getType()).thenReturn(RoomType.TWIN.getCode());
        summaryBreakdownRooms = SummaryBreakdownRoom.createSummaryBreakdownRooms(bookingRooms,
                null, null, arrivalDate, false, deviceLocaleProviderMock);
        assertEquals(R.string.criteria_room_type_twin, summaryBreakdownRooms.get(0).getRoomDescriptionStringId());

        when(bookingRoomMock.getType()).thenReturn(RoomType.FAMILY.getCode());
        summaryBreakdownRooms = SummaryBreakdownRoom.createSummaryBreakdownRooms(bookingRooms,
                null, null, arrivalDate, false, deviceLocaleProviderMock);
        assertEquals(R.string.criteria_room_type_family, summaryBreakdownRooms.get(0).getRoomDescriptionStringId());

        when(bookingRoomMock.getType()).thenReturn(RoomType.ACCESSIBLE.getCode());
        summaryBreakdownRooms = SummaryBreakdownRoom.createSummaryBreakdownRooms(bookingRooms,
                null, null, arrivalDate, false, deviceLocaleProviderMock);
        assertEquals(R.string.criteria_room_type_accessible, summaryBreakdownRooms.get(0).getRoomDescriptionStringId());
    }
}
