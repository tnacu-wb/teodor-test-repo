package uk.co.whitbread.marketing.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.exception.HotelAccountClientError;
import uk.co.whitbread.marketing.exception.HotelAccountClientException;

import java.io.ByteArrayOutputStream;

@Slf4j
@RequiredArgsConstructor
public class HotelAccountServiceErrorDecoder implements ErrorDecoder {
    private static final String AUTH_TOKEN_ERROR_CODE = "3003";

    private final JsonMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        final HotelAccountClientError errorResponse = extractContent(response, methodKey);
        return new HotelAccountClientException(response.status(),
                errorResponse.getDetails() != null ?
                        errorResponse.getDetails()[0] : null, AUTH_TOKEN_ERROR_CODE);
    }

    private HotelAccountClientError extractContent(Response response, String methodKey) {
        if (response != null) {
            int status = response.status();
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                StreamUtils.copy(response.body().asInputStream(), output);
                final HotelAccountClientError oauthClientError = objectMapper.readValue(output.toByteArray(), HotelAccountClientError.class);
                oauthClientError.setStatus(status);
                log.error("Error while trying to connect to client via feign. Response: {}, methodKey: {}", oauthClientError, methodKey);
                return oauthClientError;
            } catch (JsonParseException | JsonMappingException e) {
                String errorMessage = String.format("Error parsing Exception coming from service %s", methodKey);
                log.error(errorMessage, e);
                return new HotelAccountClientError(status, AUTH_TOKEN_ERROR_CODE, new String[]{errorMessage});
            } catch (Exception ex) {
                String errorMessage = String.format("Error while trying to extract feign client response content %s", methodKey);
                log.error(errorMessage, ex);
                return new HotelAccountClientError(status, AUTH_TOKEN_ERROR_CODE, new String[]{errorMessage});
            }
        }
        return new HotelAccountClientError(500, AUTH_TOKEN_ERROR_CODE, new String[]{"Unknown error"});
    }
}
