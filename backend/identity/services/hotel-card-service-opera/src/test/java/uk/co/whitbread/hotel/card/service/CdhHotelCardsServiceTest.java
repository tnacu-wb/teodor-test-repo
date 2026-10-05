package uk.co.whitbread.hotel.card.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CardMapper;
import uk.co.whitbread.hotel.card.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.card.utils.CardTokeniser;
import uk.co.whitbread.hotel.card.utils.PaymentCardMasker;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.BusinessPaymentCard;
import uk.co.whitbread.shared.cdh.model.BusinessPaymentPreference;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@ExtendWith(MockitoExtension.class)
public class CdhHotelCardsServiceTest {

  @InjectMocks
  CdhHotelCardsService cdhHotelCardsService;
  @Mock
  private EmployeeDataService employeeDataService;
  @Mock
  private CardMapper mockCardMapper;
  @Mock
  private EmployeeMapper mockEmployeeMapper;
  @Mock
  private CardTokeniser cardTokeniser;
  @Mock
  private PaymentCardMasker mockPaymentCardMasker;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  private GetEmployeeResponse getEmployeeResponse;
  private CdhEmployeeDetails cdhEmployeeDetails;
  private BusinessPaymentCard paymentCard;
  private static final String COMPANY_ID = "24";
  private static final String EMPLOYEE_ID = "7";
  private static final String CARD_NUMBER = "1";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
  private static final PaymentCard PAYMENT_CARD = new PaymentCard();

  @BeforeEach
  public void setUp() {
    paymentCard = BusinessPaymentCard.builder().cardNumber(CARD_NUMBER).build();

    getEmployeeResponse = GetEmployeeResponse.builder().paymentPreference(
        BusinessPaymentPreference.builder().paymentCard(paymentCard).build()).build();

    cdhEmployeeDetails = CdhEmployeeDetails.builder().employeeAccountId(EMPLOYEE_ID)
        .companyAccountId(COMPANY_ID).userEmail(USER_EMAIL_FROM_TOKEN).build();

    var featureFlag = new FeatureFlag();
    featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
  }

  @Test
  public void getPaymentCard_shouldReturnPaymentCard() {
    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(Optional.of(getEmployeeResponse));
    when(mockCardMapper.fromBusinessPaymentCardToPaymentCard(paymentCard))
        .thenReturn(PaymentCard.builder().cardNumber(CARD_NUMBER).build());

    PaymentCard paymentCard = cdhHotelCardsService.getPaymentCard(cdhEmployeeDetails, true);

    assertThat(paymentCard).isNotNull();
    assertThat(paymentCard.getCardNumber()).isEqualTo(CARD_NUMBER);
    verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
  }

  @Test
  public void getPaymentCard_throwUnsupportedOperationException() {
    assertThatThrownBy(() -> cdhHotelCardsService.getPaymentCard(cdhEmployeeDetails, false))
        .isInstanceOf(UnsupportedOperationException.class);

    verifyNoInteractions(mockCardMapper);
    verifyNoInteractions(employeeDataService);
    verifyNoInteractions(cardTokeniser);
  }

  @Test
  public void getPaymentCard_throwEmployeeNotFound() {
    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> cdhHotelCardsService.getPaymentCard(cdhEmployeeDetails, true))
        .isInstanceOf(EmployeeNotFoundException.class);

    verifyNoInteractions(mockCardMapper);
    verifyNoInteractions(cardTokeniser);
    verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
  }

  @Test
  public void addPaymentCard_shouldAddPaymentCard() {
    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(
            Optional.of(getEmployeeResponse));

    cdhHotelCardsService.addOrUpdatePaymentCard(PAYMENT_CARD, cdhEmployeeDetails, true);

    verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
  }

  @Test
   void addPaymentCard_shouldAddPaymentCardAndTokeniseCard() {
    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
            .thenReturn(
                    Optional.of(getEmployeeResponse));
    PAYMENT_CARD.setCardNumber("4444333322221111");
    PAYMENT_CARD.setExpiryDate("10/24");
    PAYMENT_CARD.setBillingAddress(Address.builder()
            .line1("line 1").countryCode("RO").postCode("100200").build());

    doNothing().when(cardTokeniser).tokeniseCard(PAYMENT_CARD, USER_EMAIL_FROM_TOKEN);

    cdhHotelCardsService.addOrUpdatePaymentCard(PAYMENT_CARD, cdhEmployeeDetails, true);

    verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
    verify(mockPaymentCardMasker).maskNumber(any());
    verify(cardTokeniser).tokeniseCard(PAYMENT_CARD , USER_EMAIL_FROM_TOKEN);
  }
  @Test
  public void addPaymentCard_throwEmployeeNotFound() {

    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> cdhHotelCardsService.addOrUpdatePaymentCard(PAYMENT_CARD, cdhEmployeeDetails, true))
        .isInstanceOf(EmployeeNotFoundException.class);

    verifyNoInteractions(mockCardMapper);
    verifyNoInteractions(mockEmployeeMapper);
    verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
    verifyNoMoreInteractions(employeeDataService);
  }

  @Test
  public void addPaymentCard_throwUnsupportedOperationException() {
    assertThatThrownBy(() -> cdhHotelCardsService.addOrUpdatePaymentCard(PAYMENT_CARD, cdhEmployeeDetails, false))
        .isInstanceOf(UnsupportedOperationException.class);

    verifyNoInteractions(mockCardMapper);
    verifyNoInteractions(mockEmployeeMapper);
    verifyNoInteractions(employeeDataService);
  }

  @Test
  public void deletePaymentCard_shouldDeletePaymentCard(){
    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN)).thenReturn(
        Optional.of(getEmployeeResponse));
    when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse)).thenReturn(
        EmployeeAccountRequest.builder().paymentPreference(BusinessPaymentPreference.builder().paymentCard(BusinessPaymentCard.builder()
            .build()).build()).build());

    cdhHotelCardsService.deletePaymentCard(cdhEmployeeDetails);

    verify(employeeDataService).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);

  }

  @Test
  public void deletePaymentCard_throwEmployeeNotFound(){

    when(employeeDataService.getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cdhHotelCardsService.deletePaymentCard(cdhEmployeeDetails))
        .isInstanceOf(EmployeeNotFoundException.class);

  }

  @Test
  void getPaymentCard_flagDisabled_usesLegacyGetEmployeeCall() {
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(Optional.of(getEmployeeResponse));
    when(mockCardMapper.fromBusinessPaymentCardToPaymentCard(paymentCard))
        .thenReturn(PaymentCard.builder().cardNumber(CARD_NUMBER).build());

    cdhHotelCardsService.getPaymentCard(cdhEmployeeDetails, true);

    verify(employeeDataService).getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
    verify(employeeDataService, times(0)).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
  }

  @Test
  void deletePaymentCard_flagDisabled_usesLegacyGetEmployeeCall() {
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN)).thenReturn(
        Optional.of(getEmployeeResponse));
    when(mockEmployeeMapper.toEmployeeAccountRequest(getEmployeeResponse)).thenReturn(
        EmployeeAccountRequest.builder().paymentPreference(BusinessPaymentPreference.builder().paymentCard(BusinessPaymentCard.builder()
            .build()).build()).build());

    cdhHotelCardsService.deletePaymentCard(cdhEmployeeDetails);

    verify(employeeDataService).getEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
    verify(employeeDataService, times(0)).getEmployeeV2(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN);
  }
}
