package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class SendEmailDto implements SelfValidation<SendEmailDto> {

  @NotNull
  private Boolean sendEmail;

  public SendEmailDto(Boolean sendEmail) {
    this.sendEmail = sendEmail;
    this.validateSelf();
  }
}