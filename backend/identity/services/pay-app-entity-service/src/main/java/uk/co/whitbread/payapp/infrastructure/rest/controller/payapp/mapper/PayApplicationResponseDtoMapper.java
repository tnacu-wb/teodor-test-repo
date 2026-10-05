package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.out.AddApplicationCardResponse;
import uk.co.whitbread.payapp.domain.model.out.AppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.AppPreCheckResponse;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.DeletePayApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.DirectDebitResponse;
import uk.co.whitbread.payapp.domain.model.out.FetchApplicationsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetAppCardsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetDdSepaFormStatusResponse;
import uk.co.whitbread.payapp.domain.model.out.GetUserPreferencesResponse;
import uk.co.whitbread.payapp.domain.model.out.InitializeApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.ShareAppResponse;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppContactDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateResumeUrlResponse;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DeletePayApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DirectDebitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.FetchApplicationsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetDdSepaFormStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetUserPreferencesResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.InitializeApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ShareAppResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.SubmitApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppContactDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateResumeUrlResponseDto;

@Mapper(componentModel = "spring")
public interface PayApplicationResponseDtoMapper {

  InitializeApplicationResponseDto toDto(InitializeApplicationResponse initializeApplicationResponse);

  FetchApplicationsResponseDto toDto(FetchApplicationsResponse fetchApplicationsResponse);

  UpdateAppContactDetailsResponseDto toDto(
      UpdateAppContactDetailsResponse updateAppContactDetailsResponse);

  UpdateAppCompanyDetailsResponseDto toDto(
      UpdateAppCompanyDetailsResponse updateAppCompanyDetailsResponse);

  DeletePayApplicationResponseDto toDto(DeletePayApplicationResponse deletePayApplicationResponse);

  AppCompanyDetailsResponseDto toDto(AppCompanyDetailsResponse appCompanyDetailsResponse);

  List<GetUserPreferencesResponseDto> toDto(
      List<GetUserPreferencesResponse> userPreferencesResponse);

  ApplicationDetailsResponseDto toDto(ApplicationDetailsResponse applicationDetailsResponse);

  ShareAppResponseDto toDto(ShareAppResponse shareAppResponse);

  GetAppCardsResponseDto toDto(GetAppCardsResponse getAppCardsResponse);

  AddApplicationCardResponseDto toDto(AddApplicationCardResponse addApplicationCardResponse);

  SubmitApplicationResponseDto toDto(SubmitApplicationResponse submitApplicationResponse);

  AppPreCheckResponseDto toDto(AppPreCheckResponse appPreCheckResponse);

  DirectDebitResponseDto toDto(DirectDebitResponse directDebitResponse);

  UpdateResumeUrlResponseDto toDto(UpdateResumeUrlResponse response);

  GetDdSepaFormStatusResponseDto toDto(GetDdSepaFormStatusResponse getDDSEPAFormStatusResponse);
}
