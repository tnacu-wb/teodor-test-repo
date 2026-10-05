package uk.co.whitbread.token.infrastructure.rest.controller.token.model.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenDto {

  @Schema(description = "OAuth2 access token",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikpva"
          + "G4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs")
  private String accessToken;

  @Schema(description = "Type of the token", example = "Bearer")
  private String tokenType;

  @Schema(description = "Lifetime of the access token in seconds", example = "3600")
  private long expiresIn;

  @Schema(description = "Timestamp when the token was issued (UTC)", example = "2025-08-06T11:32:00Z")
  private String issuedAt;


}
