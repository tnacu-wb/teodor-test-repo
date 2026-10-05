package com.whitbread.premierinn.reviewbooking;

import com.whitbread.premierinn.api.response.CardInfo;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.domain.common.PriceDomain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardSummaryModelTest {

    @Mock ReviewBookingInput reviewBookingInputMock;
    @Mock private CardInfo cardInfoMock;
    @Mock private CardSummaryModel cardSummaryModel;

    private final String cardUrl = "www.content.com/MC.jpeg";
    private final String lastFourDigitsOfCardNumber = "3048";
    private final String cardNumber = "46957305" + lastFourDigitsOfCardNumber;
    private final String nameOnCard = "Danny Boyle";
    private final String expiryDateMonth = "03";
    private final String expiryDateYear = "18";
    private final String cardLegend = "Diners Card";
    private final PriceDomain creditCardFee = new PriceDomain(2.00f, "GBP");
    private CardSummaryModel model;

    @Before
    public void onSetup() {
        when(reviewBookingInputMock.cardUrl()).thenReturn(cardUrl);
        when(reviewBookingInputMock.cardNumber()).thenReturn(cardNumber);
        when(reviewBookingInputMock.nameOnCard()).thenReturn(nameOnCard);
        when(reviewBookingInputMock.expiryDate()).thenReturn(expiryDateMonth + expiryDateYear);
        when(reviewBookingInputMock.cardInfo()).thenReturn(cardInfoMock);

        when(cardInfoMock.cardLegend()).thenReturn(cardLegend);
        when(cardInfoMock.cardFeeAmount()).thenReturn(CommonMappersKt.toBookingPrice(creditCardFee));

        model = new AutoValue_CardSummaryModel(cardLegend, creditCardFee, cardUrl, lastFourDigitsOfCardNumber,
                nameOnCard, "12/17");
    }

    @Test
    public void testCreate() {
        CardSummaryModel cardSummaryModel = CardSummaryModel.create(reviewBookingInputMock);

        assertEquals(creditCardFee, cardSummaryModel.fee());
        assertEquals(nameOnCard, cardSummaryModel.bookerName());
        assertEquals(cardLegend, cardSummaryModel.cardLegend());
        assertEquals(cardUrl, cardSummaryModel.cardUrl());
        assertEquals(lastFourDigitsOfCardNumber, cardSummaryModel.lastFourDigits());
        assertEquals(expiryDateMonth + "/" + expiryDateYear, cardSummaryModel.expiryDate());
    }

    @Test
    public void cardIsValid() {
        //Saturday, 4 November 2017 17:18:09
        Date currentDate = new Date(1509815889000L);

        assertThat(model.hasCardExpired(currentDate.getTime()), is(false));
    }

    @Test
    public void cardIsValid_case_1() {
        //Friday, 1 December 2017 17:18:09
        Date currentDate = new Date(1512148689000L);

        assertThat(model.hasCardExpired(currentDate.getTime()), is(false));
    }

    @Test
    public void cardIsValid_case_2() {
        // TODO This test fails when run in a different timezone
        //Sunday, 31 December 2017 23:59:59
        Date currentDate = new Date(1514764799000L);

        assertThat(model.hasCardExpired(currentDate.getTime()), is(false));
    }

    @Test
    public void cardHasExpired() {
        //Monday, 1 January 2018 00:00:00
        Date currentDate = new Date(1514764800000L);

        assertThat(model.hasCardExpired(currentDate.getTime()), is(true));
    }

    @Test
    public void cardHasExpired_case_2() {
        //Monday, 1 January 2018 16:33:03
        Date currentDate = new Date(1514824383000L);

        assertThat(model.hasCardExpired(currentDate.getTime()), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testCardWithInvalidExpirationDateThrowsIllegalStateException() {

        CardSummaryModel model = new AutoValue_CardSummaryModel(cardLegend, creditCardFee, cardUrl, lastFourDigitsOfCardNumber,
                nameOnCard, "12-17");

        model.hasCardExpired(1514824383000L);
    }
}