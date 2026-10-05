package uk.co.whitbread.piba.registration.validation;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.registration.exception.PibaGuidException;
import uk.co.whitbread.piba.registration.model.PibaTetheredGuidResponse;

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
		if (! errors.isEmpty()) {
			log.error("Validation errors {} for Save Guid Response {}.", errors, response);
			throw new PibaGuidException(errors.toString());
		}
	}
	
}
