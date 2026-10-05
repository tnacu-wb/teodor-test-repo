package com.whitbread.premierinn.common.utils;

import static org.mockito.Mockito.when;

import android.view.View;

import com.whitbread.premierinn.bookingdetails.HotelDetailsAction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RxUtilsTest {

    PublishSubject<Object> clicks = PublishSubject.create();

    @Mock View view;

    @Test
    public void mapToTypeIfTypeIsAllowed_When_Target_Object_Is_Null() {

        when(view.getTag()).thenReturn(null);

        TestObserver testSubscriber = clicks
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(view, String.class))
                .map(HotelDetailsAction::new)
                .test();

        testSubscriber
                .assertNoErrors()
                .assertNotComplete()
                .assertNoValues();

        clicks.onNext(new Object());
        clicks.onNext(new Object());

        testSubscriber
                .assertNoErrors()
                .assertNotComplete()
                .assertNoValues();
    }

    @Test
    public void mapToTypeIfTypeIsAllowed_When_Target_Object_Is_Not_Null_With_Disallowed_Type() {

        when(view.getTag()).thenReturn(1);

        TestObserver testSubscriber =
                clicks.compose(RxUtils.mapViewTagToTypeIfItIsAllowed(view, String.class))
                .test();

        clicks.onNext(new Object());

        testSubscriber
                .assertNoValues()
                .assertNoErrors()
                .assertNotComplete();
    }

    @Test
    public void mapToTypeIfTypeIsAllowed_When_Target_Object_Is_Not_Null_With_Allowed_Type() {
        when(view.getTag()).thenReturn(null);

        HotelDetailsAction hotelDetailsAction = new HotelDetailsAction("test");

        TestObserver testSubscriber = clicks
                .compose(RxUtils.mapViewTagToTypeIfItIsAllowed(view, String.class))
                .map(o -> hotelDetailsAction)
                .test();


        testSubscriber
                .assertNoErrors()
                .assertNotComplete()
                .assertNoValues();

        when(view.getTag()).thenReturn("test");

        clicks.onNext(new Object());
        clicks.onNext(new Object());

        testSubscriber
                .assertValues(hotelDetailsAction, hotelDetailsAction)
                .assertNoErrors()
                .assertNotComplete();
    }
}