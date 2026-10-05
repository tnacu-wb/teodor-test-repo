package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.account.service.generated.models.CustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetails;

@Mapper(componentModel = "spring", uses = {CustomerAddressMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerRequestMapper {

  @Mapping(target = "contactDetail.title", source = "title")
  @Mapping(target = "contactDetail.firstName", source = "firstName")
  @Mapping(target = "contactDetail.lastName", source = "lastName")
  @Mapping(target = "contactDetail.email", source = "emailAddress")
  @Mapping(target = "contactDetail.mobile", source = "mobile")
  @Mapping(target = "contactDetail.telephone", source = "landline")
  @Mapping(target = "contactDetail.address", source = "address")
  CustomerRequest toDto(BookerDetails bookerDetails);
}
