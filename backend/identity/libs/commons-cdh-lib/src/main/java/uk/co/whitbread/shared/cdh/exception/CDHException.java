package uk.co.whitbread.shared.cdh.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.co.whitbread.common.exceptions.http.MALHttpException;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CDHException extends RuntimeException implements MALHttpException {

  private int status;
  private String message;
  private String errorCode;

  public CDHException addStatus(int status) {
    setStatus(status);
    return this;
  }
}
