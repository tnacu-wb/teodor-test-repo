package uk.co.whitbread.shared.azureemail.requestbuilder;

import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.PI_FORGOT_PASSWORD;

import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.azureemail.api.Content;
import uk.co.whitbread.shared.azureemail.api.ForgottenPasswordReset;
import uk.co.whitbread.shared.azureemail.api.SendforgottenPasswordReset;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

@Component
public class ForgottenPasswordResetBuilder extends PTIEmailRequestBuilder {

  private final PasswordResetTransformer passwordResetTransformer;

  public ForgottenPasswordResetBuilder(
      PTIEmailProperties ptiEmailProperties,
      PasswordResetTransformer passwordResetTransformer) {

    super(ptiEmailProperties);
    this.passwordResetTransformer = passwordResetTransformer;
  }

  public SendforgottenPasswordReset buildSendForgotPasswordRequest(PasswordReset passwordReset) {
    final SendforgottenPasswordReset request = new SendforgottenPasswordReset();
    request.setIn0(buildForgottenPasswordReset(passwordReset));
    return request;
  }

  private ForgottenPasswordReset buildForgottenPasswordReset(PasswordReset passwordReset) {
    final ForgottenPasswordReset forgottenPasswordReset = new ForgottenPasswordReset();
    forgottenPasswordReset.setContent(buildForgottenPasswordResetContent(passwordReset));
    forgottenPasswordReset.setLogin(buildLogin());
    forgottenPasswordReset.setTemplate(buildTemplate(PI_FORGOT_PASSWORD.getTemplateId()));
    return forgottenPasswordReset;
  }

  private Content buildForgottenPasswordResetContent(PasswordReset passwordReset) {
    return passwordResetTransformer.toForgotPasswordResetContent(passwordReset);
  }
}
