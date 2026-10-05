package com.whitbread.premierinn.criteria.roomselector;

import com.whitbread.premierinn.domain.common.RoomType;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RoomTest {

    @Test
    public void testGetDefaultRoomStates() {
        Map<RoomType, Room.State> roomStates = Room.getDefaultRoomStates();
        assertEquals(roomStates.get(RoomType.ACCESSIBLE), Room.State.AVAILABLE);
        assertEquals(roomStates.get(RoomType.DOUBLE), Room.State.AVAILABLE);
        assertEquals(roomStates.get(RoomType.SINGLE), Room.State.AVAILABLE);
        assertEquals(roomStates.get(RoomType.TWIN), Room.State.NOT_AVAILABLE);
        assertEquals(roomStates.get(RoomType.FAMILY), Room.State.NOT_AVAILABLE);
    }

    @Test
    public void testGetFamilyRoomStates() {
        Map<RoomType, Room.State> roomStates = Room.getFamilyRoomStates();
        assertEquals(roomStates.get(RoomType.ACCESSIBLE), Room.State.NOT_AVAILABLE);
        assertEquals(roomStates.get(RoomType.DOUBLE), Room.State.NOT_AVAILABLE);
        assertEquals(roomStates.get(RoomType.SINGLE), Room.State.NOT_AVAILABLE);
        assertEquals(roomStates.get(RoomType.TWIN), Room.State.NOT_AVAILABLE);
        assertEquals(roomStates.get(RoomType.FAMILY), Room.State.AVAILABLE);
    }

    @Test
    public void fromStringToEnum() throws Exception {
        assertThat(Room.typeLookUp(RoomType.DOUBLE.getCode()), is(RoomType.DOUBLE));
    }

    @Test
    public void testTwinNotAvailableForSingleAdult() {
        Map<RoomType, Room.State> roomAvailability = Room.createRoomAvailabilityList(1, 0, false, false);
        assertEquals(roomAvailability.get(RoomType.TWIN), Room.State.NOT_AVAILABLE);
    }

    @Test
    public void testTwinAvailableForTwoAdults() {
        Map<RoomType, Room.State> roomAvailability = Room.createRoomAvailabilityList(2, 0, false, false);
        assertEquals(roomAvailability.get(RoomType.TWIN), Room.State.AVAILABLE);
    }

    @Test
    public void testSingleAvailableForCot() {
        Map<RoomType, Room.State> roomAvailability = Room.createRoomAvailabilityList(1, 0, true, false);
        assertEquals(roomAvailability.get(RoomType.SINGLE), Room.State.NOT_AVAILABLE);
    }

    @Test
    public void testAccessibleAvailableForCot() {
        Map<RoomType, Room.State> roomAvailability = Room.createRoomAvailabilityList(1, 0, true, false);
        assertEquals(roomAvailability.get(RoomType.ACCESSIBLE), Room.State.AVAILABLE);
    }
}
