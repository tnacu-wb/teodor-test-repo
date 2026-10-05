package uk.co.whitbread.company.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.service.cdh.CdhService;
import uk.co.whitbread.shared.azureemail.model.OutOfPolicySetup;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final AzureEmailService azureEmailService;
  private final CdhService cdhService;

  @Async
  public void sendAsyncOutOfPolicySetupEmail(BookingAlerts bookingAlerts, String userEmail) {
    var employee = cdhService.getEmployeeByEmail(userEmail);
    for (var email : bookingAlerts.getRecipientEmailAddresses()) {
      var outOfPolicySetup = OutOfPolicySetup.builder()
          .email(email)
          .firstName(employee.getFirstName())
          .lastName(employee.getLastName())
          .build();
      try {
        azureEmailService.sendOutOfPolicySetupEmail(outOfPolicySetup);
      } catch (Exception e) {
        log.error("Out of policy setup email could not be sent for employee", e);
      }
    }
  }
}
