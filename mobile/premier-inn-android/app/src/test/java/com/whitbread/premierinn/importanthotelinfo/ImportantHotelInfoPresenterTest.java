package com.whitbread.premierinn.importanthotelinfo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.api.response.booking.BookingNote;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

@RunWith(MockitoJUnitRunner.class)
public class ImportantHotelInfoPresenterTest {

    private static final int PRIORITY_THREE = 3;
    private static final int PRIORITY_TWO = 2;
    private static final int PRIORITY_ONE = 1;
    @Mock
    ImportantHotelInfoPresenter.View viewMock;
    @Mock
    BookingNote bookingNoteMock1;
    @Mock
    BookingNote bookingNoteMock2;
    @Mock
    BookingNote bookingNoteMock3;

    @Mock
    InfoItem infoItemMock1;
    @Mock
    InfoItem infoItemMock2;
    @Mock
    InfoItem infoItemMock3;

    private final CompositeDisposable viewDisposable = new CompositeDisposable();
    private ImportantHotelInfoPresenter presenter;

    private List<BookingNote> notes;
    private List<InfoItem> infoItems;
    private LocalDate arrivalDate;
    private LocalDate departureDate;

    @Before
    public void onSetup() {
        notes = new ArrayList<>();
        infoItems = new ArrayList<>();
        arrivalDate = LocalDate.of(2026, 10, 10);
        departureDate = LocalDate.of(2024, 10, 15);

        presenter = new ImportantHotelInfoPresenter(viewDisposable);
        presenter.initParams(notes, infoItems, arrivalDate, departureDate);
        when(viewMock.onCrossClick()).thenReturn(Observable.never());
    }

    @Test
    public void testShowSortedBookingNotes() {
        notes.add(bookingNoteMock3);
        when(bookingNoteMock3.priority()).thenReturn(PRIORITY_THREE);
        notes.add(bookingNoteMock2);
        when(bookingNoteMock2.priority()).thenReturn(PRIORITY_TWO);
        notes.add(bookingNoteMock1);
        when(bookingNoteMock1.priority()).thenReturn(PRIORITY_ONE);

        presenter.attachView(viewMock);

        verify(viewMock).showBookingNotes(notes);
        assertEquals(PRIORITY_ONE, notes.get(0).priority());
        assertEquals(PRIORITY_TWO, notes.get(1).priority());
        assertEquals(PRIORITY_THREE, notes.get(2).priority());
    }

    @Test
    public void testShowSortedInfoItems() {
        infoItems.add(infoItemMock1);
        when(infoItemMock1.getPriority()).thenReturn(String.valueOf(PRIORITY_ONE));
        when(infoItemMock1.getStartDate()).thenReturn("10/05/2023");
        when(infoItemMock1.getEndDate()).thenReturn("20/11/2043");
        infoItems.add(infoItemMock2);
        when(infoItemMock2.getPriority()).thenReturn(String.valueOf(PRIORITY_TWO));
        when(infoItemMock2.getStartDate()).thenReturn("26/03/2023");
        when(infoItemMock2.getEndDate()).thenReturn("10/06/2028");
        infoItems.add(infoItemMock3);
        when(infoItemMock3.getPriority()).thenReturn(String.valueOf(PRIORITY_THREE));
        when(infoItemMock3.getStartDate()).thenReturn("06/09/2023");
        when(infoItemMock3.getEndDate()).thenReturn("20/12/2030");

        presenter.attachView(viewMock);

        verify(viewMock).showImportantInfo(infoItems);
        assertEquals(String.valueOf(PRIORITY_ONE), infoItems.get(0).getPriority());
        assertEquals(String.valueOf(PRIORITY_TWO), infoItems.get(1).getPriority());
        assertEquals(String.valueOf(PRIORITY_THREE), infoItems.get(2).getPriority());
    }

    @Test
    public void testLifeCycle() {
        assertEquals(0, viewDisposable.size());
        assertFalse(presenter.isViewAttached());
        presenter.attachView(viewMock);
        assertEquals(1, viewDisposable.size());
        assertTrue(presenter.isViewAttached());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewDisposable.size());
    }
}
