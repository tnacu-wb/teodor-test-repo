package uk.co.whitbread.piba.registration.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class InValidTokenException extends AbstractMALException implements MAL400HttpException {
	
	private static final String ERROR_CODE = "028";

	public InValidTokenException() {
		super("Invalid companyId and EmployeeId details");
	}

	public InValidTokenException(String message) {
		super(message);
	}

	@Override
	public String getErrorCode() {
		return ERROR_CODE;
	}
}
