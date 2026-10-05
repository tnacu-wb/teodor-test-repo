package uk.co.whitbread.company.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.service.cdh.CdhService;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  private static final String EMAIL = "user@mail.com";

  @Mock
  private AzureEmailService azureEmailService;
  @Mock
  private CdhService cdhService;

  @InjectMocks
  private EmailService emailService;

  @Test
  void sendAsyncOutOfPolicySetupEmail_success() {
    var employee = GetEmployeeResponse.builder().emailAddress(EMAIL).build();
    var bookingAlerts = new BookingAlerts();
    bookingAlerts.setRecipientEmailAddresses(List.of(EMAIL));
    when(cdhService.getEmployeeByEmail(EMAIL)).thenReturn(employee);

    emailService.sendAsyncOutOfPolicySetupEmail(bookingAlerts, EMAIL);

    verify(azureEmailService).sendOutOfPolicySetupEmail(any(OutOfPolicySetup.class));
  }
}
