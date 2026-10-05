package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;

@Slf4j
public class DistributionMaxRoomValidator implements
    ConstraintValidator<DistributionMaxRoomConstraint, DistributionSearchCriteria> {

  @Value("${hotel.search.distribution.maxRooms}")
  private int defaultMaxNumberOfRooms;

  @Override
  public void initialize(final DistributionMaxRoomConstraint constraintAnnotation) {
    //This validator does not need any initialization
  }

  @Override
  public boolean isValid(final DistributionSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing 'Max Room Validator' for :{}", searchCriteria);
    return (!(isNull(searchCriteria))
        && isValidRoomValue(searchCriteria));
  }

  public boolean isNull(final DistributionSearchCriteria searchCriteria) {
    log.debug("Validating null inputs for rooms");
    return (searchCriteria == null);
  }

  public boolean isValidRoomValue(final DistributionSearchCriteria searchCriteria) {
    log.debug("validating value of room.");
    boolean isValidRoomValue = false;
    if (searchCriteria.getRooms() >= 1 && searchCriteria.getRooms() <= defaultMaxNumberOfRooms) {
      isValidRoomValue = true;
    }
    return isValidRoomValue;
  }

}
