package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class AnswerTypeNotFoundException extends AbstractMALException implements MAL400HttpException {
	private static final String ERROR_CODE = "020";

	@Override
	public String getErrorCode() {
		return ERROR_CODE;
	}
	
	public AnswerTypeNotFoundException() {
		super("Answer Type must not be empty");
	}
}
