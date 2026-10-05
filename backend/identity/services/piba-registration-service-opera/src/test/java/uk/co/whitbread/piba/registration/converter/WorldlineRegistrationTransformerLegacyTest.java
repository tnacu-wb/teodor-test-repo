package uk.co.whitbread.piba.registration.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.Scheme;
import uk.co.whitbread.piba.registration.util.TestUtil;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticate;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfo;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_CODE;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "worldline.tetheringPlus.enabled=false")
class WorldlineRegistrationTransformerLegacyTest {

    @Autowired
    private WorldlineTransformer objectUnderTest;

    private final TestUtil testUtil = new TestUtil();

    @Test
    void transformRegistrationGetInfoSoapRequestSuccessfully() {
        RegistrationGetInfo registrationGetInfo = objectUnderTest.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE);
        assertNotNull(registrationGetInfo);
        assertEquals(REGISTRATION_CODE, registrationGetInfo.getRequest().getRegistrationCode());
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
    void extractSchemeReturnsDefaultScheme() {
        Scheme scheme = objectUnderTest.extractScheme(REGISTRATION_CODE);
        assertEquals(Scheme.GB, scheme);
    }
}
