package uk.co.whitbread.dashboard.infrastructure.rest.client.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.common.exceptions.http.MALHttpException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardClientException extends RuntimeException implements MALHttpException {

  private int status;
  private String message;
  private String errorCode;
}
