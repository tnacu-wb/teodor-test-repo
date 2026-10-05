package uk.co.whitbread.shared.azureemail.requestbuilder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.azureemail.api.Login;
import uk.co.whitbread.shared.azureemail.api.Template;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

@Component
@RequiredArgsConstructor
public abstract class PTIEmailRequestBuilder {

  protected final PTIEmailProperties ptiEmailProperties;

  protected Login buildLogin() {
    final Login login = new Login();
    login.setUserName(ptiEmailProperties.getUsername());
    login.setPassword(ptiEmailProperties.getPassword());
    return login;
  }

  protected Template buildTemplate(String emailTemplateId) {
    final Template template = new Template();
    template.setTemplateId(emailTemplateId);
    return template;
  }
}
