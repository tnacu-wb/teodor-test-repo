package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.UpdateProfileRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateProfileRequestMapper {

  UpdateProfileRequest toUpdateProfileRequestModel(
      UpdateProfileRequestDto updateProfileRequestDto);

}
