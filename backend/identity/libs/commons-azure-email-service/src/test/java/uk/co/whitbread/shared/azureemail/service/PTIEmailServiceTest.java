package uk.co.whitbread.shared.azureemail.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.SendforgottenPasswordReset;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;
import uk.co.whitbread.shared.azureemail.requestbuilder.ForgottenPasswordResetBuilder;
import uk.co.whitbread.shared.azureemail.requestbuilder.PremierGuestEditedBuilder;
import uk.co.whitbread.shared.azureemail.requestbuilder.PremierGuestInternetBuilder;
import uk.co.whitbread.shared.azureemail.security.PTIEmailHeadersCallback;

@ExtendWith(MockitoExtension.class)
class PTIEmailServiceTest {

  private static final String HOST = "https://azure-host.com";
  private static final String EMAIL = "email@test.com";
  private static final String RESET_PASSWORD_URL = "https://return-url.com";
  private static final SendforgottenPasswordReset sendForgottenPasswordResetRequest = new SendforgottenPasswordReset();
  private static final SendNewPremierGuestEdited sendNewPremierGuestEditedRequest = new SendNewPremierGuestEdited();
  private static final SendNewPremierGuestInternet sendNewPremierGuestInternetRequest = new SendNewPremierGuestInternet();

  @Mock
  private WebServiceTemplate webServiceTemplateMock;
  @Mock
  private PTIEmailProperties ptiEmailProperties;
  @Mock
  private PTIEmailHeadersCallback ptiEmailHeadersCallback;
  @Mock
  private ForgottenPasswordResetBuilder forgottenPasswordResetBuilder;
  @Mock
  private PremierGuestEditedBuilder premierGuestEditedBuilder;
  @Mock
  private PremierGuestInternetBuilder premierGuestInternetBuilder;
  @InjectMocks
  private PTIEmailService ptiEmailService;

  @BeforeEach
  public void setUp() {
    doReturn(HOST).when(ptiEmailProperties).getHost();
    doReturn((WebServiceMessageCallback) webServiceMessage -> {
    }).when(ptiEmailHeadersCallback).addHeaders();
  }

  @Test
  void sendResetPasswordEmail_Success() {
    doReturn(sendForgottenPasswordResetRequest).when(forgottenPasswordResetBuilder)
        .buildSendForgotPasswordRequest(any(PasswordReset.class));
    ptiEmailService.sendResetPasswordEmail(
        PasswordReset.builder().email(EMAIL).resetPasswordUrl(RESET_PASSWORD_URL).build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), eq(
        sendForgottenPasswordResetRequest), any(WebServiceMessageCallback.class));
  }

  @Test
  void sendResetPasswordEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), eq(sendForgottenPasswordResetRequest),
            any(WebServiceMessageCallback.class));
    PasswordReset passwordReset = PasswordReset.builder().email(EMAIL)
        .resetPasswordUrl(RESET_PASSWORD_URL).build();
    assertThrows(RuntimeException.class,
        () -> ptiEmailService.sendResetPasswordEmail(passwordReset));
  }

  @Test
  void sendAccountUpdateEmail_Success() {
    doReturn(sendNewPremierGuestEditedRequest).when(premierGuestEditedBuilder)
        .buildNewPremierGuestEditedRequest(any(AccountUpdate.class));
    ptiEmailService.sendAccountUpdateEmail(AccountUpdate.builder().email(EMAIL).build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST),
        eq(sendNewPremierGuestEditedRequest), any(WebServiceMessageCallback.class));
  }

  @Test
  void sendAccountUpdateEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), eq(sendNewPremierGuestEditedRequest),
            any(WebServiceMessageCallback.class));
    AccountUpdate accountUpdate = AccountUpdate.builder().email(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> ptiEmailService.sendAccountUpdateEmail(accountUpdate));
  }

  @Test
  void sendPiRegisterEmail_Success() {
    doReturn(sendNewPremierGuestInternetRequest).when(premierGuestInternetBuilder)
        .buildPremierGuestInternetRequest(any(PiRegister.class));
    ptiEmailService.sendPiRegisterEmail(PiRegister.builder().emailAddress(EMAIL).build());
    verify(webServiceTemplateMock).marshalSendAndReceive(eq(HOST), eq(
        sendNewPremierGuestInternetRequest), any(WebServiceMessageCallback.class));
  }

  @Test
  void sendPiRegisterEmail_ThrowsException() {
    doThrow(RuntimeException.class).when(webServiceTemplateMock)
        .marshalSendAndReceive(eq(HOST), eq(sendNewPremierGuestInternetRequest),
            any(WebServiceMessageCallback.class));
    final PiRegister piRegister = PiRegister.builder().emailAddress(EMAIL).build();
    assertThrows(RuntimeException.class,
        () -> ptiEmailService.sendPiRegisterEmail(piRegister));
  }
}
