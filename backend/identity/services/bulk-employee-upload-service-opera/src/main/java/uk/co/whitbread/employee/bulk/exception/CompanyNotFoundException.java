package uk.co.whitbread.employee.bulk.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

public class CompanyNotFoundException extends AbstractMALException implements
    MAL404HttpException {

  public static final String ERROR_CODE = "2517";

  public CompanyNotFoundException(String message) {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
