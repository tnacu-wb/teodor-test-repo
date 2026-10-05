package uk.co.whitbread.hotel.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uk.co.whitbread.hotel.payment.model.ThreeDSecureVersion2Response;
import uk.co.whitbread.hotel.payment.properties.DomainUrlProperties;
import uk.co.whitbread.hotel.payment.service.utils.ParesUtils;
import uk.co.whitbread.hotel.payment.service.utils.XmlValidator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static uk.co.whitbread.hotel.payment.model.DatacashFormParameters.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private static final String PAYMENT_RESPONSE_TEMPLATE = "paymentResponse";
    private static final String THREE_D_SECURE_V2_RESPONSE_TEMPLATE = "3DSV2AuthenticationResponse";
    private static final String COMMENT_START = "<!--";
    private static final String COMMENT_END = "// -->";
    private static final String ANY_ORIGIN = "*";

    private final DomainUrlProperties domainUrlProperties;
    private final XmlValidator xmlValidator;
    private final ParesUtils paresUtils;
    private final TemplateEngine templateEngine;

    public String threeDSecureConfirmation(String paymentId, String paRes, String env) {
        boolean success = false;

        try {
            String decodedPares = paresUtils.decodePares(paRes);

            success = xmlValidator.validate(decodedPares);
        } catch (Exception e) {
            log.warn("Exception when decoding pares", e);
        }
        paRes = paRes.replaceAll("(\\r|\\n)", "");
        return generateResponseFromTemplate(paymentId, paRes, env, success);
    }

    /**
     * Contructs a 3DS V2 response object based on form data submitted by Datacash to redirectURl
     *
     * @param formData  the form data from Datacash
     * @param sessionId the sessionId for the booking/payment
     * @return a ThreeDSecureVersion2Response object
     */
    public ThreeDSecureVersion2Response constructThreeDSecureVersion2Response(MultiValueMap<String, String> formData, String sessionId) {
        ThreeDSecureVersion2Response threeDSecureVersion2Response = new ThreeDSecureVersion2Response(sessionId);
        if (formData == null) {
            threeDSecureVersion2Response.setSuccess(false);
        } else {
            threeDSecureVersion2Response.setDatacashReference(formData.getFirst(DATACASH_REFERENCE.getValue()));
            threeDSecureVersion2Response.setTransactionId(formData.getFirst(DATACASH_TRANSACTION_ID.getValue()));
            threeDSecureVersion2Response.setGatewayRecommendation(formData.getFirst(GATEWAY_RECOMMENDATION.getValue()));
            threeDSecureVersion2Response.setSuccess(StringUtils.hasText(formData.getFirst(GATEWAY_RESULT.getValue())) && "SUCCESS".equals(formData.getFirst(GATEWAY_RESULT.getValue())));
        }
        return threeDSecureVersion2Response;
    }

    public String threeDSecureV2Confirmation(ThreeDSecureVersion2Response threeDSecureVersion2Response, String env, String bookingChannel) {
        return generate3dsV2ResponseFromTemplate(threeDSecureVersion2Response, env, bookingChannel);
    }

    private String generate3dsV2ResponseFromTemplate(ThreeDSecureVersion2Response threeDSecureVersion2Response, String env, String bookingChannel) {
        final Context ctx = new Context();
        final String sanitizedEnv = sanitizeDomainUrl(env);
        ctx.setVariable("commentStart", COMMENT_START);
        ctx.setVariable("domainUrl", sanitizedEnv);
        ctx.setVariable("sessionId", threeDSecureVersion2Response.getSessionId());
        ctx.setVariable("datacashReference", threeDSecureVersion2Response.getDatacashReference());
        ctx.setVariable("gatewayRecommendation", threeDSecureVersion2Response.getGatewayRecommendation());
        ctx.setVariable("transactionId", threeDSecureVersion2Response.getTransactionId());
        ctx.setVariable("success", threeDSecureVersion2Response.isSuccess());
        ctx.setVariable("commentEnd", COMMENT_END);

        return templateEngine.process(THREE_D_SECURE_V2_RESPONSE_TEMPLATE, ctx);
    }

    private String generateResponseFromTemplate(String paymentId, String paRes, String env, boolean success) {
        final Context ctx = new Context();
        final String sanitizedEnv = sanitizeDomainUrl(env);
        ctx.setVariable("commentStart", COMMENT_START);
        ctx.setVariable("sessionId", paymentId);
        ctx.setVariable("valid", success);
        paRes = paRes.replaceAll("\\\\", "\\\\\\\\");
        ctx.setVariable("paRes", paRes);
        ctx.setVariable("domainUrl", sanitizedEnv);
        ctx.setVariable("commentEnd", COMMENT_END);

        return templateEngine.process(PAYMENT_RESPONSE_TEMPLATE, ctx);
    }

    private String sanitizeDomainUrl(String env) {
        if (Objects.isNull(env)) {
            return domainUrlProperties.getDefaultUrl();
        }
        // Apps need a special environment "*". This allows their webview to receive the message, as it doesn't have a URL.
        if (ANY_ORIGIN.equals(env)) {
            return env;
        }
        String domain;
        try {
            domain = (new URI(env)).getHost();
        } catch (URISyntaxException ue) {
            log.warn(String.format("Environment '%s' is not a valid URI", env), ue);
            return domainUrlProperties.getDefaultUrl();
        }
        if (Objects.isNull(domain) || domainUrlProperties
                .getRegex()
                .stream()
                .noneMatch(domain::matches)) {
            log.warn("Environment '{}' is not a valid environment", env);
            return domainUrlProperties.getDefaultUrl();
        }
        return env;
    }

}
