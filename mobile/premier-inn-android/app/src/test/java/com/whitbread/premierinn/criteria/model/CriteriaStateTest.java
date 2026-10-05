package com.whitbread.premierinn.criteria.model;

import com.whitbread.premierinn.criteria.CallUsConfig;
import com.whitbread.premierinn.criteria.view.NumberSelectorViewInput;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CriteriaStateTest {

    private static final String SEARCH = "search";
    private static final NumberSelectorViewInput DUMP_INPUT = NumberSelectorViewInput.builder()
        .value(0)
        .maxNumberInfo(null)
        .minusEnabled(false)
        .plusEnabled(false).build();

    @Test
    public void testDefaultValue() {
        CriteriaState criteriaState = CriteriaState.createDefault(SEARCH);
        assertEquals(LocalDate.now(), criteriaState.arrivalDate());
        assertEquals(CriteriaState.MIN_ALLOWED_NIGHTS, criteriaState.nights().value());
        assertEquals(1, criteriaState.roomConfigurations().size());
        RoomConfiguration expectedConfig = RoomConfigurationCreator.INSTANCE.createDefault(false);
        assertEquals(expectedConfig.adults(), criteriaState.roomConfigurations().get(0).adults());
        assertEquals(expectedConfig.children(), criteriaState.roomConfigurations().get(0).children());
        assertEquals(expectedConfig.roomType(), criteriaState.roomConfigurations().get(0).roomType());
        assertEquals(SEARCH, criteriaState.searchedText());
    }

    @Test
    public void testUpdateRoomConfigurationConstructor() {
        CriteriaState criteriaState = CriteriaState.createDefault(SEARCH);
        List<RoomConfiguration> rooms = new ArrayList<>();
        CriteriaState expectedState = CriteriaState.updateRoomConfigurations(criteriaState, rooms);
        assertEquals(LocalDate.now(), expectedState.arrivalDate());
        assertEquals(CriteriaState.MIN_ALLOWED_NIGHTS, expectedState.nights().value());
        assertEquals(rooms, expectedState.roomConfigurations());
    }

    @Test
    public void testUpdateTooManyRoomsMessage() {
        CriteriaState criteriaState = CriteriaState.createDefault(SEARCH);
        assertNull(criteriaState.tooManyRoomsMessage());
        String message = "an error message";
        CallUsConfig callUsConfig = new CallUsConfig("", "");
        TooManyRoomsMessage tooManyRoomsMessage = new TooManyRoomsMessage(message, callUsConfig);
        criteriaState = CriteriaState.updateTooManyRoomsMessage(criteriaState, tooManyRoomsMessage);
        assertEquals(message, criteriaState.tooManyRoomsMessage().getMessageText());
    }

    @Test
    public void testGetMaxAllowedNights() {
        CriteriaState expected = CriteriaState.create(SEARCH, LocalDate.now(), DUMP_INPUT, new ArrayList<>(), null);
        assertEquals(CriteriaState.MAX_ALLOWED_NIGHTS, expected.getMaxAllowedNightsWithOneYearLimit());

        Observable
                .range(1, CriteriaState.MAX_ALLOWED_NIGHTS)
                .subscribe(i -> {
                    LocalDate oneYearFromTodayMinusADay = LocalDate.now().plusYears(1).minusDays(i);
                    CriteriaState expectedState = CriteriaState.create(SEARCH, oneYearFromTodayMinusADay,
                            DUMP_INPUT, new ArrayList<>(), null);
                    assertEquals(i.intValue(), expectedState.getMaxAllowedNightsWithOneYearLimit());
                });
    }

    @Test
    public void testGetDepartureDate() {
        CriteriaState criteriaState = CriteriaState.create(SEARCH, LocalDate.now(), DUMP_INPUT, new ArrayList<>(), null);
        assertEquals(LocalDate.now(), criteriaState.getDepartureDate());

        NumberSelectorViewInput numberSelectorViewInput = NumberSelectorViewInput.builder()
                .value(4)
                .minusEnabled(false)
                .plusEnabled(false).build();

        criteriaState = CriteriaState.create(SEARCH, LocalDate.of(2016, 2, 1),
                numberSelectorViewInput, new ArrayList<>(), null);
        assertEquals(LocalDate.of(2016, 2, 5), criteriaState.getDepartureDate());
    }

    @Test
    public void testCanAddMoreRooms() {
        Observable.range(1, CriteriaState.MAX_ROOMS)
                .flatMapSingle(integer -> Observable.range(1, integer)
                        .map(roomNumber -> RoomConfigurationCreator.INSTANCE.create(roomNumber.intValue(), false))
                        .toList())
                .map(roomConfigurations -> CriteriaState.create(SEARCH, LocalDate.now(), DUMP_INPUT, roomConfigurations, null))
                .subscribe(criteriaState -> {
                    if (criteriaState.roomConfigurations().size() < CriteriaState.MAX_ROOMS) {
                        assertTrue(criteriaState.canAddMoreRooms());
                    } else {
                        assertFalse(criteriaState.canAddMoreRooms());
                    }
                });
    }
}
