package com.whitbread.premierinn.reviewbooking;

import com.whitbread.premierinn.api.request.booking.BookingAddress;
import com.whitbread.premierinn.api.response.CardInfo;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.PaymentProvider;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewBookingInputTest {

    @Mock
    private PaymentDetailsInput paymentDetailsInputMock;
    @Mock
    private CardInfo cardInfo;
    @Mock
    private BookingAddress bookerAddressMock;
    @Mock
    private GuestDetailsFormDataInput guestDetailsFormDataInputMock;
    @Mock
    private BookingFlowInput bookingFlowInputMock;
    @Mock
    private ParcelableDonationsDomain parcelableDonationsDomain;
    private List<GuestDetailsFormDataInput> guestDetailsFormDataInputList = Collections.singletonList(guestDetailsFormDataInputMock);

    private String cardUrl = "www.card.image.visa.com";
    private String cardNumber = "97845298734567895";
    private String nameOnCard = "John Smith";
    private String expiryDate = "0134";
    private String startDate = "0976";
    private String issueNumber = "2";
    private String cvv = "123";
    private String cardType = "AT";
    private boolean storedCard = false;
    private String operaCharityPackageCode = "ZCHRY5";
    private final boolean marketingOptIn = true;
    private final PaymentProvider paymentProvider = PaymentProvider.THREE_C_P;

    @Test
    public void testCreateWithPaymentDetailsInput() {
        ReviewBookingInput reviewBookingInput = ReviewBookingInput.builder()
                .paymentDetailsInput(paymentDetailsInputMock)
                .cardUrl(cardUrl)
                .cardInfo(cardInfo)
                .cardNumber(cardNumber)
                .cvv(cvv)
                .nameOnCard(nameOnCard)
                .expiryDate(expiryDate)
                .cardHolderAddress(bookerAddressMock)
                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW)
                .startDate(startDate)
                .issueNumber(issueNumber)
                .cardType(cardType)
                .marketingOptIn(marketingOptIn)
                .operaDonations(parcelableDonationsDomain)
                .selectedCharityPackageCode(operaCharityPackageCode)
                .build();

        assertEquals(paymentDetailsInputMock, reviewBookingInput.paymentDetailsInput());
        assertEquals(cardUrl, reviewBookingInput.cardUrl());
        assertEquals(cardInfo, reviewBookingInput.cardInfo());
        assertEquals(cardNumber, reviewBookingInput.cardNumber());
        assertEquals(nameOnCard, reviewBookingInput.nameOnCard());
        assertEquals(expiryDate, reviewBookingInput.expiryDate());
        assertEquals(bookerAddressMock, reviewBookingInput.cardHolderAddress());
        assertEquals(startDate, reviewBookingInput.startDate());
        assertEquals(issueNumber, reviewBookingInput.issueNumber());
        assertEquals(marketingOptIn, reviewBookingInput.marketingOptIn().booleanValue());
        assertEquals(parcelableDonationsDomain, reviewBookingInput.operaDonations());
        assertEquals(operaCharityPackageCode, reviewBookingInput.selectedCharityPackageCode());
    }

    @Test
    public void testCreateWithReviewBookingInput() {
        String countryIsoCode = "GB";
        String countryLegacyCode = "GB";
        String postcode = "EC1N 2HB";
        String addressLine1 = "56 Fleet St";
        String addressLine2 = "The City";
        String addressLine3 = "London";

        ReviewBookingInput reviewBookingInput = ReviewBookingInput.builder()
                .paymentDetailsInput(paymentDetailsInputMock)
                .cardUrl(cardUrl)
                .cardInfo(cardInfo)
                .cardNumber(cardNumber)
                .cvv(cvv)
                .nameOnCard(nameOnCard)
                .expiryDate(expiryDate)
                .cardHolderAddress(bookerAddressMock)
                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW)
                .startDate(startDate)
                .issueNumber(issueNumber)
                .cardType(cardType)
                .marketingOptIn(marketingOptIn)
                .operaDonations(parcelableDonationsDomain)
                .selectedCharityPackageCode(operaCharityPackageCode)
                .build();

        when(paymentDetailsInputMock.bookingFlowInput()).thenReturn(bookingFlowInputMock);
        when(paymentDetailsInputMock.address()).thenReturn(bookerAddressMock);
        when(bookerAddressMock.countryCode()).thenReturn(countryIsoCode);
        when(bookerAddressMock.legacyCountryCode()).thenReturn(countryLegacyCode);
        when(bookerAddressMock.postcode()).thenReturn(postcode);
        when(bookerAddressMock.line1()).thenReturn(addressLine1);
        when(bookerAddressMock.line2()).thenReturn(addressLine2);
        when(bookerAddressMock.city()).thenReturn(addressLine3);

        BookingAddress address = reviewBookingInput.paymentDetailsInput().address();
        PaymentDetailsInput paymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(reviewBookingInput.paymentDetailsInput().bookingFlowInput())
                .address(BookingAddress.create(address.countryCode(), address.legacyCountryCode(), address.line1(),
                        StringUtils.valueOrDefault(address.line2(), StringUtils.EMPTY_STRING),
                        address.city(), address.postcode()))
                .bookerDetails(guestDetailsFormDataInputMock)
                .guestDetailsList(guestDetailsFormDataInputList)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .isBookerStaying(true).build();

        ReviewBookingInput newReviewBookingInput = ReviewBookingInput.builder()
                .paymentDetailsInput(paymentDetailsInput)
                .cardUrl(reviewBookingInput.cardUrl())
                .cardInfo(reviewBookingInput.cardInfo())
                .cardNumber(reviewBookingInput.cardNumber())
                .cvv(reviewBookingInput.cvv())
                .nameOnCard(reviewBookingInput.nameOnCard())
                .expiryDate(reviewBookingInput.expiryDate())
                .cardHolderAddress(reviewBookingInput.cardHolderAddress())
                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW)
                .startDate(reviewBookingInput.startDate())
                .issueNumber(reviewBookingInput.issueNumber())
                .cardType(reviewBookingInput.cardType())
                .marketingOptIn(reviewBookingInput.marketingOptIn())
                .operaDonations(reviewBookingInput.operaDonations())
                .selectedCharityPackageCode(reviewBookingInput.selectedCharityPackageCode())
                .build();

        PaymentDetailsInput expectedPaymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInputMock)
                .address(BookingAddress.create(countryIsoCode, countryIsoCode, addressLine1, addressLine2, addressLine3, postcode))
                .bookerDetails(guestDetailsFormDataInputMock)
                .guestDetailsList(guestDetailsFormDataInputList)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .isBookerStaying(true).build();

        assertEquals(expectedPaymentDetailsInput, newReviewBookingInput.paymentDetailsInput());
        assertEquals(cardUrl, newReviewBookingInput.cardUrl());
        assertEquals(cardInfo, newReviewBookingInput.cardInfo());
        assertEquals(cardNumber, newReviewBookingInput.cardNumber());
        assertEquals(nameOnCard, newReviewBookingInput.nameOnCard());
        assertEquals(expiryDate, newReviewBookingInput.expiryDate());
        assertEquals(bookerAddressMock, newReviewBookingInput.cardHolderAddress());
        assertEquals(startDate, newReviewBookingInput.startDate());
        assertEquals(issueNumber, newReviewBookingInput.issueNumber());
        assertEquals(marketingOptIn, newReviewBookingInput.marketingOptIn().booleanValue());
        assertEquals(parcelableDonationsDomain, newReviewBookingInput.operaDonations());
        assertEquals(operaCharityPackageCode, newReviewBookingInput.selectedCharityPackageCode());
    }
}
