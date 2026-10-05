package uk.co.whitbread.dashboard.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardElement {

  private DashboardType type;
  private Content content;

}