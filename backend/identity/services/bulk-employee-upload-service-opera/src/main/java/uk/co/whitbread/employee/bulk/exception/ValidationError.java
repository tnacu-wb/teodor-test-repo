package uk.co.whitbread.employee.bulk.exception;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ValidationError",
    propOrder = {"errorCode", "errorDescription"}
)
public class ValidationError implements Serializable {
  private static final long serialVersionUID = 1L;
  protected String errorCode;
  protected String errorDescription;

  public ValidationError(String errorCode, String errorDescription) {
    this.errorCode = errorCode;
    this.errorDescription = errorDescription;

  }

  public String getErrorDescription() {
    return this.errorDescription;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
