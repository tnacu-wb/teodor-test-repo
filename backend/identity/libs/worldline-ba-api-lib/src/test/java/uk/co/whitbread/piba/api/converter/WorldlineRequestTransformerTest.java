package uk.co.whitbread.piba.api.converter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.whitbread.piba.api.TestApplication;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes={TestApplication.class},
    properties = {"spring.config.import=optional:configserver:"})
class WorldlineRequestTransformerTest {

    private static final String CLIENT_MESSAGE_ID= "{a6173aca-ea66-48e6-8ce2-4e04f1d4b628}";
    private static final String CULTURE_CODE= "en-GB";
    private static final String USER_NAME="test";

    @Autowired
    private WorldlineRequestTransformer objectUnderTest;

    @Test
    void testSetFieldSuccessfully(){
        assertEquals(CLIENT_MESSAGE_ID, objectUnderTest.getHeader().getClientMessageId());
        assertEquals(CULTURE_CODE, objectUnderTest.getHeader().getCultureCode());
        assertEquals(USER_NAME, objectUnderTest.getCredentials().getUsername());
    }
}
