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
import uk.co.whitbread.shared.azureemail.api.ContentPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.Login;
import uk.co.whitbread.shared.azureemail.api.NewPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.Template;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.model.PiRegister;

@ExtendWith(MockitoExtension.class)
class PremierGuestInternetBuilderTest {

  private PremierGuestInternetBuilder premierGuestInternetBuilder;
  private ContentPremierGuestInternet contentPremierGuestInternet;
  @Mock
  private PiRegisterTransformer piRegisterTransformer;

  @BeforeEach
  public void setUp() {
    contentPremierGuestInternet = new ContentPremierGuestInternet();
    doReturn(contentPremierGuestInternet).when(piRegisterTransformer).toPremierGuestInternetContent(
        any(PiRegister.class));
    premierGuestInternetBuilder = new PremierGuestInternetBuilder(ptiEmailProperties,
        piRegisterTransformer);
  }

  @Test
  void buildNewPremierGuestInternetRequest() {
    final String lastName = "lastName";
    final String title = "title";
    final String telephone = "074Tel";

    final PiRegister piRegister = PiRegister.builder()
        .guestSurname(lastName)
        .guestTitle(title)
        .telephone(telephone)
        .build();
    final SendNewPremierGuestInternet sendNewPremierGuestInternet =
        premierGuestInternetBuilder.buildPremierGuestInternetRequest(piRegister);

    final NewPremierGuestInternet newPremierGuestInternet = sendNewPremierGuestInternet.getIn0();
    assertThat(newPremierGuestInternet, notNullValue());

    final Template template = newPremierGuestInternet.getTemplate();
    assertThat(template, notNullValue());
    assertThat(template.getTemplateId(), is(EmailTemplate.PI_REGISTER.getTemplateId()));

    final Login login = newPremierGuestInternet.getLogin();
    assertThat(login, notNullValue());
    assertThat(login.getUserName(), is(ptiEmailProperties.getUsername()));
    assertThat(login.getPassword(), is(ptiEmailProperties.getPassword()));

    final ContentPremierGuestInternet content = newPremierGuestInternet.getContentPremierGuestInternet();
    assertThat(content, is(contentPremierGuestInternet));
  }

  @Test
  void buildNewPremierGuestInternetRequestInGerman() {
    final String lastName = "lastName";
    final String title = "title";
    final String telephone = "074Tel";
    final String languageCode = "DE";

    final PiRegister piRegister = PiRegister.builder()
            .guestSurname(lastName)
            .guestTitle(title)
            .telephone(telephone)
            .languageCode(languageCode)
            .build();
    final SendNewPremierGuestInternet sendNewPremierGuestInternet =
            premierGuestInternetBuilder.buildPremierGuestInternetRequest(piRegister);

    final NewPremierGuestInternet newPremierGuestInternet = sendNewPremierGuestInternet.getIn0();
    assertThat(newPremierGuestInternet, notNullValue());

    final Template template = newPremierGuestInternet.getTemplate();
    assertThat(template, notNullValue());
    assertThat(template.getTemplateId(), is(EmailTemplate.PI_REGISTER_DE.getTemplateId()));

    final Login login = newPremierGuestInternet.getLogin();
    assertThat(login, notNullValue());
    assertThat(login.getUserName(), is(ptiEmailProperties.getUsername()));
    assertThat(login.getPassword(), is(ptiEmailProperties.getPassword()));

    final ContentPremierGuestInternet content = newPremierGuestInternet.getContentPremierGuestInternet();
    assertThat(content, is(contentPremierGuestInternet));
  }

}