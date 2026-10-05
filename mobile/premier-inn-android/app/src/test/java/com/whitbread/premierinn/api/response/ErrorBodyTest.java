package com.whitbread.premierinn.api.response;

import com.whitbread.premierinn.common.retrofitConfiguration.GsonAdapterFactory;
import com.whitbread.premierinn.data.GsonFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import okhttp3.ResponseBody;
import retrofit2.Response;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ErrorBodyTest {

    private static final int RETURNED_ERROR_CODE = 123;
    private static final String RETURNED_ERROR_MSG = "My error message";

    private static final String SERIALIZED_ERROR_BODY = "{\"code\": \""
            + RETURNED_ERROR_CODE + "\", \"details\": [\""
            + RETURNED_ERROR_MSG + "\"]}";

    @Test
    public void testErrorBodyDeserialization() {
        Response errorResponse = Response.error(400, ResponseBody.create(null, SERIALIZED_ERROR_BODY));

        ErrorBody createdErrorBody = ErrorBody.fromResponse(errorResponse, GsonFactory.create(GsonAdapterFactory.create()));

        assertEquals(RETURNED_ERROR_CODE, createdErrorBody.code());
        assertEquals(RETURNED_ERROR_MSG, createdErrorBody.errorMessage());
    }
}