package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardRequest;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.AppPreCheckRequest;
import uk.co.whitbread.payapp.domain.model.in.ApplicationDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.DeleteCardRequest;
import uk.co.whitbread.payapp.domain.model.in.DeletePayApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppCardsRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppLookupRequest;
import uk.co.whitbread.payapp.domain.model.in.GetDdSepaFormStatusRequest;
import uk.co.whitbread.payapp.domain.model.in.GetUserPreferencesRequest;
import uk.co.whitbread.payapp.domain.model.in.InitializeApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.RemoveParticipantRequest;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.domain.model.in.ShareAppRequest;
import uk.co.whitbread.payapp.domain.model.in.SubmitApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppContactDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateResumeUrlRequest;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AddApplicationCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ApplicationDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeleteCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeletePayApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DirectDebitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppCardsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppLookupRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetDdSepaFormStatusRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetUserPreferencesRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.InitializeApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.RemoveParticipantRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ShareAppRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.SubmitApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppContactDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateResumeUrlRequestDto;

@Mapper(componentModel = "spring")
public interface PayApplicationRequestDtoMapper {

  InitializeApplicationRequest toModel(
      InitializeApplicationRequestDto initializeApplicationRequestDto);

  UpdateAppContactDetailsRequest toModel(
      UpdateAppContactDetailsRequestDto updateAppContactDetailsRequestDto);

  @Mapping(target = "appCompanyDetails", source = "appCompanyDetailsDto")
  UpdateAppCompanyDetailsRequest toModel(
      UpdateAppCompanyDetailsRequestDto updateAppCompanyDetailsRequestDto);

  AppCompanyDetailsRequest toModel(
      AppCompanyDetailsRequestDto appCompanyDetailsRequestDto);

  DeletePayApplicationRequest toModel(
      DeletePayApplicationRequestDto deletePayApplicationRequestDto);

  GetAppLookupRequest toModel(GetAppLookupRequestDto getAppLookupRequestDto);

  GetUserPreferencesRequest toModel(GetUserPreferencesRequestDto userPreferencesRequestDto);

  ApplicationDetailsRequest toModel(ApplicationDetailsRequestDto applicationDetailsRequestDto);

  AddApplicationCardRequest toModel(AddApplicationCardRequestDto addApplicationCardRequestDto);

  ShareAppRequest toModel(ShareAppRequestDto shareAppRequestDto);

  DeleteCardRequest toModel(DeleteCardRequestDto deleteCardRequestDto);

  GetAppCardsRequest toModel(GetAppCardsRequestDto getAppCardsRequestDto);

  RemoveParticipantRequest toModel(RemoveParticipantRequestDto removeParticipantRequestDto);

  SubmitApplicationRequest toModel(SubmitApplicationRequestDto submitApplicationRequestDto);

  @Mapping(target = "email", source = "email")
  @Mapping(target = "scheme", source = "scheme")
  AppPreCheckRequest toModel(Scheme scheme, String email);

  DirectDebitRequest toModel(DirectDebitRequestDto directDebitRequestDto);

  UpdateResumeUrlRequest toModel(UpdateResumeUrlRequestDto updateResumeUrlRequestDto);

  GetDdSepaFormStatusRequest toModel(GetDdSepaFormStatusRequestDto getDDSEPAFormStatusRequestDto);
}
