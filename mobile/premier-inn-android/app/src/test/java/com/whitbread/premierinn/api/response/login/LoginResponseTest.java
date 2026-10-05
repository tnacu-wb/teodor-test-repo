package com.whitbread.premierinn.api.response.login;

import com.whitbread.premierinn.api.response.InstanceFactory;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class LoginResponseTest {

    @Test
    public void loginResponse_typeAdapter_success() throws Exception {

        LoginResponse loginResponse = InstanceFactory.create(LoginResponse.class, "apiTest/login-success.json");

        assertNotNull(loginResponse);

        assertNotNull(loginResponse.sessionId());
    }
}