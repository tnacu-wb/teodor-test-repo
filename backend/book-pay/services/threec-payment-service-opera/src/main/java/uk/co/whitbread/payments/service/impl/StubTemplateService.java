package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import uk.co.whitbread.payments.service.TemplateService;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "3c.iPage", name = "stubMode", havingValue = "true")
public class StubTemplateService implements TemplateService {

    private final SpringWebFluxTemplateEngine templateEngine;

    private static final String REDIRECT_TEMPLATE = "paymentResponse";
    private static final String COMMENT_START = "<!--";
    private static final String COMMENT_END = "// -->";

    @Override
    public String getRedirectHtml(String environment, Map<String, String> queryParams,
                                  String paymentId, String paymentStatus) {
        throw new UnsupportedOperationException();
    }

    /**
     * Stub implementation returns back the final redirect HTML instead of the usual iPage.
     */
    @Override
    public String getIPageHtml(String session, String language, String paymentId, LocalDate departureDate) {
        final Context ctx = new Context();
        ctx.setVariable("domainUrl", "*");
        ctx.setVariable("paymentId", paymentId);
        ctx.setVariable("commentStart", COMMENT_START);
        ctx.setVariable("commentEnd", COMMENT_END);
        var result = templateEngine.process(REDIRECT_TEMPLATE, ctx);
        log.info("Successfully generated redirect HTML for payment id {}.", paymentId);
        return result;
    }
}
