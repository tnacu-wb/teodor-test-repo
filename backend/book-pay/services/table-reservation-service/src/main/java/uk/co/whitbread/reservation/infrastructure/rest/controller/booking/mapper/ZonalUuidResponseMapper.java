package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.aem.ZonalUuidResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ZonalUuidResponseDto;

@Mapper(componentModel = "spring")
public interface ZonalUuidResponseMapper {

  default List<ZonalUuidResponseDto> toDto(List<ZonalUuidResponse> zonalUuidResponses) {
    if (zonalUuidResponses == null) {
      return new ArrayList<>();
    }
    return zonalUuidResponses.stream()
        .map(response -> ZonalUuidResponseDto.builder()
            .id(response.getId())
            .title(response.getTitle())
            .path(response.getPath())
            .address1(response.getAddress1())
            .address2(response.getAddress2())
            .address3(response.getAddress3())
            .address4(response.getAddress4())
            .contactInfo(response.getContactInfo())
            .googleMapURL(response.getGoogleMapURL())
            .externalSystemIdentifier(response.getExternalSystemIdentifier())
            .latitude(response.getLatitude())
            .longitude(response.getLongitude())
            .externalSourceSystem(response.getExternalSourceSystem())
            .bookingHeroImage(response.getBookingHeroImage())
            .bookingHeroBackgroundImage(response.getBookingHeroBackgroundImage())
            .build())
        .toList();
  }

}
