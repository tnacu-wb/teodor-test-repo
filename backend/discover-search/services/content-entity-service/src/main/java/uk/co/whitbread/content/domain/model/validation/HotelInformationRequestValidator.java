package uk.co.whitbread.content.domain.model.validation;

import static java.util.Optional.of;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;

public class HotelInformationRequestValidator implements
    ConstraintValidator<HotelInformationRequestConstraint, HotelInformationRequest> {

  @Override
  public void initialize(HotelInformationRequestConstraint constraint) {
    //Initialization not required.
  }

  @Override
  public boolean isValid(HotelInformationRequest request, ConstraintValidatorContext context) {
    if (request == null) {
      return true;
    }

    var isHotelIdSet = of(request)
        .map(HotelInformationRequest::getHotelId)
        .isPresent();
    var isSlugSet = of(request)
        .map(HotelInformationRequest::getSlug)
        .isPresent();

    if (isHotelIdSet) {
      return !isSlugSet;
    }

    return isSlugSet;
  }

}
