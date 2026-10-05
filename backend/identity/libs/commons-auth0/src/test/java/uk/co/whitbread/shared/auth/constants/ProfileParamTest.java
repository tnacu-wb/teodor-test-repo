package uk.co.whitbread.shared.auth.constants;

import org.junit.Assert;
import org.junit.Test;

public class ProfileParamTest {

    @Test
    public void shouldReturnEmail() {
        ProfileParam target = ProfileParam.EMAIL;
        Assert.assertEquals(target.getParam(),"name");
    }

    @Test
    public void shouldReturnSessionId() {
        ProfileParam target = ProfileParam.SESSION_ID;
        Assert.assertEquals(target.getParam(),"sessionId");
    }
}