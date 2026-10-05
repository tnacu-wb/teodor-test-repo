package uk.co.whitbread.piba.account.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesRequest;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalances;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWord;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.piba.account.util.TestUtil.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "worldline.tetheringPlus.enabled=false")
class WorldlineAccountTransformerLegacyTest {
    
    @Autowired
    private WorldlineTransformer worldlineTransformer;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    void transformViewCurrentBalanceSoapRequestSuccessfully() {
        CustomerAccountCurrentBalancesRequest request= new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID, SCHEME_CUSTOMER_ID, null);
        CustomerAccountViewCurrentBalances balancesRequest = worldlineTransformer.toCustomerAccountCurrentBalancesRequest(request);
        assertNotNull(balancesRequest);
        assertEquals("{"+TETHERED_USER_GUID+"}", balancesRequest.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, balancesRequest.getRequest().getSchemeCustomerId());
    }

    @Test
    void transformTetheredUserDetailsGetSuccessfully() {
        TetheredUserDetailsGet tetheredUserDetailsGet = worldlineTransformer.toTetheredUserDetailsRequest(TETHERED_USER_GUID, null);
        assertNotNull(tetheredUserDetailsGet);
        assertEquals("{"+ TETHERED_USER_GUID +"}", tetheredUserDetailsGet.getRequest().getTetheredUserGuid());
    }

    @Test
    void transformResetMemorableWordRequestSuccessfully() {
        String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        UpdateMemorableWordRequest
            updateMemorableWordRequest =
            new UpdateMemorableWordRequest(SESSION_ID, SAMPLE_NONCE, timestamp, HASH,
                MEMORABLE_WORD, TETHERED_USER_GUID, null);
        UpdateMemorableWord request = worldlineTransformer.toUpdateMemorableWordRequest(updateMemorableWordRequest);
        assertNotNull(request);
        assertNotNull(request.getRequest());
        assertNotNull(request.getRequest().getSessionToken());
        assertEquals(SESSION_ID, request.getRequest().getSessionToken().getSessionId());
        assertEquals(SAMPLE_NONCE, request.getRequest().getSessionToken().getNonce());
        assertEquals(timestamp, request.getRequest().getSessionToken().getTimestamp());
        assertEquals(HASH, request.getRequest().getSessionToken().getHash());
        assertEquals(MEMORABLE_WORD, request.getRequest().getNewMemorableWord());
    }
}