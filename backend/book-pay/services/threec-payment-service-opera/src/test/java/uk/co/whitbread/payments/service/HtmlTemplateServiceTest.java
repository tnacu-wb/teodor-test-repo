package uk.co.whitbread.payments.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import uk.co.whitbread.payments.properties.RedirectProperties;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.service.impl.HtmlTemplateService;
import uk.co.whitbread.payments.util.PaymentIdGenerator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HtmlTemplateServiceTest {

    @Mock
    private RedirectProperties redirectProperties;

    @Mock
    private ThreeCProperties threeCProperties;

    private SpringWebFluxTemplateEngine templateEngine;

    private HtmlTemplateService htmlTemplateService;

    @Mock
    private PaymentIdGenerator paymentIdGenerator;

    @BeforeEach
    public void setUp() {
        templateEngine = new SpringWebFluxTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        htmlTemplateService = new HtmlTemplateService(templateEngine, redirectProperties, threeCProperties);
    }

    @Test
    void testHandleRedirectReturnsDefaultUrl() {
        when(redirectProperties.getDefaultUrl()).thenReturn("https://www.default.com");
        when(redirectProperties.getRegex()).thenReturn(List.of(".*\\.premierinn\\.com"));
        var queryParams = new HashMap<String, String>() {{
            put("MerchantRef", "1234");
            put("TxID", "4321");
        }};

        String html = htmlTemplateService.getRedirectHtml("https://www.wrong.com", queryParams,
                "1234567890D", "SUCCESS");
        assertNotNull(html);
        assertTrue(html.contains("https://www.default.com"));
    }

    @Test
    void verifyIPageTemplate() {
        when(threeCProperties.getIPage()).thenReturn(mock(ThreeCProperties.IPage.class));
        when(threeCProperties.getIPage().getProviderUrl()).thenReturn("providerUrl");
        String html = htmlTemplateService.getIPageHtml("session", "en", "paymentId", LocalDate.now());
        assertNotNull(html);
        assertTrue(html.contains("session"));
        assertTrue(html.contains("providerUrl"));
        assertTrue(html.contains("&quot;language&quot;:&quot;en&quot;"));
        assertTrue(html.contains(LocalDate.now().toString()));
    }

    private ITemplateResolver templateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);
        return templateResolver;
    }

}