package com.whitbread.premierinn.paymentdetails;

import com.whitbread.premierinn.api.request.booking.BookingAddress;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PaymentDetailsInputTest {

    @Mock private BookingFlowInput bookingFlowInputMock;
    @Mock private GuestDetailsFormDataInput bookerDetailsMock;

    private boolean isBookerStaying = false;
    private String countryCode = "GB";
    private String countryIsoCode = "GB";

    private String postcode = "EC1A 2HB";
    private String addressLine1 = "120 High Holborn";
    private String addressLine2 = "Chancery Lane";
    private String addressLine3 = "London";
    private List<GuestDetailsFormDataInput> guestDetailsFormDataInputs = Collections.singletonList(bookerDetailsMock);

    @Test
    public void testCreate() {
        PaymentDetailsInput paymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInputMock)
                .address(BookingAddress.create(countryIsoCode, countryCode, addressLine1, addressLine2, addressLine3, postcode))
                .bookerDetails(bookerDetailsMock)
                .guestDetailsList(guestDetailsFormDataInputs)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .isBookerStaying(isBookerStaying).build();

        assertEquals(bookingFlowInputMock, paymentDetailsInput.bookingFlowInput());
        assertEquals(bookerDetailsMock, paymentDetailsInput.bookerDetails());
        assertEquals(guestDetailsFormDataInputs, paymentDetailsInput.guestDetailsList());
        assertEquals(isBookerStaying, paymentDetailsInput.isBookerStaying());
        assertEquals(countryCode, paymentDetailsInput.address().countryCode());
        assertEquals(postcode, paymentDetailsInput.address().postcode());
        assertEquals(addressLine1, paymentDetailsInput.address().line1());
        assertEquals(addressLine2, paymentDetailsInput.address().line2());
        assertEquals(addressLine3, paymentDetailsInput.address().city());
    }

    @Test
    public void testCreateBusinessTrip() {
        PaymentDetailsInput paymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInputMock)
                .address(BookingAddress.create(countryIsoCode, countryCode, addressLine1, addressLine2, addressLine3, postcode))
                .bookerDetails(bookerDetailsMock)
                .guestDetailsList(guestDetailsFormDataInputs)
                .isBusinessTrip(true)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .isBookerStaying(isBookerStaying).build();

        assertEquals(true, paymentDetailsInput.isBusinessTrip());
        assertEquals(false, paymentDetailsInput.isTaxExempt());
        assertEquals(bookingFlowInputMock, paymentDetailsInput.bookingFlowInput());
        assertEquals(bookerDetailsMock, paymentDetailsInput.bookerDetails());
        assertEquals(guestDetailsFormDataInputs, paymentDetailsInput.guestDetailsList());
        assertEquals(isBookerStaying, paymentDetailsInput.isBookerStaying());
        assertEquals(countryCode, paymentDetailsInput.address().countryCode());
        assertEquals(postcode, paymentDetailsInput.address().postcode());
        assertEquals(addressLine1, paymentDetailsInput.address().line1());
        assertEquals(addressLine2, paymentDetailsInput.address().line2());
        assertEquals(addressLine3, paymentDetailsInput.address().city());
    }

    @Test
    public void testCreateBusinessTripWithTaxExempt() {
        PaymentDetailsInput paymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInputMock)
                .address(BookingAddress.create(countryIsoCode, countryCode, addressLine1, addressLine2, addressLine3, postcode))
                .bookerDetails(bookerDetailsMock)
                .guestDetailsList(guestDetailsFormDataInputs)
                .isBusinessTrip(true)
                .isTaxExempt(true)
                .marketingOptIn(true)
                .isBookerStaying(isBookerStaying).build();

        assertEquals(true, paymentDetailsInput.isBusinessTrip());
        assertEquals(true, paymentDetailsInput.isTaxExempt());
        assertEquals(bookingFlowInputMock, paymentDetailsInput.bookingFlowInput());
        assertEquals(bookerDetailsMock, paymentDetailsInput.bookerDetails());
        assertEquals(guestDetailsFormDataInputs, paymentDetailsInput.guestDetailsList());
        assertEquals(isBookerStaying, paymentDetailsInput.isBookerStaying());
        assertEquals(countryCode, paymentDetailsInput.address().countryCode());
        assertEquals(postcode, paymentDetailsInput.address().postcode());
        assertEquals(addressLine1, paymentDetailsInput.address().line1());
        assertEquals(addressLine2, paymentDetailsInput.address().line2());
        assertEquals(addressLine3, paymentDetailsInput.address().city());
    }
}
