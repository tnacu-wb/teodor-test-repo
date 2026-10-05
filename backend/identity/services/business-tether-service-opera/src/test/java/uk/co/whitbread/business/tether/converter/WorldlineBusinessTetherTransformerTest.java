package uk.co.whitbread.business.tether.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumber;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumber;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class WorldlineBusinessTetherTransformerTest {
    private static final String MEMORABLE_WORD = "word";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String ACCOUNT_NUMBER1 = "accountNumber1";
    private static final String CARD_NUMBER = "cardNumber";
    private static final String LINK_CODE = "linkCode";

    private static final String USERNAME = "gbtest";
    private static final String CULTURE_CODE = "en-GB";
    private static final String CLIENT_MESSAGE_ID = "{a6173aca-ea66-48e6-8ce2-4e04f1d4b628}";
    private static final String TETHERED_GUID = "TetheredGuid";

    @Autowired
    private WorldlineTransformer sut;

    @MockitoBean(answers = RETURNS_DEEP_STUBS)
    private ObjectMapper objectMapper;

    @Test
    public void shouldTransformToTetherByAccountSoapRequest() {

        TetherByAccountNumber tetherByAccountNumber
                = sut.toTetherByAccountRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.GB);

        assertThat(tetherByAccountNumber.getRequest())
                .hasFieldOrPropertyWithValue("newMemorableWord", MEMORABLE_WORD)
                .hasFieldOrPropertyWithValue("accountNumber", ACCOUNT_NUMBER)
                .hasFieldOrPropertyWithValue("linkCode", LINK_CODE);
    }

    @Test
    public void shouldTransformToTetherByCardSoapRequest() {

        TetherByCardNumber tetherByCardNumber
                = sut.toTetherByCardRequest(buildTetherLinkRequest(CARD_NUMBER), Scheme.GB);

        assertThat(tetherByCardNumber.getRequest())
                .hasFieldOrPropertyWithValue("newMemorableWord", MEMORABLE_WORD)
                .hasFieldOrPropertyWithValue("cardNumber", CARD_NUMBER)
                .hasFieldOrPropertyWithValue("linkCode", LINK_CODE);
    }

    @Test
    public void shouldTransformToLoginTetheredUserSoapRequest() {

        LoginTetheredUser loginTetheredUser = sut.toLoginTetheredUserRequest(TETHERED_GUID, Scheme.GB);

        assertThat(loginTetheredUser.getRequest())
                .hasFieldOrPropertyWithValue("tetheredUserGuid", "{" + TETHERED_GUID + "}")
                .hasFieldOrPropertyWithValue("trustedPartnerCredentials.username", USERNAME)
                .hasFieldOrPropertyWithValue("header.cultureCode", CULTURE_CODE);
                //.hasFieldOrPropertyWithValue("header.clientMessageId", CLIENT_MESSAGE_ID);
    }

    @Test
    public void transformTetherByAccountRequestSuccessfullyGB() {
        //when
        TetherByAccountNumber tetherByAccountNumber = sut.toTetherByAccountRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.GB);
        //then
        assertNotNull(tetherByAccountNumber);
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbHeader().getClientMessageId(), tetherByAccountNumber.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbHeader().getCultureCode(), tetherByAccountNumber.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbCredentials().getUsername(), tetherByAccountNumber.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbCredentials().getPassword(), tetherByAccountNumber.getRequest().getTrustedPartnerCredentials().getPassword());
        assertFalse(tetherByAccountNumber.getRequest().getHeader().getClientMessageId().isEmpty());
    }

    @Test
    public void transformTetherByAccountRequestSuccessfullyDE() {
        //when
        TetherByAccountNumber tetherByAccountNumber = sut.toTetherByAccountRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.DE);
        //then
        assertNotNull(tetherByAccountNumber);
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeHeader().getClientMessageId(), tetherByAccountNumber.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeHeader().getCultureCode(), tetherByAccountNumber.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeCredentials().getUsername(), tetherByAccountNumber.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeCredentials().getPassword(), tetherByAccountNumber.getRequest().getTrustedPartnerCredentials().getPassword());
        assertFalse(tetherByAccountNumber.getRequest().getHeader().getClientMessageId().isEmpty());
    }

    @Test
    public void transformTetherByCardRequestSuccessfullyGB() {
        //when
        TetherByCardNumber tetherByCardNumber = sut.toTetherByCardRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.GB);
        //then
        assertNotNull(tetherByCardNumber);
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbHeader().getClientMessageId(), tetherByCardNumber.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbHeader().getCultureCode(), tetherByCardNumber.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbCredentials().getUsername(), tetherByCardNumber.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getGbCredentials().getPassword(), tetherByCardNumber.getRequest().getTrustedPartnerCredentials().getPassword());
        assertFalse(tetherByCardNumber.getRequest().getHeader().getClientMessageId().isEmpty());
    }

    @Test
    public void transformTetherByCardRequestSuccessfullyDE() {
        //when
        TetherByCardNumber tetherByCardNumber = sut.toTetherByCardRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.DE);
        //then
        assertNotNull(tetherByCardNumber);
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeHeader().getClientMessageId(), tetherByCardNumber.getRequest().getHeader().getClientMessageId());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeHeader().getCultureCode(), tetherByCardNumber.getRequest().getHeader().getCultureCode());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeCredentials().getUsername(), tetherByCardNumber.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineBusinessTetherTransformer) sut).getDeCredentials().getPassword(), tetherByCardNumber.getRequest().getTrustedPartnerCredentials().getPassword());
        assertFalse(tetherByCardNumber.getRequest().getHeader().getClientMessageId().isEmpty());
    }

    @Test
    public void transformTetherByCardRequestsWithUniqueClientMessageId() {
        //when
        TetherByCardNumber tetherByCardNumber1 = sut.toTetherByCardRequest(buildTetherLinkRequest(ACCOUNT_NUMBER), Scheme.DE);
        TetherByCardNumber tetherByCardNumber2 = sut.toTetherByCardRequest(buildTetherLinkRequest(ACCOUNT_NUMBER1), Scheme.GB);
        //then;
        assertNotEquals(tetherByCardNumber1.getRequest().getHeader().getClientMessageId(),
                tetherByCardNumber2.getRequest().getHeader().getClientMessageId());
    }

    private TetherLinkRequest buildTetherLinkRequest(String linkId) {
        TetherLinkRequest tetherLinkRequest = new TetherLinkRequest();
        tetherLinkRequest.setMemorableWord(MEMORABLE_WORD);
        tetherLinkRequest.setLinkCode(LINK_CODE);
        tetherLinkRequest.setLinkId(linkId);

        return tetherLinkRequest;
    }
}
