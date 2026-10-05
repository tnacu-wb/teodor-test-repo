package uk.co.whitbread.hotel.account.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.whitbread.hotel.account.exceptions.PibaGuidException;
import uk.co.whitbread.hotel.account.model.PibaTetheredGuidResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PibaGuidValidatorTest {
	
	LocalValidatorFactoryBean localValidatorFactory;
	
	@InjectMocks
	PibaGuidValidator pibaGuidValidator;

	@Mock
	Validator validator;

	@BeforeEach
	void setup() {
		localValidatorFactory = new LocalValidatorFactoryBean();
		localValidatorFactory.setProviderClass(HibernateValidator.class);
		localValidatorFactory.afterPropertiesSet();
	}

	@Test
	void verifyTetheredGuidResponseSuccess() {
		var pibaTetheredGuidResponse =
				new PibaTetheredGuidResponse(20, 30, of("74e52bbd-c1d9-4adb-97e6-96d38d34ca64"));
		var result = localValidatorFactory.validate(pibaTetheredGuidResponse);
		List<String> messages = getErrorMessages(result);
		assertThat("", result, hasSize(0));
	}
	
	@Test
	void verifyTetheredGuidResponsFailureNoResponse() {
		var pibaTetheredGuidResponse = new PibaTetheredGuidResponse(20, 30, null);
		var result = localValidatorFactory.validate(pibaTetheredGuidResponse);
		List<String> messages = getErrorMessages(result);
		assertThat("", result, hasSize(1));
		assertThat("", messages, contains("must not be null"));
	}
	
	private List<String> getErrorMessages(Set<ConstraintViolation<PibaTetheredGuidResponse>> result) {
		return result.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
	}

	@Test
	void verifyMultiTetheredGuidResponseSuccess() {
		var pibaTetheredGuidResponse =
				of(new PibaTetheredGuidResponse(20, 30, of("74e52bbd-c1d9-4adb-97e6-96d38d34ca64")),
						new PibaTetheredGuidResponse(20, 30, of("74e52bbd-c1d9-4adb-97e6-96d38d34ca64")));
		pibaGuidValidator.validateMultiple(pibaTetheredGuidResponse);
		verify(validator, times(1)).validate(pibaTetheredGuidResponse);
	}

	@Test
	void validateMultipleNull() {
		assertThrows(PibaGuidException.class, () -> pibaGuidValidator.validateMultiple(null));
	}
}
