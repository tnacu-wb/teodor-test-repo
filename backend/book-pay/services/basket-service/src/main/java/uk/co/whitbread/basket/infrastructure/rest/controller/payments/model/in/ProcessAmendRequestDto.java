package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class ProcessAmendRequestDto implements SelfValidation<ProcessAmendRequestDto> {

  @NotNull
  private String language;
  @NotNull
  private String channel;
  private String paymentOptionSelected;
  private String emailAddress;
  private String ccAgentId;
  private String token;

  public ProcessAmendRequestDto(String language, String channel,
      String paymentOptionSelected, String emailAddress, String ccAgentId,
      String token) {
    setFieldValues(language, channel, paymentOptionSelected,
        emailAddress, ccAgentId, token);
    this.validateSelf();
  }

  private void setFieldValues(String language, String channel,
      String paymentOptionSelected, String emailAddress, String ccAgentId, String token) {
    this.language = language;
    this.channel = channel;
    this.paymentOptionSelected = paymentOptionSelected;
    this.emailAddress = emailAddress;
    this.ccAgentId = ccAgentId;
    this.token = token;
  }
}
