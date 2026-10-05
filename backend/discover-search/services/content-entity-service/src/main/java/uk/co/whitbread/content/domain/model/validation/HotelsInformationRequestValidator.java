package uk.co.whitbread.content.domain.model.validation;

import static java.util.Optional.of;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;

public class HotelsInformationRequestValidator implements
    ConstraintValidator<HotelsInformationRequestConstraint, HotelsInformationRequest> {

  @Override
  public void initialize(HotelsInformationRequestConstraint constraint) {
    //Initialization not required.
  }

  @Override
  public boolean isValid(HotelsInformationRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    final boolean[] isHotelIdsSet = new boolean[1];
    Optional.ofNullable(request).ifPresent(
        req ->
            isHotelIdsSet[0] = of(req)
                .map(HotelsInformationRequest::getHotelIds).get().parallelStream()
                .allMatch(StringUtils::isNotEmpty));

    return isHotelIdsSet[0];
  }

}
