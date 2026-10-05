package uk.co.whitbread.hotel.register.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.utils.register.cdh.CompanyUtils;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class, builder = @Builder(disableBuilder = true))
public interface CompanyMapper {

  @Mapping(source = "customer", target = "mainContact")
  @Mapping(source = "contactDetail.address", target = "companyAddress")
  @Mapping(source = "alternativeName", target = "alternateCompanyName")
  CompanyAccountRequest toCompanyAccountRequest(Customer customer);

  @Mapping(target = "cellCodes", source = "companyCellCodes")
  CompanyAccountRequest toCompanyAccountRequest(GetCompanyResponse getCompanyResponse);

  @Mapping(source = "request", target = "mainContact")
  @Mapping(source = "companyName", target = "companyName")
  @Mapping(source = "companyName", target = "alternateCompanyName")
  @Mapping(source = "address", target = "companyAddress")
  @Mapping(source = "uniqueTaxpayerReference", target = "uniqueTaxReference")
  CompanyAccountRequest toCompanyAccountRequest(InnBRegistrationStepOneRequest request);

  @Mapping(source = "companySector", target = "companySector")
  @Mapping(source = "averageMonthlyBooking", target = "averageMonthlyBooking")
  @Mapping(source = "numberOfEmployee", target = "numberOfEmployee")
  void toUpdatedCompanyAccountRequest(InnBRegistrationStepTwoRequest request, @MappingTarget CompanyAccountRequest companyAccountRequest);

  @AfterMapping
  default void setDefaultCompanyValues(@MappingTarget CompanyAccountRequest result) {
    CompanyUtils.setDefaultCompanyValues(result);
  }

  @AfterMapping
  default void setSocialMediaHandle(InnBRegistrationStepOneRequest request,
      @MappingTarget CompanyAccountRequest result) {
    if (StringUtils.isNotBlank(request.getSocialMediaType()) && StringUtils.isNotBlank(
        request.getSocialMediaId())) {
      result.setSocialMediaHandle(StringUtils.joinWith("|", request.getSocialMediaType(),
          request.getSocialMediaId()));
    }
  }

}
