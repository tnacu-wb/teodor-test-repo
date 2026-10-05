package uk.co.whitbread.payments.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import uk.co.whitbread.payments.properties.RedirectProperties;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.service.TemplateService;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "3c.iPage", name = "stubMode", havingValue = "false", matchIfMissing = true)
public class HtmlTemplateService implements TemplateService {

    private final SpringWebFluxTemplateEngine templateEngine;
    private final RedirectProperties redirectProperties;
    private final ThreeCProperties threeCProperties;

    private static final String REDIRECT_TEMPLATE = "paymentResponse";
    private static final String I_PAGE_TEMPLATE = "iPage";
    private static final String COMMENT_START = "<!--";
    private static final String COMMENT_END = "// -->";

    public String getRedirectHtml(String environment, Map<String, String> queryParams,
                                  String paymentId, String paymentStatus) {
        final String sanitizedEnv = sanitizeDomainUrl(environment);
        final Context ctx = new Context();
        String merchantReference = queryParams.get("MerchantRef");
        String transactionId = queryParams.get("TxID");
        String authCode = queryParams.get("AuthorisationCode");
        String cardType = queryParams.get("CardType");
        String tokenNo = queryParams.get("TokenNo");
        ctx.setVariable("domainUrl", sanitizedEnv);
        ctx.setVariable("paymentId", paymentId);
        ctx.setVariable("paymentStatus", paymentStatus);
        ctx.setVariable("merchantReference", merchantReference);
        ctx.setVariable("transactionId", transactionId);
        ctx.setVariable("authCode", authCode);
        ctx.setVariable("cardType", cardType);
        ctx.setVariable("tokenNo", tokenNo);
        ctx.setVariable("commentStart", COMMENT_START);
        ctx.setVariable("commentEnd", COMMENT_END);
        var result = templateEngine.process(REDIRECT_TEMPLATE, ctx);
        log.info("Successfully generated redirect HTML for payment id {}.", paymentId);
        return result;
    }

    public String getIPageHtml(String session, String language, String paymentId, LocalDate departureDate) {
        final Context ctx = new Context();
        Map<String, String> map = new HashMap();
        map.put("language", language);
        map.put("departureDate", !isNull(departureDate) ? departureDate.toString() : "" );
        ctx.setVariable("providerUrl", threeCProperties.getIPage().getProviderUrl());
        ctx.setVariable("session", session);
        ctx.setVariable("language", language);
        try {
            ctx.setVariable("merchantData", new ObjectMapper().writeValueAsString(map));
        } catch (JsonProcessingException e) {
            log.error("Error processing map into json ", e.getMessage());
        }
        var result = templateEngine.process(I_PAGE_TEMPLATE, ctx);
        log.info("Successfully generated iPage HTML for payment id {}.", paymentId);
        return result;
    }

    String sanitizeDomainUrl(String env) {
        String domain;
        try {
            domain = (new URI(env)).getHost();
        } catch (Exception e) {
            log.warn(String.format("Environment '%s' is not a valid URI", env), e);
            return redirectProperties.getDefaultUrl();
        }
        if (isNull(domain) || redirectProperties
                .getRegex()
                .stream()
                .noneMatch(domain::matches)) {
            log.warn("Environment '{}' is not a valid environment", env);
            return redirectProperties.getDefaultUrl();
        }
        return env;
    }
}
