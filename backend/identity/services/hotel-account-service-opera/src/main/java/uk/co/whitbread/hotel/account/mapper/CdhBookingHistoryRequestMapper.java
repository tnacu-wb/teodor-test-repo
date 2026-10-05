package uk.co.whitbread.hotel.account.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Mapper(componentModel = "spring")
public interface CdhBookingHistoryRequestMapper {

  @Mapping(target = "customerAccountId", source = "customerAccountId")
  @Mapping(target = "bookingsDatabaseSearch", constant = "true")
  @Mapping(target = "continuationToken", source = "staysRequest.continuationToken")
  @Mapping(target = "pageNumber", source = "pageIndex")
  @Mapping(target = "pageSize", source = "pageSize")
  CdhReservationSearchCriteriaDto toPiCdhBookingHistoryRequest(BBStaysRequest staysRequest, Integer pageIndex, Integer pageSize, String customerAccountId);

  @Mapping(target = "employeeAccountId", source = "cdhEmployeeDetails.employeeAccountId")
  @Mapping(target = "companyAccountId", source = "cdhEmployeeDetails.companyAccountId")
  @Mapping(target = "bookingsDatabaseSearch", constant = "true")
  @Mapping(target = "continuationToken", source = "staysRequest.continuationToken")
  @Mapping(target = "pageNumber", source = "pageIndex")
  @Mapping(target = "pageSize", source = "pageSize")
  CdhReservationSearchCriteriaDto toBbCdhBookingHistoryRequest(BBStaysRequest staysRequest, Integer pageIndex, Integer pageSize, CdhEmployeeDetails cdhEmployeeDetails);

}
