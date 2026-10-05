package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestBuilderData.ptiEmailProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.azureemail.api.Content;
import uk.co.whitbread.shared.azureemail.api.ForgottenPasswordReset;
import uk.co.whitbread.shared.azureemail.api.Login;
import uk.co.whitbread.shared.azureemail.api.SendforgottenPasswordReset;
import uk.co.whitbread.shared.azureemail.api.Template;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;

@ExtendWith(MockitoExtension.class)
class ForgottenPasswordResetBuilderTest {

  private ForgottenPasswordResetBuilder forgottenPasswordResetBuilder;
  private Content passwordResetContent;
  @Mock
  private PasswordResetTransformer passwordResetTransformer;

  @BeforeEach
  public void setUp() {
    passwordResetContent = new Content();
    doReturn(passwordResetContent).when(passwordResetTransformer).toForgotPasswordResetContent(
        any(PasswordReset.class));
    forgottenPasswordResetBuilder = new ForgottenPasswordResetBuilder(ptiEmailProperties,
        passwordResetTransformer);
  }

  @Test
  void buildSendForgotPasswordRequest() {

    final SendforgottenPasswordReset url = forgottenPasswordResetBuilder
        .buildSendForgotPasswordRequest(PasswordReset.builder().build());

    final ForgottenPasswordReset forgottenPasswordReset = url.getIn0();
    assertThat(forgottenPasswordReset, notNullValue());

    final Template template = forgottenPasswordReset.getTemplate();
    assertThat(template, notNullValue());
    assertThat(template.getTemplateId(), is(EmailTemplate.PI_FORGOT_PASSWORD.getTemplateId()));

    final Login login = forgottenPasswordReset.getLogin();
    assertThat(login, notNullValue());
    assertThat(login.getUserName(), is(ptiEmailProperties.getUsername()));
    assertThat(login.getPassword(), is(ptiEmailProperties.getPassword()));

    assertThat(forgottenPasswordReset.getContent(), is(passwordResetContent));
  }
}