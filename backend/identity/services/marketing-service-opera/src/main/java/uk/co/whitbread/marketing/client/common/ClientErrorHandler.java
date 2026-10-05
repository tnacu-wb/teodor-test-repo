package uk.co.whitbread.marketing.client.common;

import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpStatusCodeException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.exception.CDHError;

public final class ClientErrorHandler {

    private ClientErrorHandler() {
    }

    public static CDHError getCdhError(HttpStatusCodeException e, JsonMapper objectMapper, Logger log) {
        try {
            if (ObjectUtils.isEmpty(e.getResponseBodyAsString())) {
                return new CDHError(e.getStatusCode().value(), e.getMessage());
            }
            return objectMapper.readValue(e.getResponseBodyAsString(), CDHError.class);
        } catch (JacksonException | IllegalArgumentException io) {
            log.debug("Response body doesn't match to CDHError.class: {}, Response body: {}", io.getMessage(), e.getResponseBodyAsString(), e);
            return new CDHError(e.getStatusCode().value(), e.getResponseBodyAsString());
        }
    }
}

