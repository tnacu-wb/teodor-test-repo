package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.ProfileStayingGuestDetailsDto;

@Mapper(componentModel = "spring")
public interface CreateProfileRequestMapper {

  ProfileStayingGuestDetails toProfileRequestModel(
      ProfileStayingGuestDetailsDto stayingGuestDetails);

}
