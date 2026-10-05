package uk.co.whitbread.dashboard.infrastructure.rest.client.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardClientError {

  private int status;
  private String code;
  private String[] details;
}
