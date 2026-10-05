package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.apache.logging.log4j.util.Strings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetailsResponse;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.FetchApplicationDetailsResponseDto;

@Mapper(componentModel = "spring")
public interface ApplicationDetailsResponseMapper {

  @Mapping(target = "contactDetails", source = "worldlineResponse.data.contactDetails")
  @Mapping(target = "companyDetails", source = "worldlineResponse.data.companyDetails")
  @Mapping(target = "cardDetails", source = "worldlineResponse.data.cardDetails")
  @Mapping(target = "status", expression = "java(toStatusModel(cdhResponse, worldlineResponse))")
  @Mapping(target = "campaignCode", source = "worldlineResponse.data.applicationDetails.campaignCode")
  @Mapping(target = "incentiveId", source = "worldlineResponse.data.applicationDetails.incentiveId")
  @Mapping(target = "incentiveCode", source = "worldlineResponse.data.applicationDetails.incentiveCode")
  @Mapping(target = "termsAndConditionsAccepted",
      source = "worldlineResponse.data.applicationDetails.termsAndConditionsAccepted")
  @Mapping(target = "registrationQuestion", source = "worldlineResponse.data.applicationDetails.registrationQuestion")
  @Mapping(target = "registrationAnswer", source = "worldlineResponse.data.applicationDetails.registrationAnswer")
  @Mapping(target = "accountName", expression = "java(toAccountNameModel(cdhResponse, worldlineResponse))")
  ApplicationDetailsResponse toModel(
      uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse cdhResponse,
      FetchApplicationDetailsResponseDto worldlineResponse);

  @Named("toStatusModel")
  default String toStatusModel(
      uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse cdhResponse,
      FetchApplicationDetailsResponseDto worldlineResponse) {
    return Strings.isBlank(worldlineResponse.getData().getApplicationDetails().getStatus())
        ? cdhResponse.getStage() : worldlineResponse.getData().getApplicationDetails().getStatus();
  }

  @Named("toAccountNameModel")
  default String toAccountNameModel(
      uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse cdhResponse,
      FetchApplicationDetailsResponseDto worldlineResponse) {
    return Strings.isBlank(worldlineResponse.getData().getCompanyDetails().getCompanyName())
        ? cdhResponse.getAccountName() : worldlineResponse.getData().getCompanyDetails().getCompanyName();
  }
}
