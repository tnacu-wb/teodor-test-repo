package com.whitbread.premierinn.api.request;

import com.whitbread.premierinn.api.request.booking.Breakfast;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.common.RoomBooking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BreakfastTest {

    private static final String UPSELL_ITEM_CODE = "123";

    private List<RoomBooking> rooms = new ArrayList<>();

    @Mock UpsellItem upsellItem;
    @Mock RoomBooking room;

    @Before
    public void setup() {
        when(upsellItem.code()).thenReturn(UPSELL_ITEM_CODE);
        rooms.add(room);
    }

    @Test
    public void testNullUpsellItemReturnsEmptyList() {
        List<Breakfast> breakfastList = Breakfast.createBreakfasts(null, new ArrayList<>());
        assertTrue(breakfastList.isEmpty());
    }

    @Test
    public void testChildrenExcludedFromMainMealIfFreeAndAddedSeparatelyWithNoAdult() {
        when(upsellItem.freeBreakfastCode()).thenReturn("15");
        when(upsellItem.availableForChildren()).thenReturn(true);
        when(upsellItem.freeBreakfastOption()).thenReturn(true);
        when(room.getAdults()).thenReturn(2);
        when(room.getChildren()).thenReturn(1);

        List<Breakfast> breakfastList = Breakfast.createBreakfasts(upsellItem, rooms);
        assertEquals(2, breakfastList.size());
        assertEquals(1, breakfastList.get(0).children());
        assertEquals(0, breakfastList.get(0).adults());
        assertEquals(0, breakfastList.get(1).children());
    }

    @Test
    public void testChildrenIncludedIfNotFree() {
        when(upsellItem.availableForChildren()).thenReturn(true);
        when(upsellItem.freeBreakfastOption()).thenReturn(false);
        when(room.getAdults()).thenReturn(2);
        when(room.getChildren()).thenReturn(2);

        List<Breakfast> breakfastList = Breakfast.createBreakfasts(upsellItem, rooms);
        assertEquals(1, breakfastList.size());
        assertEquals(2, breakfastList.get(0).children());
    }

    @Test
    public void testAdultsIncluded() {
        when(upsellItem.availableForChildren()).thenReturn(true);
        when(upsellItem.freeBreakfastOption()).thenReturn(false);
        when(room.getAdults()).thenReturn(2);
        when(room.getChildren()).thenReturn(2);

        List<Breakfast> breakfastList = Breakfast.createBreakfasts(upsellItem, rooms);
        assertEquals(1, breakfastList.size());
        assertEquals(2, breakfastList.get(0).adults());
    }

    @Test
    public void testCodePopulated() {
        when(upsellItem.availableForChildren()).thenReturn(true);
        when(upsellItem.freeBreakfastOption()).thenReturn(false);
        when(room.getAdults()).thenReturn(2);
        when(room.getChildren()).thenReturn(2);

        List<Breakfast> breakfastList = Breakfast.createBreakfasts(upsellItem, rooms);
        assertEquals(1, breakfastList.size());
        assertEquals(UPSELL_ITEM_CODE, breakfastList.get(0).code());
    }
}
