package uk.co.whitbread.hotel.card.service.cards;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CDHMapper;
import uk.co.whitbread.hotel.card.mapper.CDHMapperImpl;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapper;
import uk.co.whitbread.hotel.card.mapper.PaymentCardMapperImpl;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.card.utils.FakeDataGenerator;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.hotel.card.service.cards.PIPersonalCardServiceTestData.testMapPaymentCard_expectedPaymentCard;
import static uk.co.whitbread.hotel.card.service.cards.PIPersonalCardServiceTestData.testUpdatePaymentCardSuccess_expectedUpdateRequest;

@ExtendWith(MockitoExtension.class)
class PIPersonalCardServiceTest {

    @InjectMocks
    private PIPersonalCardService piPersonalCardService;
    @Spy
    private CDHMapper cdhMapper = new CDHMapperImpl();
    @Mock
    private CustomerDataService customerDataService;
    @Spy
    private PaymentCardMapper paymentCardMapper = new PaymentCardMapperImpl();
    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    @BeforeEach
    void setUp() {
        var featureFlag = new FeatureFlag();
        featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
        lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void testUpdatePaymentCardSuccess(boolean isNewAccount) {
        //given
        var customerAccountRequestArgumentCaptor = ArgumentCaptor.forClass(CustomerAccountRequest.class);
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardPIPersonal.class);
        var customerAccountId = paymentCard.getCustomerAccountId();
        var userEmail = paymentCard.getUserEmail();
        var response = FakeDataGenerator.createFakeData(GetCustomerAccountResponse.class);
        if (isNewAccount) {
            response.setPaymentPreference(null);
        }
        var expectedUpdateRequest = testUpdatePaymentCardSuccess_expectedUpdateRequest(paymentCard, response);

        when(customerDataService.getCustomerAccountV3(customerAccountId, userEmail))
                .thenReturn(Optional.of(response));
        when(customerDataService.updateCustomerAccount(eq(customerAccountId), customerAccountRequestArgumentCaptor.capture(), eq(userEmail)))
                .thenReturn(Mockito.mock(CustomerAccountResponse.class));

        //when
        piPersonalCardService.updatePaymentCard(paymentCard);

        //then
        verifyNoMoreInteractions(customerDataService);
        Assertions.assertThat(customerAccountRequestArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedUpdateRequest);
    }

    @Test
    void testUpdatePaymentCard_throwAccountNotFoundException() {
        //given
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardPIPersonal.class);
        var customerAccountId = paymentCard.getCustomerAccountId();
        var userEmail = paymentCard.getUserEmail();

        when(customerDataService.getCustomerAccountV3(customerAccountId, userEmail))
                .thenReturn(Optional.empty());

        //then
        assertThrows(AccountNotFoundException.class, () -> {
            piPersonalCardService.updatePaymentCard(paymentCard);
        });

        verify(customerDataService, never()).updateCustomerAccount(any(), any(), any());
    }

    @Test
    void testStrategyName() {
        SaveCardPurpose actual = piPersonalCardService.strategyName();
        assertEquals(SaveCardPurpose.PI_PERSONAL, actual);
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    void testMapPaymentCard(boolean cnpRequired) {
        var paymentCardDTO = FakeDataGenerator.createFakeData(PaymentCardDTO.class);
        paymentCardDTO.setCnpRequired(cnpRequired);
        var expectedPaymentCard = testMapPaymentCard_expectedPaymentCard(paymentCardDTO, cnpRequired);
        paymentCardDTO.setCardHolderName(paymentCardDTO.getCardHolderName() + " ");

        var paymentCard = (PaymentCardPIPersonal) piPersonalCardService.mapPaymentCard(paymentCardDTO);

        verify(paymentCardMapper, times(1)).toPaymentCardPIPersonal(paymentCardDTO);
        Assertions.assertThat(paymentCard)
                .usingRecursiveComparison()
                .ignoringFields("cardNumber")
                .isEqualTo(expectedPaymentCard);
    }

    @Test
    void testUpdatePaymentCard_flagDisabled_usesLegacyGetCustomerAccountCall() {
        //given
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardPIPersonal.class);
        var customerAccountId = paymentCard.getCustomerAccountId();
        var userEmail = paymentCard.getUserEmail();
        var response = FakeDataGenerator.createFakeData(GetCustomerAccountResponse.class);

        var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
        when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
        when(customerDataService.getCustomerAccount(customerAccountId, userEmail))
                .thenReturn(Optional.of(response));
        when(customerDataService.updateCustomerAccount(eq(customerAccountId), any(), eq(userEmail)))
                .thenReturn(Mockito.mock(CustomerAccountResponse.class));

        //when
        piPersonalCardService.updatePaymentCard(paymentCard);

        //then
        verify(customerDataService).getCustomerAccount(customerAccountId, userEmail);
        verify(customerDataService, never()).getCustomerAccountV3(customerAccountId, userEmail);
    }
}
