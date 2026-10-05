package uk.co.whitbread.hotel.card.mapper;

import static uk.co.whitbread.hotel.card.utils.EnumConverter.getEnum;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.card.client.account.model.AccessLevel;
import uk.co.whitbread.hotel.card.client.account.model.Customer;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

  @Mapping(target = "companyId", source = "companyAccountId")
  @Mapping(target = "business.accessLevel", qualifiedByName = "toAccessLevel", source = "accessLevel")
  @Mapping(target = "business.employeeId", source = "employeeAccountId")
  Customer toCustomer(GetEmployeeResponse response);

  EmployeeAccountRequest toEmployeeAccountRequest(GetEmployeeResponse getEmployeeResponse);

  @Named("toAccessLevel")
  default AccessLevel toAccessLevel(String accessLevel) {
    return getEnum(AccessLevel.class, accessLevel);
  }

}