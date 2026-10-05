package uk.co.whitbread.infrastructure.rest.client.accounts.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellCode {
  private String type;
  private String description;
}
