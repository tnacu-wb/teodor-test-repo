package uk.co.whitbread.account.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.account.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AccountRegistrationRequest extends SelfValidation<AccountRegistrationRequest> {
  String captcha;
  @NotEmpty
  String password;
  @NotNull
  ContactDetail contactDetail;
  MarketingPreferencesRequest marketingPreferencesRequest;

  String basketReference;

  public AccountRegistrationRequest(String captcha, String password, ContactDetail contactDetail,
      MarketingPreferencesRequest marketingPreferencesRequest,
      String basketReference) {
    this.captcha = captcha;
    this.password = password;
    this.contactDetail = contactDetail;
    this.marketingPreferencesRequest = marketingPreferencesRequest;
    this.basketReference = basketReference;
    this.validateSelf();
  }
}
