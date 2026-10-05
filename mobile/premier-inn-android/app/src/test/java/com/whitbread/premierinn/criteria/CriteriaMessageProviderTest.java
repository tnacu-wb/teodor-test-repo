package com.whitbread.premierinn.criteria;

import android.content.Context;
import android.content.res.Resources;

import com.whitbread.premierinn.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CriteriaMessageProviderTest {

    @Mock
    Context contextMock;
    @Mock
    Resources resourcesMock;

    private CriteriaMessageProvider criteriaMessageProvider;

    @Before
    public void setup() {
        criteriaMessageProvider = new CriteriaMessageProvider(contextMock);
        when(contextMock.getResources()).thenReturn(resourcesMock);
    }

    @Test
    public void testGetOneYearReservationMessage() {
        int numberOfNights = 5;
        String test = "test";
        when(resourcesMock.getQuantityString(R.plurals.criteria_tooltip_message, numberOfNights, numberOfNights)).thenReturn(test);
        assertEquals(test, criteriaMessageProvider.getOneYearReservationMessage(numberOfNights));
    }

    @Test
    public void testGetTooManyNightsMessageWithResponseFromServer() {
        String maxNumberNights = "max number nights";
        when(contextMock.getString(R.string.criteria_max_number_of_nights_message)).thenReturn(maxNumberNights);

        String messageFromServer = "message from server";
        assertEquals(maxNumberNights + " " + messageFromServer, criteriaMessageProvider.getTooManyNightsMessage(messageFromServer));
    }

    @Test
    public void testGetTooManyNightsMessageWithNoResponseFromServer() {
        String maxNumberNights = "max number nights";
        when(contextMock.getString(R.string.criteria_max_number_of_nights_message)).thenReturn(maxNumberNights);
        String defaultMessage = "default message";
        when(contextMock.getString(R.string.call_view_call_us_prompt)).thenReturn(defaultMessage);

        String messageFromServer = "";
        assertEquals(maxNumberNights + " " + defaultMessage, criteriaMessageProvider.getTooManyNightsMessage(messageFromServer));
    }

    @Test
    public void testGetTooManyRoomsMessageWithResponseFromServer() {
        String maxNumberRooms = "max number rooms";
        when(contextMock.getString(R.string.criteria_max_number_of_rooms_message)).thenReturn(maxNumberRooms);

        String messageFromServer = "message from server";
        assertEquals(maxNumberRooms + " " + messageFromServer, criteriaMessageProvider.getTooManyRoomsMessage(messageFromServer));
    }

    @Test
    public void testGetTooManyRoomsMessageWithNoResponseFromServer() {
        String maxNumberRooms = "max number nights";
        when(contextMock.getString(R.string.criteria_max_number_of_rooms_message)).thenReturn(maxNumberRooms);
        String defaultMessage = "default message";
        when(contextMock.getString(R.string.call_view_call_us_prompt)).thenReturn(defaultMessage);

        String messageFromServer = "";
        assertEquals(maxNumberRooms + " " + defaultMessage, criteriaMessageProvider.getTooManyRoomsMessage(messageFromServer));
    }

    @Test
    public void testGetTooManyAdultMessage() {
        String maxNumberAdults = "max number adults";
        when(contextMock.getString(R.string.criteria_max_number_adult)).thenReturn(maxNumberAdults);

        assertEquals(maxNumberAdults, criteriaMessageProvider.getTooManyAdultMessage());
    }

    @Test
    public void testGetTooManyChildrenMessage() {
        String maxNumberChildren = "max number children";
        when(contextMock.getString(R.string.criteria_max_number_children)).thenReturn(maxNumberChildren);

        assertEquals(maxNumberChildren, criteriaMessageProvider.getTooManyChildrenMessage());
    }
}
