package uk.co.whitbread.business.tether.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumber;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "worldline.tetheringPlus.enabled=false")
public class WorldlineBusinessTetherTransformerLegacyTest {
    private static final String MEMORABLE_WORD = "word";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String LINK_CODE = "linkCode";

    private static final String USERNAME = "gbtest";
    private static final String CULTURE_CODE = "en-GB";
    private static final String TETHERED_GUID = "5c746bf3-5c4d-4e07-a7ec-68ed807ebc66";
    private static final String EMPLOYEE_ID = "1";
    private static final String COMPANY_ID = "3467";

    @Autowired
    private WorldlineTransformer sut;

    @MockitoBean(answers = RETURNS_DEEP_STUBS)
    private ObjectMapper objectMapper;

    @Test
    void shouldTransformToTetherByAccountSoapRequest() {

        TetherByAccountNumber tetherByAccountNumber
                = sut.toTetherByAccountRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.GB);

        assertThat(tetherByAccountNumber.getRequest())
                .hasFieldOrPropertyWithValue("newMemorableWord", MEMORABLE_WORD)
                .hasFieldOrPropertyWithValue("accountNumber", ACCOUNT_NUMBER)
                .hasFieldOrPropertyWithValue("linkCode", LINK_CODE);
    }

    @Test
    void shouldTransformToTetherByCardSoapRequest() {

        TetherByCardNumber tetherByCardNumber
                = sut.toTetherByCardRequest(buildTetherLinkRequest(CARD_NUMBER), Scheme.GB);

        assertThat(tetherByCardNumber.getRequest())
                .hasFieldOrPropertyWithValue("newMemorableWord", MEMORABLE_WORD)
                .hasFieldOrPropertyWithValue("cardNumber", CARD_NUMBER)
                .hasFieldOrPropertyWithValue("linkCode", LINK_CODE);
    }

    @Test
    void shouldTransformToLoginTetheredUserSoapRequest() {

        LoginTetheredUser loginTetheredUser = sut.toLoginTetheredUserRequest(TETHERED_GUID, Scheme.GB);

        assertThat(loginTetheredUser.getRequest())
                .hasFieldOrPropertyWithValue("tetheredUserGuid", "{" + TETHERED_GUID + "}")
                .hasFieldOrPropertyWithValue("trustedPartnerCredentials.username", USERNAME)
                .hasFieldOrPropertyWithValue("header.cultureCode", CULTURE_CODE);
    }

    @Test
    void transformTetherByAccountRequestSuccessfully() {
        //when
        TetherByAccountNumber tetherByAccountNumber = sut.toTetherByAccountRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.GB);
        //then
        assertNotNull(tetherByAccountNumber);
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getHeader().getClientMessageId(), tetherByAccountNumber.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getHeader().getCultureCode(), tetherByAccountNumber.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getCredentials().getUsername(), tetherByAccountNumber.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getCredentials().getPassword(), tetherByAccountNumber.getRequest().getTrustedPartnerCredentials().getPassword());
        assertFalse(tetherByAccountNumber.getRequest().getHeader().getClientMessageId().isEmpty());
    }

    @Test
    void transformTetherByCardRequestSuccessfully() {
        //when
        TetherByCardNumber tetherByCardNumber = sut.toTetherByCardRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.GB);
        //then
        assertNotNull(tetherByCardNumber);
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getHeader().getClientMessageId(), tetherByCardNumber.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getHeader().getCultureCode(), tetherByCardNumber.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getCredentials().getUsername(), tetherByCardNumber.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineBusinessTetherTransformerLegacy) sut).getCredentials().getPassword(), tetherByCardNumber.getRequest().getTrustedPartnerCredentials().getPassword());
        assertFalse(tetherByCardNumber.getRequest().getHeader().getClientMessageId().isEmpty());
    }

    private TetherLinkRequest buildTetherLinkRequest(String linkId) {
        TetherLinkRequest tetherLinkRequest = new TetherLinkRequest();
        tetherLinkRequest.setMemorableWord(MEMORABLE_WORD);
        tetherLinkRequest.setLinkCode(LINK_CODE);
        tetherLinkRequest.setLinkId(linkId);

        return tetherLinkRequest;
    }
}
