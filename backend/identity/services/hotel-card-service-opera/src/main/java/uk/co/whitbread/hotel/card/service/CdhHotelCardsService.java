package uk.co.whitbread.hotel.card.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.hotel.card.mapper.CardMapper;
import uk.co.whitbread.hotel.card.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.utils.CardTokeniser;
import uk.co.whitbread.hotel.card.utils.PaymentCardMasker;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhHotelCardsService {

  public static final String EMPLOYEE_NOT_FOUND = "Employee %s from company %s was not found";
  private static final String NOT_IMPLEMENTED_FOR_NON_BUSINESS = "Not implemented yet. Only implemented for business = true";
  private final EmployeeDataService employeeDataService;
  private final CardMapper cardMapper;
  private final EmployeeMapper employeeMapper;
  private final PaymentCardMasker paymentCardMasker;
  private final CardTokeniser cardTokeniser;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public PaymentCard getPaymentCard(CdhEmployeeDetails cdhEmployeeDetails, boolean business) {
    checkBusiness(business);
    Optional<GetEmployeeResponse> optionalGetCustomerResponse = getEmployee(
        cdhEmployeeDetails.getCompanyAccountId(), cdhEmployeeDetails.getEmployeeAccountId(),
        cdhEmployeeDetails.getUserEmail());

    GetEmployeeResponse getEmployeeResponse = optionalGetCustomerResponse.orElseThrow(
        () -> new EmployeeNotFoundException(String
            .format(EMPLOYEE_NOT_FOUND,
                cdhEmployeeDetails.getEmployeeAccountId(),
                cdhEmployeeDetails.getCompanyAccountId())));

    return cardMapper.fromBusinessPaymentCardToPaymentCard(
        getEmployeeResponse.getPaymentPreference().getPaymentCard());
  }

  public void addOrUpdatePaymentCard(PaymentCard paymentCard, CdhEmployeeDetails cdhEmployeeDetails,
      boolean business) {
    checkBusiness(business);
    Optional<GetEmployeeResponse> optionalGetCustomerResponse = getEmployee(
        cdhEmployeeDetails.getCompanyAccountId(), cdhEmployeeDetails.getEmployeeAccountId(),
        cdhEmployeeDetails.getUserEmail());

    GetEmployeeResponse getEmployeeResponse = optionalGetCustomerResponse.orElseThrow(
        () -> new EmployeeNotFoundException(String
            .format(EMPLOYEE_NOT_FOUND,
                cdhEmployeeDetails.getEmployeeAccountId(),
                cdhEmployeeDetails.getCompanyAccountId())));

    String maskedCardNumber = paymentCard.getCardNumber() != null ?
        paymentCardMasker.maskNumber(paymentCard.getCardNumber()) : null;

    if (paymentCard.getCardNumber() != null) {
      cardTokeniser.tokeniseCard(paymentCard, cdhEmployeeDetails.getUserEmail());
    }

    EmployeeAccountRequest employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(
        cardMapper.addCustomerPaymentCard(getEmployeeResponse, paymentCard, maskedCardNumber));

    employeeDataService.updateEmployeeAccount(cdhEmployeeDetails.getCompanyAccountId(),
        cdhEmployeeDetails.getEmployeeAccountId(), employeeAccountRequest,
        cdhEmployeeDetails.getUserEmail());
  }

  public void deletePaymentCard(CdhEmployeeDetails cdhEmployeeDetails) {
    Optional<GetEmployeeResponse> optionalGetCustomerResponse = getEmployee(
        cdhEmployeeDetails.getCompanyAccountId(), cdhEmployeeDetails.getEmployeeAccountId(),
        cdhEmployeeDetails.getUserEmail());

    GetEmployeeResponse getEmployeeResponse = optionalGetCustomerResponse.orElseThrow(
        () -> new EmployeeNotFoundException(String
            .format(EMPLOYEE_NOT_FOUND,
                cdhEmployeeDetails.getEmployeeAccountId(),
                cdhEmployeeDetails.getCompanyAccountId())));

    EmployeeAccountRequest employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(
        getEmployeeResponse);
    if (employeeAccountRequest.getPaymentPreference().getPaymentCard() != null) {
      employeeAccountRequest.getPaymentPreference().setPaymentCard(null);
    }

    employeeDataService.updateEmployeeAccount(cdhEmployeeDetails.getCompanyAccountId(),
        cdhEmployeeDetails.getEmployeeAccountId(), employeeAccountRequest,
        cdhEmployeeDetails.getUserEmail());
  }

  private void checkBusiness(boolean business) {
    if (!business) {
      throw new UnsupportedOperationException(NOT_IMPLEMENTED_FOR_NON_BUSINESS);
    }
  }

  private Optional<GetEmployeeResponse> getEmployee(String companyAccountId,
      String employeeAccountId, String accessedBy) {
    if (isCdhApiDeprecationEnabled()) {
      return employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, accessedBy);
    }
    return employeeDataService.getEmployee(companyAccountId, employeeAccountId, accessedBy);
  }

  private boolean isCdhApiDeprecationEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
  }
}
