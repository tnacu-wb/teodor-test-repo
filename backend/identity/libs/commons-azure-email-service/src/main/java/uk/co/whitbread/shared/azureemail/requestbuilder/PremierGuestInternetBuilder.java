package uk.co.whitbread.shared.azureemail.requestbuilder;

import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.azureemail.api.ContentPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.NewPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.api.SendNewPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.properties.AzureEmailProperties;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

import static uk.co.whitbread.shared.azureemail.model.EmailTemplate.*;

@Component
public class PremierGuestInternetBuilder extends PTIEmailRequestBuilder {

  private final PiRegisterTransformer piRegisterTransformer;

  public PremierGuestInternetBuilder(PTIEmailProperties ptiEmailProperties,
      PiRegisterTransformer piRegisterTransformer) {

    super(ptiEmailProperties);
    this.piRegisterTransformer = piRegisterTransformer;
  }

  public SendNewPremierGuestInternet buildPremierGuestInternetRequest(PiRegister piRegister) {
    final SendNewPremierGuestInternet sendNewPremierGuestInternet = new SendNewPremierGuestInternet();
    sendNewPremierGuestInternet.setIn0(buildPremierGuestInternet(piRegister));
    return sendNewPremierGuestInternet;
  }

  private NewPremierGuestInternet buildPremierGuestInternet(PiRegister piRegister) {
    final NewPremierGuestInternet newPremierGuestInternet = new NewPremierGuestInternet();
    newPremierGuestInternet.setContentPremierGuestInternet(buildContentPremierGuestInternet(piRegister));
    newPremierGuestInternet.setLogin(buildLogin());
    newPremierGuestInternet.setTemplate(buildTemplate(getTemplateId(piRegister.getLanguageCode())));
    return newPremierGuestInternet;
  }

  private ContentPremierGuestInternet buildContentPremierGuestInternet(PiRegister piRegister) {
    return piRegisterTransformer.toPremierGuestInternetContent(piRegister);
  }

  private static String getTemplateId(String language) {
    if (AzureEmailProperties.LANGUAGE_DE.equalsIgnoreCase(language)) {
      return PI_REGISTER_DE.getTemplateId();
    }
    return PI_REGISTER.getTemplateId();
  }
}
