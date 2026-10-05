package uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class EmailRequestDto implements SelfValidation<EmailRequestDto> {

  @NotNull
  private String bookingReference;

  private String email;

  private String emailRequestType;
  private List<DepositsDto> deposits;
  private boolean failedRefund;

  public EmailRequestDto(String bookingReference, String email, String emailRequestType,
      List<DepositsDto> deposits, boolean failedRefund) {
    this.bookingReference = bookingReference;
    this.email = email;
    this.emailRequestType = emailRequestType;
    this.deposits = deposits;
    this.failedRefund = failedRefund;
    this.validateSelf();
  }
}