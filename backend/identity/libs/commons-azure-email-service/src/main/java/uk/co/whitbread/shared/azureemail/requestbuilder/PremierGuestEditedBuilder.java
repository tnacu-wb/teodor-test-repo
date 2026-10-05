package uk.co.whitbread.shared.azureemail.requestbuilder;

import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.azureemail.api.ContentPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.NewPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestEdited;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.model.EmailTemplate;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

@Component
public class PremierGuestEditedBuilder extends PTIEmailRequestBuilder {

  private final AccountUpdateTransformer accountUpdateTransformer;

  public PremierGuestEditedBuilder(
      PTIEmailProperties ptiEmailProperties,
      AccountUpdateTransformer accountUpdateTransformer) {

    super(ptiEmailProperties);
    this.accountUpdateTransformer = accountUpdateTransformer;
  }

  public SendNewPremierGuestEdited buildNewPremierGuestEditedRequest(AccountUpdate accountUpdate) {
    final SendNewPremierGuestEdited request = new SendNewPremierGuestEdited();
    request.setIn0(buildNewPremierGuestEdited(accountUpdate));
    return request;
  }

  private NewPremierGuestEdited buildNewPremierGuestEdited(AccountUpdate accountUpdate) {
    final NewPremierGuestEdited newPremierGuestEdited = new NewPremierGuestEdited();
    newPremierGuestEdited.setTemplate(buildTemplate(EmailTemplate.UPDATE_ACCOUNT.getTemplateId()));
    newPremierGuestEdited.setLogin(buildLogin());
    newPremierGuestEdited.setContentPremierGuestEdited(buildPremierGuestEditedContent(accountUpdate));
    return newPremierGuestEdited;
  }

  private ContentPremierGuestEdited buildPremierGuestEditedContent(AccountUpdate updateRequest) {
    return accountUpdateTransformer.toPremierGuestEditedContent(updateRequest);
  }
}
