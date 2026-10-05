package uk.co.whitbread.hotel.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request for sending email to leisure account")
public class SendEmailRequest {

  @Schema(description = "Customer firstName")
  @NotBlank(message = "firstName is required")
  private String firstName;

  @Schema(description = "Customer lastName")
  @NotBlank(message = "lastName is required")
  private String lastName;

  @Schema(description = "OTP for email verification", example = "123456")
  private String otp;

  @Schema(description = "Language code for the email", example = "en")
  @NotBlank(message = "Language is required")
  private String language;

  @Schema(description = "Type of message to send", example = "PASSWORD_RESET")
  @NotBlank(message = "Message type is required")
  private String messageType;
}


