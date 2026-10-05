package uk.co.whitbread.company.service.cdh;

import static uk.co.whitbread.company.utils.EmailValidator.validateEmails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.mapper.CompanyMapper;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.service.EmailService;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhBookingAlertsService {

  private final EmailService emailService;
  private final CompanyMapper companyMapper;
  private final CdhService cdhService;

  public void updateBookingAlerts(String companyId, BookingAlerts bookingAlerts,
      String userEmail) {
    validateEmails(bookingAlerts.getRecipientEmailAddresses());
    GetCompanyResponse getCompanyResponse = cdhService.getCompanyDetails(companyId, userEmail);
    CompanyAccountRequest companyAccountRequest = companyMapper.toCompanyAccountRequest(getCompanyResponse);
    CompanyAccountRequest updatedCompanyRequest = companyMapper.toUpdatedCompanyAccountRequest(companyAccountRequest, bookingAlerts);
    cdhService.updateCompanyDetails(companyId, updatedCompanyRequest, userEmail);
    emailService.sendAsyncOutOfPolicySetupEmail(bookingAlerts, userEmail);
  }
}
