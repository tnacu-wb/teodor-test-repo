package uk.co.whitbread.payments.infrastructure.rest.client.token.model.out;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TokenResponseDto {
  private String clientToken;
  private String clientId;
  private LocalDateTime generatedAt;
}