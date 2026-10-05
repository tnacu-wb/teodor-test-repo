package uk.co.whitbread.company.employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourceDetails {
  private String channel;
  private String journey;
  private String locale;
}
