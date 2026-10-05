package uk.co.whitbread.piba.registration.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
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
import uk.co.whitbread.piba.registration.service.RegistrationCodeParser;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
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
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Component
@ConditionalOnProperty(name = "worldline.tetheringPlus.enabled", havingValue = "true")
public class WorldlineRegistrationTransformer extends WorldlineRequestTransformer implements WorldlineTransformer {
    private final RegistrationMapper mapper;
    private final RegistrationCodeParser parser;

    public WorldlineRegistrationTransformer(WorldLineProperties worldLineProperties,
                                            RegistrationMapper mapper,
                                            RegistrationCodeParser parser) {
        super(worldLineProperties);
        this.mapper = mapper;
        this.parser = parser;
    }

    @Override
    public RegistrationGetInfo toRegistrationGetInfoSoapRequest(String registrationCode) {
        RegistrationGetInfoRequestType request = new RegistrationGetInfoRequestType();
        Scheme scheme = extractScheme(registrationCode);
        log.info("Inside toRegistrationGetInfoSoapRequest  and scheme- {}", scheme.name());
        log.info("getWorldLineRequestHeader(scheme) - {}", getWorldLineRequestHeader(scheme));
        log.info("user Name {} and password - {}", getWorldLineCredentialsType(scheme).getUsername(), getWorldLineCredentialsType(scheme).getPassword());
        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
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
        String registrationCode = registrationAuthenticationRequest.getRegistrationCode();
        Scheme scheme = extractScheme(registrationCode);
        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        request.setRegistrationCode(registrationCode);

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
        String registrationCode = registrationSubmitRequest.getRegistrationCode();
        Scheme scheme = extractScheme(registrationCode);
        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        request.setRegistrationCode(registrationCode);

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


    private RegistrationDetailsType populateRegistrationDetailsType(RegistrationDetails registrationDetails) {
        return mapper.toRegistrationDetailsType(registrationDetails);
    }

    @Override
    public PibaTetheredGuidRequest toPibaTetheredGuidRequest(String tetherGuid, EmployeeDetails employeeDetails, Scheme scheme) {
        return new PibaTetheredGuidRequest(employeeDetails.getCompanyId(),
            employeeDetails.getEmployeeId(), tetherGuid, scheme);
    }

    @Override
    public Scheme extractScheme(String registrationCode) {
        Scheme scheme = EnumUtils.getEnum(Scheme.class, parser.parseRegistrationCode(registrationCode));
        log.info("Scheme in extractScheme - {}", scheme);
        return isNull(scheme) ? Scheme.GB : scheme;
    }

    private HeaderType getWorldLineRequestHeader(Scheme scheme) {
        HeaderType header = Scheme.DE.equals(scheme) ? getDeHeader() : getGbHeader();
        header.setClientMessageId(String.valueOf(UUID.randomUUID()));
        log.debug("Adding ClientMessageId={}", header.getClientMessageId());
        return header;
    }

    private TrustedPartnerCredentialsType getWorldLineCredentialsType(Scheme scheme) {
        return Scheme.DE.equals(scheme) ? getDeCredentials() : getGbCredentials();
    }
}
