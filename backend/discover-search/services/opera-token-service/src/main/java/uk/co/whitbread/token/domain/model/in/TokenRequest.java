package uk.co.whitbread.token.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

  @NotBlank(message = "Provider ID is required")
  @Pattern(regexp = "^[a-zA-Z0-9_-]+$",
      message = "Provider ID must be alphanumeric with hyphens and underscores only")
  private String providerId;
}
