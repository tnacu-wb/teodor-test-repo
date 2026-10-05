package uk.co.whitbread.business.tether.exception;

import uk.co.whitbread.piba.api.exception.PibaException;

public class BusinessTetherException extends PibaException {
	
	public static final String ERROR_CODE = "2603";
	
	
	public BusinessTetherException(String message) {
		super(message);
		withErrorCode(ERROR_CODE);
	}
	
	
	@Override
	public BusinessTetherException withErrorCode(String errorCode) {
		super.withErrorCode(errorCode);
		return this;
	}
}
