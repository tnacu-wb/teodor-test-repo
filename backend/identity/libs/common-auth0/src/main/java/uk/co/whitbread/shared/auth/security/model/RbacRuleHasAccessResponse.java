package uk.co.whitbread.shared.auth.security.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RbacRuleHasAccessResponse {

  private Boolean hasAccess;
  private LocalDateTime generatedAt;
}
