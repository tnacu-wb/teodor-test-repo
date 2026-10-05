package uk.co.whitbread.ohip.domain.model.eckoh.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class EckohWebhook implements SelfValidation<EckohWebhook> {

  @NotNull
  private String result;
  @NotNull
  private int resultCode;
  @NotNull
  private String maskedPan;
  @NotNull
  private String expiry;
  @NotNull
  private String scheme;
  @NotNull
  private EckohCardType type;
  @NotNull
  private String reference;
  @NotNull
  private String token;
  private String exception;

  public EckohWebhook(String result, int resultCode, String maskedPan, String expiry, String scheme, EckohCardType type,
      String reference, String token, String exception) {
    this.result = result;
    this.resultCode = resultCode;
    this.maskedPan = maskedPan;
    this.expiry = expiry;
    this.scheme = scheme;
    this.type = type;
    this.reference = reference;
    this.token = token;
    this.exception = exception;
    this.validateSelf();
  }
}
