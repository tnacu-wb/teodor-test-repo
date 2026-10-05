package uk.co.whitbread.hotel.card.service.cards;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.exceptions.CardNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CDHMapper;
import uk.co.whitbread.hotel.card.mapper.CDHMapperImpl;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapper;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapperImpl;
import uk.co.whitbread.hotel.card.model.CardType;
import uk.co.whitbread.hotel.card.model.PaymentCardBBCentral;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.utils.FakeDataGenerator;
import uk.co.whitbread.shared.cdh.PaymentCardDataService;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;
import uk.co.whitbread.shared.cdh.model.card.PaymentCardDetails;
import uk.co.whitbread.shared.cdh.model.card.UpdatePaymentCardDetailsResponse;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.hotel.card.service.cards.BBCentralCardServiceTestData.*;

@ExtendWith(MockitoExtension.class)
class BBCentralCardServiceTest {

    @InjectMocks
    private BBCentralCardService bbCentralCardService;
    @Spy
    private CDHMapper cdhMapper = new CDHMapperImpl();
    @Mock
    private PaymentCardDataService paymentCardDataService;
    @Spy
    private PaymentCardMapper paymentCardMapper = new PaymentCardMapperImpl();
    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testAddNewPaymentCardSuccess() {
        //given
        var paymentCardDetailsArgumentCaptor = ArgumentCaptor.forClass(PaymentCardDetails.class);
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardBBCentral.class);
        paymentCard.setCardId(null);
        var expectedPaymentCardDetails = testAddNewPaymentCardSuccess_expectedPaymentCardDetails(paymentCard);

        when(paymentCardDataService.createCompanyPaymentCard(eq(paymentCard.getCompanyId()), paymentCardDetailsArgumentCaptor.capture(), eq(paymentCard.getUserEmail())))
                .thenReturn(Mockito.mock(GetPaymentCardDetailsResponse.class));

        //when
        bbCentralCardService.updatePaymentCard(paymentCard);

        //then
        verifyNoMoreInteractions(paymentCardDataService);
        Assertions.assertThat(paymentCardDetailsArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentCardDetails);
    }

    @Test
    void testEditNewPaymentNoneCardSuccess() {
        //given
        var paymentCardDetailsArgumentCaptor = ArgumentCaptor.forClass(PaymentCardDetails.class);
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardBBCentral.class);
        paymentCard.setCardType(CardType.NONE.name());
        var paymentCardDetailsResponse = FakeDataGenerator.createFakeData(GetPaymentCardDetailsResponse.class);
        paymentCard.setCardId(paymentCardDetailsResponse.getCardId());
        var expectedPaymentCardDetails = testEditNewPaymentNoneCardSuccess_expectedPaymentCardDetails(paymentCard, paymentCardDetailsResponse);

        when(paymentCardDataService.getPaymentCards(paymentCard.getCompanyId(), paymentCard.getUserEmail()))
                .thenReturn(List.of(paymentCardDetailsResponse));
        when(paymentCardDataService.updatePaymentCard(eq(paymentCard.getCompanyId()), eq(paymentCard.getCardId()), paymentCardDetailsArgumentCaptor.capture(), eq(paymentCard.getUserEmail())))
                .thenReturn(Mockito.mock(UpdatePaymentCardDetailsResponse.class));

        //when
        bbCentralCardService.updatePaymentCard(paymentCard);

        //then
        verifyNoMoreInteractions(paymentCardDataService);
        Assertions.assertThat(paymentCardDetailsArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentCardDetails);
    }

    @Test
    void testEditNewPaymentCardSuccess() {
        //given
        var paymentCardDetailsArgumentCaptor = ArgumentCaptor.forClass(PaymentCardDetails.class);
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardBBCentral.class);
        var expectedPaymentCardDetails = testAddNewPaymentCardSuccess_expectedPaymentCardDetails(paymentCard);

        when(paymentCardDataService.updatePaymentCard(eq(paymentCard.getCompanyId()), eq(paymentCard.getCardId()), paymentCardDetailsArgumentCaptor.capture(), eq(paymentCard.getUserEmail())))
                .thenReturn(Mockito.mock(UpdatePaymentCardDetailsResponse.class));

        //when
        bbCentralCardService.updatePaymentCard(paymentCard);

        //then
        verifyNoMoreInteractions(paymentCardDataService);
        Assertions.assertThat(paymentCardDetailsArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedPaymentCardDetails);
    }

    @Test
    void testEditNewPaymentCardNotFound() {
        //given
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardBBCentral.class);
        paymentCard.setCardType(CardType.NONE.name());

        when(paymentCardDataService.getPaymentCards(paymentCard.getCompanyId(), paymentCard.getUserEmail()))
                .thenReturn(Collections.emptyList());

        //when
        assertThrows(CardNotFoundException.class, () -> bbCentralCardService.updatePaymentCard(paymentCard));
    }

    @Test
    void testStrategyName() {
        SaveCardPurpose actual = bbCentralCardService.strategyName();
        assertEquals(SaveCardPurpose.BB_CENTRAL, actual);
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    void testMapPaymentCard(boolean cnpRequired) {
        var paymentCardDTO = FakeDataGenerator.createFakeData(PaymentCardDTO.class);
        paymentCardDTO.setCnpRequired(cnpRequired);
        var expectedPaymentCard = testMapPaymentCard_expectedPaymentCard(paymentCardDTO, cnpRequired);
        paymentCardDTO.setCardHolderName(paymentCardDTO.getCardHolderName() + " ");

        var paymentCard = (PaymentCardBBCentral) bbCentralCardService.mapPaymentCard(paymentCardDTO);

        verify(paymentCardMapper, times(1)).toPaymentCardBBCentral(paymentCardDTO);
        Assertions.assertThat(paymentCard)
                .usingRecursiveComparison()
                .ignoringFields("cardNumber")
                .isEqualTo(expectedPaymentCard);
    }
}
