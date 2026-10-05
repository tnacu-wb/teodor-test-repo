package uk.co.whitbread.piba.registration.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.api.converter.WorldlineRequestTransformer;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.registration.mapper.RegistrationMapper;
import uk.co.whitbread.piba.registration.model.PibaTetheredGuidRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.model.RegistrationDetails;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import uk.co.whitbread.piba.registration.model.Scheme;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticate;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateRequestType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticationAnswerType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfo;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoRequestType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmit;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitRequestType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(name = "worldline.tetheringPlus.enabled", havingValue = "false", matchIfMissing = true)
public class WorldlineRegistrationTransformerLegacy extends WorldlineRequestTransformer implements WorldlineTransformer {
    private final RegistrationMapper mapper;

    public WorldlineRegistrationTransformerLegacy(WorldLineProperties worldLineProperties, RegistrationMapper mapper) {
        super(worldLineProperties);
        this.mapper = mapper;
    }

    @Override
    public RegistrationGetInfo toRegistrationGetInfoSoapRequest(String registrationCode) {
        RegistrationGetInfoRequestType request = new RegistrationGetInfoRequestType();
        request.setHeader(getHeader());
        request.setTrustedPartnerCredentials(getCredentials());
        request.setRegistrationCode(registrationCode);

        RegistrationGetInfo registrationGetInfo = new RegistrationGetInfo();
        registrationGetInfo.setRequest(request);

        return registrationGetInfo;
    }

    @Override
    public RegistrationInfoResponse toRegistrationInfoResponse(RegistrationGetInfoResponse registrationGetInfoResponse) {
        return mapper.toRegistrationInfoResponse(registrationGetInfoResponse);
    }

    @Override
    public RegistrationAuthenticate toRegistrationAuthenticateSoapRequest(RegistrationAuthenticationRequest registrationAuthenticationRequest) {
        RegistrationAuthenticateRequestType request = new RegistrationAuthenticateRequestType();
        request.setHeader(getHeader());
        request.setTrustedPartnerCredentials(getCredentials());
        request.setRegistrationCode(registrationAuthenticationRequest.getRegistrationCode());

        if (registrationAuthenticationRequest.getAuthenticationAnswers() != null) {
            List<RegistrationAuthenticationAnswerType> registrationAuthenticationAnswers = registrationAuthenticationRequest.getAuthenticationAnswers().stream().map(
                    authQuestion -> {
                        RegistrationAuthenticationAnswerType registrationAuthenticationAnswerType = new RegistrationAuthenticationAnswerType();
                        registrationAuthenticationAnswerType.setAnswer(authQuestion.getAnswer());
                        registrationAuthenticationAnswerType.setQuestionId(authQuestion.getQuestionId());
                        return registrationAuthenticationAnswerType;
                    }
            ).collect(Collectors.toList());
            request.getAuthenticationAnswers().addAll(registrationAuthenticationAnswers);
        }
        RegistrationAuthenticate registrationAuthenticate = new RegistrationAuthenticate();
        registrationAuthenticate.setRequest(request);

        return registrationAuthenticate;
    }

    @Override
    public RegistrationAuthenticationResponse toRegistrationAuthResponse(RegistrationAuthenticateResponse registrationAuthenticateResponse) {
        return mapper.toRegistrationAuthenticationResponse(registrationAuthenticateResponse);
    }

    @Override
    public RegistrationSubmit toRegistrationSubmitSoapRequest(RegistrationSubmitRequest registrationSubmitRequest) {
        RegistrationSubmitRequestType request = new RegistrationSubmitRequestType();
        request.setHeader(getHeader());
        request.setTrustedPartnerCredentials(getCredentials());
        request.setRegistrationCode(registrationSubmitRequest.getRegistrationCode());

        if (registrationSubmitRequest.getAuthenticationAnswers() != null) {
            List<RegistrationAuthenticationAnswerType> registrationAuthenticationAnswers = registrationSubmitRequest.getAuthenticationAnswers().stream().map(
                    authQuestion -> {
                        RegistrationAuthenticationAnswerType registrationAuthenticationAnswerType = new RegistrationAuthenticationAnswerType();
                        registrationAuthenticationAnswerType.setAnswer(authQuestion.getAnswer());
                        registrationAuthenticationAnswerType.setQuestionId(authQuestion.getQuestionId());
                        return registrationAuthenticationAnswerType;
                    }
            ).collect(Collectors.toList());
            request.getAuthenticationAnswers().addAll(registrationAuthenticationAnswers);
        }
        RegistrationDetailsType registrationDetailsType = populateRegistrationDetailsType(registrationSubmitRequest.getRegistrationDetails());
        request.setRegistrationDetails(registrationDetailsType);

        RegistrationSubmit registrationSubmit = new RegistrationSubmit();
        registrationSubmit.setRequest(request);

        return registrationSubmit;
    }

    @Override
    public RegistrationSubmitResp toRegistrationSubmitResponse(RegistrationSubmitResponse registrationSubmitResponse) {
        return mapper.toRegistrationSubmitResp(registrationSubmitResponse);
    }

    @Override
    public PibaTetheredGuidRequest toPibaTetheredGuidRequest(String tetherGuid, EmployeeDetails employeeDetails, Scheme scheme) {
        return new PibaTetheredGuidRequest(employeeDetails.getCompanyId(),
                employeeDetails.getEmployeeId(), tetherGuid, scheme);
    }

    @Override
    public Scheme extractScheme(String registrationCode) {
        return Scheme.GB;
    }

    private RegistrationDetailsType populateRegistrationDetailsType(RegistrationDetails registrationDetails) {
        return mapper.toRegistrationDetailsType(registrationDetails);
    }

}
