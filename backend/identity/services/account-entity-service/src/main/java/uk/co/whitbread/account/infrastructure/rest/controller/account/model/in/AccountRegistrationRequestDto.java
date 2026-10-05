package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountRegistrationRequestDto {
  private String captcha;

  @NotEmpty
  private String password;

  private String country;

  private String language;

  private String basketReference;

  @NotNull
  @Valid
  private ContactDetailDto contactDetail;

  private UpdatePreferencesRequestV2Dto updatePreferencesRequest;

}
