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
import uk.co.whitbread.hotel.card.model.PaymentCardBBPersonal;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.card.utils.FakeDataGenerator;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.hotel.card.service.cards.BBPersonalCardServiceTestData.testMapPaymentCard_expectedPaymentCard;
import static uk.co.whitbread.hotel.card.service.cards.BBPersonalCardServiceTestData.testUpdatePaymentCardSuccess_expectedUpdateRequest;

@ExtendWith(MockitoExtension.class)
class BBPersonalCardServiceTest {

    @InjectMocks
    private BBPersonalCardService bbPersonalCardService;
    @Spy
    private CDHMapper cdhMapper = new CDHMapperImpl();
    @Mock
    private EmployeeDataService employeeDataService;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;
    @Spy
    private PaymentCardMapper paymentCardMapper = new PaymentCardMapperImpl();
    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        var featureFlag = new FeatureFlag();
        featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
        lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    }

    @Test
    void testUpdatePaymentCardSuccess() {
        //given
        var employeeAccountRequestArgumentCaptor = ArgumentCaptor.forClass(EmployeeAccountRequest.class);
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardBBPersonal.class);
        var employeeAccountId = paymentCard.getEmployeeAccountId();
        var companyAccountId = paymentCard.getCompanyAccountId();
        var userEmail = paymentCard.getUserEmail();
        var expectedUpdateRequest = testUpdatePaymentCardSuccess_expectedUpdateRequest(paymentCard,
            employeeAccountId, companyAccountId);

        when(employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, userEmail))
                .thenReturn(Optional.of(new GetEmployeeResponse()));
        when(employeeDataService.updateEmployeeAccount(eq(companyAccountId), eq(employeeAccountId), employeeAccountRequestArgumentCaptor.capture(), eq(userEmail)))
                .thenReturn(Mockito.mock(EmployeeAccountResponse.class));

        //when
        bbPersonalCardService.updatePaymentCard(paymentCard);

        //then
        verifyNoMoreInteractions(employeeDataService);
        Assertions.assertThat(employeeAccountRequestArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectedUpdateRequest);
    }

    @Test
    void testUpdatePaymentCard_throwAccountNotFoundException() {
        //given
        var paymentCard = FakeDataGenerator.createFakeData(PaymentCardBBPersonal.class);
        var employeeAccountId = paymentCard.getEmployeeAccountId();
        var companyAccountId = paymentCard.getCompanyAccountId();
        var userEmail = paymentCard.getUserEmail();

        var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
        when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
        when(employeeDataService.getEmployee(companyAccountId, employeeAccountId, userEmail))
                .thenReturn(Optional.empty());

        //then
        assertThrows(AccountNotFoundException.class, () -> {
            bbPersonalCardService.updatePaymentCard(paymentCard);
        });

        verify(employeeDataService, never()).updateEmployeeAccount(any(), any(), any(), any());
    }

    @Test
    void testStrategyName() {
        SaveCardPurpose actual = bbPersonalCardService.strategyName();
        assertEquals(SaveCardPurpose.BB_PERSONAL, actual);
    }

    @ParameterizedTest
    @CsvSource({"false", "true"})
    void testMapPaymentCard(boolean cnpRequired) {
        var paymentCardDTO = FakeDataGenerator.createFakeData(PaymentCardDTO.class);
        paymentCardDTO.setCnpRequired(cnpRequired);
        var expectedPaymentCard = testMapPaymentCard_expectedPaymentCard(paymentCardDTO, cnpRequired);
        paymentCardDTO.setCardHolderName(paymentCardDTO.getCardHolderName() + " ");

        var paymentCard = (PaymentCardBBPersonal) bbPersonalCardService.mapPaymentCard(paymentCardDTO);

        verify(paymentCardMapper, times(1)).toPaymentCardBBPersonal(paymentCardDTO);
        Assertions.assertThat(paymentCard)
                .usingRecursiveComparison()
                .ignoringFields("cardNumber")
                .isEqualTo(expectedPaymentCard);
    }
}
