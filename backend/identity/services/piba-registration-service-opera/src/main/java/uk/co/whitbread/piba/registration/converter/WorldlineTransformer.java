package uk.co.whitbread.piba.registration.converter;

import uk.co.whitbread.piba.registration.model.PibaTetheredGuidRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import uk.co.whitbread.piba.registration.model.Scheme;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticate;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfo;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmit;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;

public interface WorldlineTransformer {

    RegistrationGetInfo toRegistrationGetInfoSoapRequest(String registrationCode);

    RegistrationInfoResponse toRegistrationInfoResponse(RegistrationGetInfoResponse registrationGetInfoResponse);

    RegistrationAuthenticate toRegistrationAuthenticateSoapRequest(RegistrationAuthenticationRequest registrationAuthenticationRequest);

    RegistrationAuthenticationResponse toRegistrationAuthResponse(RegistrationAuthenticateResponse registrationAuthenticateResponse);

    RegistrationSubmit toRegistrationSubmitSoapRequest(RegistrationSubmitRequest registrationSubmitRequest);

    RegistrationSubmitResp toRegistrationSubmitResponse(RegistrationSubmitResponse registrationSubmitResponse);

    PibaTetheredGuidRequest toPibaTetheredGuidRequest(String tetherGuid, EmployeeDetails employeeDetails, Scheme scheme);

    Scheme extractScheme(String registrationCode);

}
