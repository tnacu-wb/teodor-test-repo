package uk.co.whitbread.piba.account.validation;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.exception.PibaGuidException;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
@Component
public class PibaGuidValidator {
	private final Validator validator;

	public void validate(PibaTetheredGuidResponse response) {
		ofNullable(response)
				.map(PibaTetheredGuidResponse::getTetheredGuid)
				.orElseThrow(PibaGuidException::new);

		var errors = validator.validate(response);
		if (errors.size() > 0) {
			log.error("Validation errors {} for Retrieve Guids Response {}.", errors, response);
			throw new PibaGuidException(errors.toString());
		}
	}

	public void validateMultiple(List<PibaTetheredGuidResponse> response) {
		ofNullable(response)
				.orElseThrow(PibaGuidException::new)
				.stream()
				.map(PibaTetheredGuidResponse::getTetheredGuid)
				.filter(Objects::isNull)
				.forEach(tetheredGuid -> {
					throw new PibaGuidException();
				});
		var errors = validator.validate(response);
		if (errors.size() > 0) {
			log.error("Validation errors {} for Retrieve Multi Guids Response {}.", errors, response);
			throw new PibaGuidException(errors.toString());
		}
	}
	
}
