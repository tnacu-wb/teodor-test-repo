package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static uk.co.whitbread.shared.azureemail.requestbuilder.RequestBuilderData.ptiEmailProperties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.azureemail.api.ContentPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.Login;
import uk.co.whitbread.shared.azureemail.api.NewPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.Template;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;

@ExtendWith(MockitoExtension.class)
class PremierGuestEditedBuilderTest {

  private PremierGuestEditedBuilder premierGuestEditedBuilder;
  private ContentPremierGuestEdited contentPremierGuestEdited;
  @Mock
  private AccountUpdateTransformer accountUpdateTransformer;

  @BeforeEach
  public void setUp() {
    contentPremierGuestEdited = new ContentPremierGuestEdited();
    doReturn(contentPremierGuestEdited).when(accountUpdateTransformer).toPremierGuestEditedContent0(
        any(AccountUpdate.class));
    doCallRealMethod().when(accountUpdateTransformer).toPremierGuestEditedContent(
        any(AccountUpdate.class));
    premierGuestEditedBuilder = new PremierGuestEditedBuilder(ptiEmailProperties,
        accountUpdateTransformer);
  }

  @Test
  void buildNewPremierGuestEditedRequest() {
    final String lastName = "lastName";
    final String title = "title";
    final String telephone = "074Tel";
    final String mobile = "074Mob";

    final AccountUpdate accountUpdate = AccountUpdate.builder()
        .lastName(lastName)
        .title(title)
        .telephone(telephone)
        .mobile(mobile)
        .build();
    final SendNewPremierGuestEdited sendNewPremierGuestEdited =
        premierGuestEditedBuilder.buildNewPremierGuestEditedRequest(accountUpdate);

    final NewPremierGuestEdited newPremierGuestEdited = sendNewPremierGuestEdited.getIn0();
    assertThat(newPremierGuestEdited, notNullValue());

    final Template template = newPremierGuestEdited.getTemplate();
    assertThat(template, notNullValue());
    assertThat(template.getTemplateId(), is(EmailTemplate.UPDATE_ACCOUNT.getTemplateId()));

    final Login login = newPremierGuestEdited.getLogin();
    assertThat(login, notNullValue());
    assertThat(login.getUserName(), is(ptiEmailProperties.getUsername()));
    assertThat(login.getPassword(), is(ptiEmailProperties.getPassword()));

    final ContentPremierGuestEdited content = newPremierGuestEdited.getContentPremierGuestEdited();
    assertThat(content, is(contentPremierGuestEdited));
    assertThat(content.getInfName(), is(String.format("%s %s", title, lastName)));
    assertThat(content.getTelephone(), is(telephone));
  }

  @Test
  void buildNewPremierGuestEditedRequest_telephoneFallbackToMobile() {
    final String mobile = "074Mob";

    final AccountUpdate accountUpdate = AccountUpdate.builder()
        .mobile(mobile)
        .build();
    final SendNewPremierGuestEdited sendNewPremierGuestEdited =
        premierGuestEditedBuilder.buildNewPremierGuestEditedRequest(accountUpdate);

    final NewPremierGuestEdited newPremierGuestEdited = sendNewPremierGuestEdited.getIn0();
    assertThat(newPremierGuestEdited, notNullValue());

    final ContentPremierGuestEdited content = newPremierGuestEdited.getContentPremierGuestEdited();
    assertThat(content, notNullValue());
    assertThat(content.getTelephone(), is(mobile));
  }
}