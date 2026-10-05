package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.co.whitbread.payments.Application;
import uk.co.whitbread.payments.service.TemplateService;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("opera-perf")
class StubTemplateServiceIT {

    @Autowired
    TemplateService templateService;

    @Test
    void verifyStubTemplateServiceLoads() {
        Assertions.assertTrue(templateService instanceof StubTemplateService);
    }

    @Test
    void verifyGetRedirectHtmlThrowsException() {
        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                templateService.getRedirectHtml("environment", Collections.emptyMap(),
                        "paymentId", "SUCCESS"));
    }

    @Test
    void verifyGetIPageHtmlReturnsMockedData() {
        var html = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <title>Payment Details</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <script type=\"text/javascript\">\n" +
            "        <!--\n" +
            "        try {\n" +
            "            parent.postMessage(\"{\\\"paymentId\\\": \\\"paymentId\\\", \\\"merchantReference\\\": \\\"\\\",\\\"transactionId\\\": \\\"\\\",\\\"authCode\\\": \\\"\\\",\\\"cardType\\\": \\\"\\\", \\\"tokenNo\\\": \\\"\\\", \\\"paymentStatus\\\": \\\"\\\"}\", \"*\");\n" +
            "        } catch (error) {\n" +
            "            if (console) {\n" +
            "                console.log(error);\n" +
            "            }\n" +
            "        }\n" +
            "        // -->\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
        Assertions.assertEquals(html, templateService.getIPageHtml("session", "language", "paymentId", LocalDate.now()));
    }

}
