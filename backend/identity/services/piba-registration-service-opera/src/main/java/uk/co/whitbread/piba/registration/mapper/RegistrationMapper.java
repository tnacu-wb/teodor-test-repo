package uk.co.whitbread.piba.registration.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.model.RegistrationDetails;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

  String TITLE_WL_DOCTOR = "Doctor";
  String TITLE_DOCTOR = "Dr";

  @Mapping(target = "registrationCodeInfo", source = "response.registrationCodeInfo")
  RegistrationInfoResponse toRegistrationInfoResponse(RegistrationGetInfoResponse sharedDataRequestResponse);

  @Mapping(target = "registrationCodeInfo", source = "response.registrationCodeInfo")
  @Mapping(target = "registrationPrePopulatedItems", source = "response.prepopulatedItems")
  @BeanMapping(qualifiedByName = "setTitle")
  RegistrationAuthenticationResponse toRegistrationAuthenticationResponse(RegistrationAuthenticateResponse registrationAuthenticateResponse);

  @Named("setTitle")
  @AfterMapping
  default void setTitle(@MappingTarget RegistrationAuthenticationResponse registrationAuthenticationResponse, RegistrationAuthenticateResponse registrationAuthenticateResponse) {
    String titleWlDoctor = "Doctor";
    String titleDoctor = "Dr";
    String registrationTitle = registrationAuthenticateResponse.getResponse().getPrepopulatedItems().getTitle();

    if (titleWlDoctor.equals(registrationTitle)) {
      registrationAuthenticationResponse.getRegistrationPrePopulatedItems().setTitle(titleDoctor);
    } else {
      registrationAuthenticationResponse.getRegistrationPrePopulatedItems().setTitle(registrationTitle);
    }
  }

  @Mapping(target = "registrationCodeInfo", source = "response.registrationCodeInfo")
  @Mapping(target = "tetherDetails", source = "response.tetherDetails")
  RegistrationSubmitResp toRegistrationSubmitResp(RegistrationSubmitResponse registrationSubmitResponse);

  RegistrationDetailsType toRegistrationDetailsType(RegistrationDetails registrationDetails);
}
