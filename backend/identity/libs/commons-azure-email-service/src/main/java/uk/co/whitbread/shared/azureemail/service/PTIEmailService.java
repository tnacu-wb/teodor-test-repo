package uk.co.whitbread.shared.azureemail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.SendforgottenPasswordReset;
import uk.co.whitbread.shared.azureemail.exception.AzureEmailServiceException;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;
import uk.co.whitbread.shared.azureemail.requestbuilder.ForgottenPasswordResetBuilder;
import uk.co.whitbread.shared.azureemail.requestbuilder.PremierGuestEditedBuilder;
import uk.co.whitbread.shared.azureemail.requestbuilder.PremierGuestInternetBuilder;
import uk.co.whitbread.shared.azureemail.security.PTIEmailHeadersCallback;

@Service
@Slf4j
@RequiredArgsConstructor
public class PTIEmailService {

  private final WebServiceTemplate webServiceTemplate;
  private final PTIEmailProperties ptiEmailProperties;
  private final PTIEmailHeadersCallback ptiEmailHeadersCallback;
  private final PremierGuestEditedBuilder premierGuestEditedBuilder;
  private final ForgottenPasswordResetBuilder forgottenPasswordResetBuilder;
  private final PremierGuestInternetBuilder premierGuestInternetBuilder;

  public void sendResetPasswordEmail(PasswordReset passwordReset) {
    final SendforgottenPasswordReset request =
        forgottenPasswordResetBuilder.buildSendForgotPasswordRequest(passwordReset);
    marshalSendAndReceive(request);
  }

  public void sendAccountUpdateEmail(AccountUpdate accountUpdate) {
    final SendNewPremierGuestEdited request =
        premierGuestEditedBuilder.buildNewPremierGuestEditedRequest(accountUpdate);
    marshalSendAndReceive(request);
  }

  public void sendPiRegisterEmail(PiRegister piRegister) {
    final SendNewPremierGuestInternet request =
        premierGuestInternetBuilder.buildPremierGuestInternetRequest(piRegister);
    marshalSendAndReceive(request);
  }

  private void marshalSendAndReceive(Object request) {
    final String host = ptiEmailProperties.getHost();
    try {
      webServiceTemplate.marshalSendAndReceive(host, request,
          ptiEmailHeadersCallback.addHeaders());
    } catch (RuntimeException e) {
      log.error("Error while making email request {} to {}", request, host);
      throw new AzureEmailServiceException(e);
    }
  }
}
