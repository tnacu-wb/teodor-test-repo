package uk.co.whitbread.hotel.card.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.mapper.CardMapper;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.utils.CardTokeniser;
import uk.co.whitbread.hotel.card.utils.PaymentCardMasker;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.PaymentCardDataService;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;

@ExtendWith(MockitoExtension.class)
class CdhCompanyCardsServiceTest {

    @InjectMocks
    private CdhCompanyCardsService sut;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private PaymentCardMasker mockPaymentCardMasker;
    @Mock
    private PaymentCardDataService mockPaymentCardDataService;
    @Mock
    private CardTokeniser cardTokeniser;
    @Mock
    private PaymentCard paymentCard;

    private static final String COMPANY_ID = "24";
    private static final String CARD_ID = "7";
    private static final String CARD_TOKEN = "343434200000005";
    private static final String EMPLOYEE_ID = "7";
    private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
    private static final PaymentCard PAYMENT_CARD = new PaymentCard();

    @Test
    void updatePaymentCard_shouldMakeRequest() {

        // Given

        // When
        sut.updatePaymentCard(COMPANY_ID, CARD_ID, PAYMENT_CARD, USER_EMAIL_FROM_TOKEN);

        // Then
        verify(mockPaymentCardDataService).updatePaymentCard(any(), any(), any(), any());
        verify(cardMapper).toPaymentCardDetails(any(), any());
        verify(mockPaymentCardMasker).maskNumber(any());
    }
    @Test
    void updatePaymentCard_shouldTokeniseCard() {
        // Given
        when(paymentCard.getCardNumber()).thenReturn("4444333322221111");
        doNothing().when(cardTokeniser).tokeniseCard(paymentCard, USER_EMAIL_FROM_TOKEN);

        // When
        sut.updatePaymentCard(COMPANY_ID, CARD_ID, paymentCard, USER_EMAIL_FROM_TOKEN);

        // Then
        verify(cardTokeniser).tokeniseCard(paymentCard, USER_EMAIL_FROM_TOKEN);
    }

    @Test
    void deleteCdhPaymentCard_shouldDeleteCard() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID).userEmail(USER_EMAIL_FROM_TOKEN).build();
        sut.deletePaymentCard(COMPANY_ID, CARD_ID, cdhEmployeeDetails.getUserEmail());
        verify(mockPaymentCardDataService).deletePaymentCard(COMPANY_ID, CARD_ID,
            USER_EMAIL_FROM_TOKEN);
    }
    
    @Test
    void addPaymentCard_shouldAddPaymentCard() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID).userEmail(USER_EMAIL_FROM_TOKEN).build();

        when(mockPaymentCardDataService.createCompanyPaymentCard(eq(COMPANY_ID), any(),
            eq(cdhEmployeeDetails.getUserEmail()))).thenReturn(
            GetPaymentCardDetailsResponse.builder().cardId(CARD_ID).token(CARD_TOKEN).build());

        sut.addCdhPaymentCard(COMPANY_ID, PAYMENT_CARD, cdhEmployeeDetails.getUserEmail());

        verify(mockPaymentCardDataService).createCompanyPaymentCard(any(), any(), any());
    }

    @Test
     void addPaymentCard_shouldAddPaymentCardAndTokeniseCard() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
                .companyAccountId(COMPANY_ID)
                .employeeAccountId(EMPLOYEE_ID).userEmail(USER_EMAIL_FROM_TOKEN).build();

        when(mockPaymentCardDataService.createCompanyPaymentCard(eq(COMPANY_ID), any(),
                eq(cdhEmployeeDetails.getUserEmail()))).thenReturn(
                GetPaymentCardDetailsResponse.builder().cardId(CARD_ID).token(CARD_TOKEN).build());
        when(paymentCard.getCardNumber()).thenReturn("4444333322221111");
        doNothing().when(cardTokeniser).tokeniseCard(paymentCard, USER_EMAIL_FROM_TOKEN);

        sut.addCdhPaymentCard(COMPANY_ID, paymentCard, cdhEmployeeDetails.getUserEmail());

        verify(mockPaymentCardDataService).createCompanyPaymentCard(any(), any(), any());
        verify(cardTokeniser).tokeniseCard(paymentCard , USER_EMAIL_FROM_TOKEN);
    }


    @Test
    void getPaymentCards_shouldGetCompanyPaymentCards() {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID).userEmail(USER_EMAIL_FROM_TOKEN).build();

        List<GetPaymentCardDetailsResponse> paymentCardDetailsResponses = List.of(
            GetPaymentCardDetailsResponse.builder().build());

        when(mockPaymentCardDataService.getPaymentCards(COMPANY_ID, cdhEmployeeDetails.getUserEmail())).thenReturn(
            paymentCardDetailsResponses);

        sut.getPaymentCards(COMPANY_ID, cdhEmployeeDetails.getUserEmail());

        verify(cardMapper).toPaymentCard(any(GetPaymentCardDetailsResponse.class));
        verify(mockPaymentCardDataService).getPaymentCards(any(), any());
    }
}
