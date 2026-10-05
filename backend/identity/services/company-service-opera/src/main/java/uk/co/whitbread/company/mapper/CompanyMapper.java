package uk.co.whitbread.company.mapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.co.whitbread.company.model.Address;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.model.BookingAllowances;
import uk.co.whitbread.company.model.Company;
import uk.co.whitbread.company.model.CompanyDetailsResponse;
import uk.co.whitbread.company.model.CompanyManagementQuestions;
import uk.co.whitbread.company.model.CompanySummary;
import uk.co.whitbread.company.model.ManagementInformationAnswer;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.CompanyManagementDetails;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Mapper(componentModel = "spring", uses = {
    EmployeeMapper.class, QuestionMapper.class,
    PaymentCardMapper.class, PriceMapper.class, CellCodeMapper.class,
    AddressMapper.class,
    BookingAlertsMapper.class})
public interface CompanyMapper {

  @Mapping(target = "requestedCompany.companyDetails.companyName", source = "companyName")
  @Mapping(target = "requestedCompany.companyDetails.alternateCompanyName", source = "alternateCompanyName")
  @Mapping(target = "requestedCompany.companyDetails.numberOfEmployees", source = "numberOfEmployees")
  @Mapping(target = "requestedCompany.companyDetails.mainEmployee", source = "mainContact")
  @Mapping(target = "requestedCompany.companyDetails.companyAddress", source = "companyAddress")
  @Mapping(target = "requestedCompany.companyCellCodes", source = "companyCellCodes")
  @Mapping(target = "requestedCompany.bookingAllowances", source = "bookingAllowances")
  @Mapping(target = "requestedCompany.bookingAlerts", source = "bookingAlerts")
  @Mapping(target = "requestedCompany.paymentDetails", source = "companyManagementDetails.paymentDetails")
  @Mapping(target = "requestedCompany.companyManagementDetails", expression = "java(toCompanyManagementQuestions(companyManagementDetails))")
  @Mapping(target = "requestedCompany.companyStatus.status", source = "status")
  @Mapping(target = "requestedCompany.companyDetails.companySector", source = "companySector")
  @Mapping(target = "requestedCompany.companyDetails.averageMonthlyBooking", source = "averageMonthlyBooking")
  @Mapping(target = "requestedCompany.companyDetails.numberOfEmployee", source = "numberOfEmployee")
  @Mapping(target = "requestedCompany.companyType", source = "companyType")
  CompanyDetailsResponse toCompanyDetailsResponse(GetCompanyResponse getCompanyResponse);


  @Mapping(target = "bookingAlertHotels", expression = "java(toBookingAlertHotels(bookingAlerts.getBookingAlertHotels()))")
  BookingAlerts toBookingAlerts(uk.co.whitbread.shared.cdh.model.company.BookingAlerts bookingAlerts);

  @Mapping(target = "userDefinedManagement", source = "questions")
  CompanyManagementQuestions toCompanyManagementQuestions(
      CompanyManagementDetails companyManagementDetails);

  @Named("toBookingAlertHotels")
  default List<String> toBookingAlertHotels(List<String> bookingAlertHotels){
    if (bookingAlertHotels == null) {
      return List.of();
    }
    return bookingAlertHotels;
  }

  @Mapping(target = "mainContact.address.companyName", source = "companyName")
  CompanyAccountRequest toUpdatedCompanyAccountRequest(@MappingTarget CompanyAccountRequest companyAccountRequest, CompanySummary companySummary);

  CompanyAccountRequest toUpdatedCompanyAccountRequest(@MappingTarget CompanyAccountRequest companyAccountRequest, BookingAlerts bookingAlerts);

  CompanyAccountRequest toUpdatedCompanyAccountRequest(@MappingTarget CompanyAccountRequest getCompanyResponse, BookingAllowances bookingAllowances);

  @Mapping(target = "cellCodes", source = "companyCellCodes")
  @Mapping(target = "companyManagementDetails.questions", source="companyManagementDetails.questions", qualifiedByName = "toCustomQuestion")
  CompanyAccountRequest toCompanyAccountRequest(GetCompanyResponse getCompanyResponse);


  @Mapping(target = "rateCaps.ukWide", source = "rateCaps.uKWide")
  uk.co.whitbread.shared.cdh.model.company.BookingAlerts toBookingAlerts(BookingAlerts bookingAlerts);

  @Mapping(target = "maxDinnerBudgets.ireland.currency", constant = "EUR")
  @Mapping(target = "maxDinnerBudgets.ukWide", source = "maxDinnerBudgets.uKWide")
  uk.co.whitbread.shared.cdh.model.company.BookingAllowances toBookingAllowances(BookingAllowances bookingAllowances);

  @Mapping(target = "countryCode", source = "address.country")
  GetCompaniesQueryParams toGetCompaniesQueryParams(String companyName, Address address);

  default void setManagementInformationAnswerIfNull(CompanyDetailsResponse companyDetailsResponse) {
    Optional.ofNullable(companyDetailsResponse.getRequestedCompany())
        .map(Company::getCompanyManagementDetails)
        .map(CompanyManagementQuestions::getPurchaseOrderManagement)
        .filter(pom -> Objects.isNull(pom.getManagementInformationAnswer()))
        .ifPresent(pom -> pom.setManagementInformationAnswer(ManagementInformationAnswer.builder().build()));

  }
}
