package uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohWebhookRequestDto {

  @NotNull
  @JsonProperty("result")
  private String result;
  @NotNull
  @JsonProperty("result_code")
  private int resultCode;
  @NotNull
  @JsonProperty("masked_pan")
  private String maskedPan;
  @NotNull
  @JsonProperty("payment_id")
  private String paymentId;
  @NotNull
  @JsonProperty("expiry")
  private String expiry;
  @NotNull
  @JsonProperty("scheme")
  private String scheme;
  @NotNull
  @JsonProperty("type")
  private CardTypeDto type;
  @NotNull
  @JsonProperty("reference")
  private String reference;
  @NotNull
  @JsonProperty("token")
  private String token;
  @JsonProperty("exception")
  private String exception;

}
