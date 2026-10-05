package uk.co.whitbread.company.service.cdh;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.exceptions.InvalidEmailException;
import uk.co.whitbread.company.mapper.CompanyMapper;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.service.EmailService;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@ExtendWith(MockitoExtension.class)
class CdhBookingAlertsServiceTest {

  private static final String COMPANY_ID = "125";
  private static final String EMAIL = "user@mail.com";

  @Mock
  private EmailService emailService;
  @Mock
  private CompanyMapper companyMapper;
  @Mock
  private CdhService cdhService;

  @InjectMocks
  private CdhBookingAlertsService cdhBookingAlertsService;

  @Test
  void updateBookingAlerts_success() {
    var bookingAlerts = new BookingAlerts();
    var cdhBookingAlerts = uk.co.whitbread.shared.cdh.model.company.BookingAlerts.builder()
        .recipientEmailAddresses(List.of(EMAIL)).build();
    bookingAlerts.setRecipientEmailAddresses(List.of(EMAIL));

    var getCompanyResponse = GetCompanyResponse.builder().build();
    var companyAccountRequest = CompanyAccountRequest.builder()
        .bookingAlerts(cdhBookingAlerts)
        .build();

    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(getCompanyResponse);
    when(companyMapper.toCompanyAccountRequest(getCompanyResponse)).thenReturn(companyAccountRequest);
    when(companyMapper.toUpdatedCompanyAccountRequest(companyAccountRequest, bookingAlerts)).thenReturn(companyAccountRequest);

    cdhBookingAlertsService.updateBookingAlerts(COMPANY_ID, bookingAlerts, EMAIL);

    verify(cdhService).updateCompanyDetails(COMPANY_ID, companyAccountRequest, EMAIL);
    verify(emailService).sendAsyncOutOfPolicySetupEmail(bookingAlerts, EMAIL);
  }

  @Test
  void updateBookingAlerts_throwsInvalidEmailException() {
    var bookingAlerts = new BookingAlerts();
    bookingAlerts.setRecipientEmailAddresses(List.of("invalidEmail@"));
    assertThatThrownBy(
        () -> cdhBookingAlertsService.updateBookingAlerts(COMPANY_ID, bookingAlerts, EMAIL))
        .isInstanceOf(InvalidEmailException.class);

    verify(emailService, times(0)).sendAsyncOutOfPolicySetupEmail(bookingAlerts, EMAIL);
  }
}
