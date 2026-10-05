package uk.co.whitbread.marketing.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.exception.OauthClientError;
import uk.co.whitbread.marketing.exception.OauthClientException;

import java.io.ByteArrayOutputStream;

@Slf4j
@RequiredArgsConstructor
public class OauthServiceErrorDecoder implements ErrorDecoder {
    private static final String AUTH_TOKEN_ERROR_CODE = "3002";

    private final JsonMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        final OauthClientError errorResponse = extractContent(response, methodKey);
        return new OauthClientException(response.status(), errorResponse.getError(), AUTH_TOKEN_ERROR_CODE);
    }

    private OauthClientError extractContent(Response response, String methodKey) {
        if (response != null) {
            int status = response.status();
            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                if(response.body() != null) {
                StreamUtils.copy(response.body().asInputStream(), output);
                }
                OauthClientError oauthClientError = new OauthClientError();
                if (response.body() != null){
                    oauthClientError = objectMapper.readValue(output.toByteArray(), OauthClientError.class);

                }
                oauthClientError.setStatus(status);
                log.error("Error while trying to connect to client via feign. Response: {}, methodKey: {}", oauthClientError, methodKey);
                return oauthClientError;
            } catch (JacksonException | IllegalArgumentException e) {
                String errorMessage = String.format("Error parsing Exception coming from service %s", methodKey);
                log.error(errorMessage, e);
                return new OauthClientError(status, errorMessage);
            } catch (Exception ex) {
                String errorMessage = String.format("Error while trying to extract feign client response content %s", methodKey);
                log.error(errorMessage, ex);
                return new OauthClientError(status, errorMessage);
            }
        }
        return new OauthClientError(500, "Unknown error");
    }
}
