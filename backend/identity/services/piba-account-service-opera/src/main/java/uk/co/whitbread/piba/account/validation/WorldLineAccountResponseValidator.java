package uk.co.whitbread.piba.account.validation;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.exception.InvalidRequestException;
import uk.co.whitbread.piba.account.exception.PibaAccountException;
import uk.co.whitbread.piba.api.validation.WorldLineResponseValidator;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.ResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordResponse;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Component
@Slf4j
public class WorldLineAccountResponseValidator extends WorldLineResponseValidator {
	
	private static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";
	private static final String VALIDATION_ERROR = "ValidationError";
	private static final String SCHEMA_ERROR = "SchemaError";
	private static final String AUTH_ERROR ="AuthenticationError";

	public void validate(CustomerAccountViewTransactionsResponse response) {
		try {
			var validInnerResponse = ofNullable(response)
					.map(CustomerAccountViewTransactionsResponse::getResponse)
					.orElseThrow(() -> new PibaAccountException(ERROR_EMPTY_RESPONSE));
			validate(validInnerResponse);
		} catch (Exception e) {
			log.error("Validation failed for Worldline response CustomerAccountViewTransactionsResponse: {}",
					e.getMessage(), e);
			throw e;
		}
	}
	
	public void validate(CustomerAccountViewCurrentBalancesResponse response) {
		var validInnerResponse = ofNullable(response)
				.map(CustomerAccountViewCurrentBalancesResponse::getResponse)
				.orElseThrow(() -> new PibaAccountException(ERROR_EMPTY_RESPONSE));
		validate(validInnerResponse);
	}

	public void validate(CustomerAccountInvoiceListResponse response) {
		var validInnerResponse = ofNullable(response)
				.map(CustomerAccountInvoiceListResponse::getResponse)
				.orElseThrow(() -> new PibaAccountException(ERROR_EMPTY_RESPONSE));
		validate(validInnerResponse);
	}

	public void validate(CustomerAccountInvoiceDownloadResponse response) {
		var validInnerResponse = ofNullable(response)
				.map(CustomerAccountInvoiceDownloadResponse::getResponse)
				.orElseThrow(() -> new PibaAccountException(ERROR_EMPTY_RESPONSE));
		validate(validInnerResponse);
	}

	public void validate(CreditProposeNewLimitResponse response) {
		ofNullable(response)
				.map(CreditProposeNewLimitResponse::getResponse)
				.ifPresentOrElse(this::validateLimitResponse, this::throwPibaAccountException);
	}
	
	private void validateLimitResponse(ResponseType response) {
		ofNullable(response).filter(responseType -> VALIDATION_ERROR.equals(responseType.getResultCode()))
				.map(ResponseType::getErrors)
				.ifPresentOrElse(this::invalidCreditLimitException, () -> this.validate(response));
	}
	
	public void validate(TetheredUserDetailsGetResponse response) {
		var validInnerResponse = ofNullable(response)
				.map(TetheredUserDetailsGetResponse::getResponse)
				.orElseThrow(() -> new PibaAccountException(ERROR_EMPTY_RESPONSE));
		validate(validInnerResponse);
	}
	
	public void validate(LoginTetheredUserResponse response) {
		 ofNullable(response).map(LoginTetheredUserResponse::getResponse).
				filter(responseType -> (SCHEMA_ERROR.equals(responseType.getResultCode())
						|| AUTH_ERROR.equals(responseType.getResultCode())))
				.map(ResponseType::getErrors)
				.ifPresentOrElse(this::invalidInputException, () -> this.validate(response.getResponse()));
	}
	
	public void validate(UpdateMemorableWordResponse response) {
		ofNullable(response).map(UpdateMemorableWordResponse::getResponse).
				filter(responseType -> SCHEMA_ERROR.equals(responseType.getResultCode()))
				.map(ResponseType::getErrors)
				.ifPresentOrElse(this::invalidInputException, () -> this.validate(response.getResponse()));
	}
	
	private void invalidInputException(final List<ErrorInfoType> errorInfoTypes) {
		throw new InvalidRequestException(validateErrorMessage(errorInfoTypes));
	}
	
	private void throwPibaAccountException() {
		throw new PibaAccountException(ERROR_EMPTY_RESPONSE);
	}
	
	private void invalidCreditLimitException(final List<ErrorInfoType> errorInfoTypes) {
		throw new InvalidRequestException(validateErrorMessage(errorInfoTypes));
	}
	
	private String validateErrorMessage(List<ErrorInfoType> errorsItem) {
		return errorsItem.stream().map(this::getErrorMessage).collect(joining(","));
	}
	
	private String getErrorMessage(ErrorInfoType errorInfoType) {
		return ofNullable(errorInfoType.getInfo()).orElse(errorInfoType.getErrorCode());
	}
}

