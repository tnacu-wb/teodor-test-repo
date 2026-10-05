package uk.co.whitbread.piba.registration.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationQuestion;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import uk.co.whitbread.piba.registration.model.Scheme;
import uk.co.whitbread.piba.registration.util.TestUtil;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticate;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponseType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticationQuestionType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationCodeInfoType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfo;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponseType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmit;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetherDetailsType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.piba.registration.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WorldlineRegistrationTransformerTest {

    @Autowired
    private WorldlineTransformer objectUnderTest;

    private final TestUtil testUtil = new TestUtil();

    private RegistrationInfoResponse registrationInfoResponse;
    private final RegistrationGetInfoResponse registrationGetInfoResponse = new RegistrationGetInfoResponse();

    @Test
    void transformRegistrationGetInfoSoapRequestSuccessfully() {
        RegistrationGetInfo registrationGetInfo = objectUnderTest.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE);
        assertNotNull(registrationGetInfo);
        assertEquals(REGISTRATION_CODE, registrationGetInfo.getRequest().getRegistrationCode());
    }

    @Test
    void transformRegistrationGetInfoSoapRequestSuccessfullyDeRequest() {
        RegistrationGetInfo registrationGetInfo = objectUnderTest.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE_DE);
        assertNotNull(registrationGetInfo);
        assertEquals(REGISTRATION_CODE_DE, registrationGetInfo.getRequest().getRegistrationCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeHeader().getClientMessageId(), registrationGetInfo.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeHeader().getCultureCode(), registrationGetInfo.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeCredentials().getUsername(), registrationGetInfo.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeCredentials().getPassword(), registrationGetInfo.getRequest().getTrustedPartnerCredentials().getPassword());
    }

    @Test
    void transformRegistrationGetInfoSoapRequestSuccessfullyGbRequest() {
        RegistrationGetInfo registrationGetInfo = objectUnderTest.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE_GB);
        assertNotNull(registrationGetInfo);
        assertEquals(REGISTRATION_CODE_GB, registrationGetInfo.getRequest().getRegistrationCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbHeader().getClientMessageId(), registrationGetInfo.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbHeader().getCultureCode(), registrationGetInfo.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbCredentials().getUsername(), registrationGetInfo.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbCredentials().getPassword(), registrationGetInfo.getRequest().getTrustedPartnerCredentials().getPassword());
    }

    @Test
    void transformRegistrationInfoResponseSuccessfully() {
        RegistrationGetInfoResponseType registrationGetInfoResponseType = new RegistrationGetInfoResponseType();
        registrationGetInfoResponseType.setRegistrationCodeInfo(testUtil.buildRegistrationCodeInfoType(true));
        registrationGetInfoResponse.setResponse(registrationGetInfoResponseType);
        registrationInfoResponse = objectUnderTest.toRegistrationInfoResponse(registrationGetInfoResponse);
        assertNotNull(registrationInfoResponse);
        assertNotNull(registrationInfoResponse.getRegistrationCodeInfo());
        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID, registrationInfoResponse.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, registrationInfoResponse.getRegistrationCodeInfo().getSchemeCustomerId());
        assertEquals(REGISTRATION_ROLE, registrationInfoResponse.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(REGISTRATION_CODE, registrationInfoResponse.getRegistrationCodeInfo().getRegistrationCode());

        assertEquals(1, registrationInfoResponse.getRegistrationCodeInfo().getAuthenticationQuestions().size());
        RegistrationAuthenticationQuestion question = registrationInfoResponse.getRegistrationCodeInfo().getAuthenticationQuestions().get(0);
        assertEquals(1, question.getQuestionId());
        assertEquals("Question", question.getQuestion());
    }

    @Test
    void transformRegistrationInfoResponseWithoutAuthQuest() {
        RegistrationGetInfoResponseType registrationGetInfoResponseType = new RegistrationGetInfoResponseType();
        registrationGetInfoResponseType.setRegistrationCodeInfo(testUtil.buildRegistrationCodeInfoType(false));
        registrationGetInfoResponse.setResponse(registrationGetInfoResponseType);
        registrationInfoResponse = objectUnderTest.toRegistrationInfoResponse(registrationGetInfoResponse);
        assertNotNull(registrationInfoResponse);
        assertNotNull(registrationInfoResponse.getRegistrationCodeInfo());
        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID, registrationInfoResponse.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, registrationInfoResponse.getRegistrationCodeInfo().getSchemeCustomerId());
        assertEquals(REGISTRATION_ROLE, registrationInfoResponse.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(REGISTRATION_CODE, registrationInfoResponse.getRegistrationCodeInfo().getRegistrationCode());

        assertTrue(registrationInfoResponse.getRegistrationCodeInfo().getAuthenticationQuestions().isEmpty());

    }

    @Test
    void transformRegistrationAuthenticateSoapRequestSuccessfully() {
        RegistrationAuthenticationRequest registrationAuthenticationRequest = testUtil.buildAuthenticationRequest(REGISTRATION_CODE);
        RegistrationAuthenticate registrationAuthenticate = objectUnderTest.toRegistrationAuthenticateSoapRequest(registrationAuthenticationRequest);
        assertNotNull(registrationAuthenticate);
        assertEquals(REGISTRATION_CODE, registrationAuthenticate.getRequest().getRegistrationCode());
        assertEquals(1, registrationAuthenticate.getRequest().getAuthenticationAnswers().size());
    }

    @Test
    void transformRegistrationAuthenticateSoapRequestSuccessfullyDE() {
        RegistrationAuthenticationRequest registrationAuthenticationRequest = testUtil.buildAuthenticationRequest(REGISTRATION_CODE_DE);
        RegistrationAuthenticate registrationAuthenticate = objectUnderTest.toRegistrationAuthenticateSoapRequest(registrationAuthenticationRequest);
        assertNotNull(registrationAuthenticate);
        assertEquals(REGISTRATION_CODE_DE, registrationAuthenticate.getRequest().getRegistrationCode());
        assertEquals(1, registrationAuthenticate.getRequest().getAuthenticationAnswers().size());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeHeader().getClientMessageId(), registrationAuthenticate.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeHeader().getCultureCode(), registrationAuthenticate.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeCredentials().getUsername(), registrationAuthenticate.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeCredentials().getPassword(), registrationAuthenticate.getRequest().getTrustedPartnerCredentials().getPassword());

    }

    @Test
    void transformRegistrationAuthenticateSoapRequestSuccessfullyGB() {
        RegistrationAuthenticationRequest registrationAuthenticationRequest = testUtil.buildAuthenticationRequest(REGISTRATION_CODE_GB);
        RegistrationAuthenticate registrationAuthenticate = objectUnderTest.toRegistrationAuthenticateSoapRequest(registrationAuthenticationRequest);
        assertNotNull(registrationAuthenticate);
        assertEquals(REGISTRATION_CODE_GB, registrationAuthenticate.getRequest().getRegistrationCode());
        assertEquals(1, registrationAuthenticate.getRequest().getAuthenticationAnswers().size());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbHeader().getClientMessageId(), registrationAuthenticate.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbHeader().getCultureCode(), registrationAuthenticate.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbCredentials().getUsername(), registrationAuthenticate.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbCredentials().getPassword(), registrationAuthenticate.getRequest().getTrustedPartnerCredentials().getPassword());
    }

    @Test
    void transformRegistrationAuthenticateResponseSuccessfully() {
        RegistrationAuthenticateResponse registrationAuthenticateResponse = new RegistrationAuthenticateResponse();
        RegistrationAuthenticateResponseType response = new RegistrationAuthenticateResponseType();
        RegistrationCodeInfoType registrationCodeInfoType = new RegistrationCodeInfoType();
        registrationCodeInfoType.setPrimarySchemeCustomerId(PRIMARY_SCHEME_CUSTOMER_ID);
        registrationCodeInfoType.setRegistrationCode(REGISTRATION_CODE);
        registrationCodeInfoType.setRegistrationRole(REGISTRATION_ROLE);
        registrationCodeInfoType.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        RegistrationAuthenticationQuestionType registrationAuthenticationQuestionType = new RegistrationAuthenticationQuestionType();
        registrationAuthenticationQuestionType.setQuestionId(1);
        registrationAuthenticationQuestionType.setQuestion("Question");
        registrationCodeInfoType.getAuthenticationQuestions().add(registrationAuthenticationQuestionType);
        response.setRegistrationCodeInfo(registrationCodeInfoType);

        response.setPrepopulatedItems(testUtil.buildPrePopulatedItemsType());

        registrationAuthenticateResponse.setResponse(response);

        RegistrationAuthenticationResponse registrationAuthenticationResponse = objectUnderTest.toRegistrationAuthResponse(registrationAuthenticateResponse);
        assertNotNull(registrationAuthenticationResponse);
        assertNotNull(registrationAuthenticationResponse.getRegistrationCodeInfo());
        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID, registrationAuthenticationResponse.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, registrationAuthenticationResponse.getRegistrationCodeInfo().getSchemeCustomerId());
        assertEquals(REGISTRATION_ROLE, registrationAuthenticationResponse.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(REGISTRATION_CODE, registrationAuthenticationResponse.getRegistrationCodeInfo().getRegistrationCode());
        assertEquals("aa@bb.cc", registrationAuthenticationResponse.getRegistrationPrePopulatedItems().getEmailAddress());
        assertEquals("Forename", registrationAuthenticationResponse.getRegistrationPrePopulatedItems().getForename());
        assertEquals(1, registrationAuthenticationResponse.getRegistrationCodeInfo().getAuthenticationQuestions().size());
    }

    @Test
    void transformRegistrationSubmitSoapRequestSuccessfully() {
        RegistrationSubmitRequest registrationSubmitRequest = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE);

        RegistrationSubmit registrationSubmit = objectUnderTest.toRegistrationSubmitSoapRequest(registrationSubmitRequest);
        assertNotNull(registrationSubmit);
        assertEquals(REGISTRATION_CODE, registrationSubmit.getRequest().getRegistrationCode());
        assertEquals("aa@bb.cc", registrationSubmit.getRequest().getRegistrationDetails().getEmailAddress());
        assertEquals("Forename", registrationSubmit.getRequest().getRegistrationDetails().getForename());
        assertEquals("MemorableWord", registrationSubmit.getRequest().getRegistrationDetails().getMemorableWord());
    }

    @Test
    void transformRegistrationSubmitSoapRequestSuccessfullyDE() {
        RegistrationSubmitRequest registrationSubmitRequest = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE_DE);

        RegistrationSubmit registrationSubmit = objectUnderTest.toRegistrationSubmitSoapRequest(registrationSubmitRequest);
        assertNotNull(registrationSubmit);
        assertEquals(REGISTRATION_CODE_DE, registrationSubmit.getRequest().getRegistrationCode());
        assertEquals("aa@bb.cc", registrationSubmit.getRequest().getRegistrationDetails().getEmailAddress());
        assertEquals("Forename", registrationSubmit.getRequest().getRegistrationDetails().getForename());
        assertEquals("MemorableWord", registrationSubmit.getRequest().getRegistrationDetails().getMemorableWord());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeHeader().getClientMessageId(), registrationSubmit.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeHeader().getCultureCode(), registrationSubmit.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeCredentials().getUsername(), registrationSubmit.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getDeCredentials().getPassword(), registrationSubmit.getRequest().getTrustedPartnerCredentials().getPassword());

    }

    @Test
    void transformRegistrationSubmitSoapRequestSuccessfullyGB() {
        RegistrationSubmitRequest registrationSubmitRequest = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE_GB);

        RegistrationSubmit registrationSubmit = objectUnderTest.toRegistrationSubmitSoapRequest(registrationSubmitRequest);
        assertNotNull(registrationSubmit);
        assertEquals(REGISTRATION_CODE_GB, registrationSubmit.getRequest().getRegistrationCode());
        assertEquals("aa@bb.cc", registrationSubmit.getRequest().getRegistrationDetails().getEmailAddress());
        assertEquals("Forename", registrationSubmit.getRequest().getRegistrationDetails().getForename());
        assertEquals("MemorableWord", registrationSubmit.getRequest().getRegistrationDetails().getMemorableWord());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbHeader().getClientMessageId(), registrationSubmit.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbHeader().getCultureCode(), registrationSubmit.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbCredentials().getUsername(), registrationSubmit.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineRegistrationTransformer) objectUnderTest).getGbCredentials().getPassword(), registrationSubmit.getRequest().getTrustedPartnerCredentials().getPassword());

    }

    @Test
    void transformRegistrationSubmitResponseSuccessfully() {
        RegistrationSubmitResponse registrationSubmitResponse = new RegistrationSubmitResponse();
        RegistrationSubmitResponseType response = new RegistrationSubmitResponseType();
        response.setRegistrationCodeInfo(testUtil.buildRegistrationCodeInfoType(true));
        TetherDetailsType tetherDetailsType = new TetherDetailsType();
        tetherDetailsType.setTetheredUserGuid(TETHERED_USER_GUID);
        response.setTetherDetails(tetherDetailsType);

        registrationSubmitResponse.setResponse(response);

        RegistrationSubmitResp registrationSubmitResp = objectUnderTest.toRegistrationSubmitResponse(registrationSubmitResponse);
        assertNotNull(registrationSubmitResp);
        assertNotNull(registrationSubmitResp.getRegistrationCodeInfo());
        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID, registrationSubmitResp.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, registrationSubmitResp.getRegistrationCodeInfo().getSchemeCustomerId());
        assertEquals(REGISTRATION_ROLE, registrationSubmitResp.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(REGISTRATION_CODE, registrationSubmitResp.getRegistrationCodeInfo().getRegistrationCode());
        assertEquals(TETHERED_USER_GUID, registrationSubmitResp.getTetherDetails().getTetheredUserGuid());
        assertEquals(1, registrationSubmitResp.getRegistrationCodeInfo().getAuthenticationQuestions().size());
    }

    @Test
    void shouldHaveUniqueClientMessageId_whenTransformSubmitSoapRequest_isRunTwice() {
        RegistrationSubmitRequest requestGb = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE_GB);
        RegistrationSubmitRequest requestDe = testUtil.buildRegistrationSubmitRequest(REGISTRATION_CODE_DE);

        RegistrationSubmit firstCall = objectUnderTest.toRegistrationSubmitSoapRequest(requestGb);
        RegistrationSubmit secondCall = objectUnderTest.toRegistrationSubmitSoapRequest(requestDe);
        assertNotEquals(firstCall.getRequest().getHeader().getClientMessageId(),
                secondCall.getRequest().getHeader().getClientMessageId());
    }

    @Test
    void shouldHaveUniqueClientMessageId_whenTransformAuthenticateRequest_isRunTwice() {
        RegistrationAuthenticationRequest requestGb = testUtil.buildAuthenticationRequest(REGISTRATION_CODE_GB);
        RegistrationAuthenticationRequest requestDe = testUtil.buildAuthenticationRequest(REGISTRATION_CODE_DE);

        RegistrationAuthenticate firstCall = objectUnderTest.toRegistrationAuthenticateSoapRequest(requestGb);
        RegistrationAuthenticate secondCall = objectUnderTest.toRegistrationAuthenticateSoapRequest(requestDe);
        assertNotEquals(firstCall.getRequest().getHeader().getClientMessageId(),
                secondCall.getRequest().getHeader().getClientMessageId());
    }

    @Test
    void extractDESchemeIsSuccessful() {
        Scheme scheme = objectUnderTest.extractScheme(REGISTRATION_CODE_DE);
        assertEquals(Scheme.DE, scheme);
    }

    @Test
    void extractGBSchemeIsSuccessful() {
        Scheme scheme = objectUnderTest.extractScheme(REGISTRATION_CODE_GB);
        assertEquals(Scheme.GB, scheme);
    }

}
