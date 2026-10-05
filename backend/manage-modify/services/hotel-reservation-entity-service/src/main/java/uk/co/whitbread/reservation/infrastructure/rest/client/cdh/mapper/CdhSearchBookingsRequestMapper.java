package uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;

@Mapper(componentModel = "spring")
public interface CdhSearchBookingsRequestMapper {

  @Mapping(target = "lastName", expression = "java(toConvertBookerOrGuestSurnameDto(cdhSearchBookingsRequest))")
  @Mapping(target = "emailAddress", source = "bookerEmail")
  @Mapping(target = "telephone", source = "bookerPhone")
  @Mapping(target = "postalCode", source = "bookerPostcode")
  @Mapping(target = "hotelCode", source = "hotelId")
  @Mapping(target = "arrivalDateFrom", source = "arrivalDateFrom")
  @Mapping(target = "arrivalDateTo", source = "arrivalDateTo")
  @Mapping(target = "bookingsDatabaseSearch", source = "bookingsDatabaseSearch")
  @Mapping(target = "companyName", source = "companyName")
  @Mapping(target = "thirdPartyReference", source = "thirdPartyBookingReferenceNumber")
  @Mapping(target = "pageSize", source = "pageSize")
  @Mapping(target = "pageNumber", source = "pageNumber")
  @Mapping(target = "continuationToken", source = "continuationToken")
  @Mapping(target = "bookingDate", expression = "java(null)")
  CdhReservationSearchCriteriaDto toDto(CdhSearchBookingsRequest cdhSearchBookingsRequest);

  default String toConvertBookerOrGuestSurnameDto(CdhSearchBookingsRequest cdhSearchBookingsRequest) {

    if (cdhSearchBookingsRequest.getBookerLastName() == null
            || cdhSearchBookingsRequest.getBookerLastName().isEmpty()) {
      return cdhSearchBookingsRequest.getGuestLastName();
    }
    return cdhSearchBookingsRequest.getBookerLastName();
  }
}
