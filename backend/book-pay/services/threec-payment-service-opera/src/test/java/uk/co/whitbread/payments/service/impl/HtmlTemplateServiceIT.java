package uk.co.whitbread.payments.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.whitbread.payments.Application;
import uk.co.whitbread.payments.service.TemplateService;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HtmlTemplateServiceIT {

    @Autowired
    TemplateService templateService;

    @Test
    void verifyStubTemplateServiceLoads() {
        Assertions.assertTrue(templateService instanceof HtmlTemplateService);
    }

    @Test
    void verifyGetRedirectHtml() {
        var html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <title>Payment Details</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <script type=\"text/javascript\">\n" +
                "        <!--\n" +
                "        try {\n" +
                "            parent.postMessage(\"{\\\"paymentId\\\": \\\"paymentId\\\", \\\"merchantReference\\\": \\\"\\\",\\\"transactionId\\\": \\\"\\\",\\\"authCode\\\": \\\"\\\",\\\"cardType\\\": \\\"\\\", \\\"tokenNo\\\": \\\"\\\", \\\"paymentStatus\\\": \\\"SUCCESS\\\"}\", \"https://secure2.beta.premierinn.digital/\");\n" +
                "        } catch (error) {\n" +
                "            if (console) {\n" +
                "                console.log(error);\n" +
                "            }\n" +
                "        }\n" +
                "        // -->\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
        Assertions.assertEquals(html,
                templateService.getRedirectHtml("environment", Collections.emptyMap(),
                        "paymentId", "SUCCESS"));
    }

    @Test
    void verifyGetIPageHtmlReturnsMockedData() {
        var date = LocalDate.now();
        var html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <title></title>\n" +
                "</head>\n" +
                "<body style=\"position: absolute; top: 50%; transform: translateY(-50%); width: 100%;\">\n" +
                "<div style=\"margin: 0 auto;width: 80px;\">\n" +
                "    <svg width='80px' height='80px' xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"\n" +
                "         preserveAspectRatio=\"xMidYMid\" class=\"uil-spin\">\n" +
                "        <rect x=\"0\" y=\"0\" width=\"100\" height=\"100\" fill=\"none\" class=\"bk\"></rect>\n" +
                "        <g transform=\"translate(50 50)\">\n" +
                "            <g transform=\"rotate(0) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0s\" dur=\"1s\"\n" +
                "                                      repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(45) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.12s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.12s\"\n" +
                "                                      dur=\"1s\" repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(90) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.25s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.25s\"\n" +
                "                                      dur=\"1s\" repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(135) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.37s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.37s\"\n" +
                "                                      dur=\"1s\" repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(180) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.5s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.5s\" dur=\"1s\"\n" +
                "                                      repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(225) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.62s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.62s\"\n" +
                "                                      dur=\"1s\" repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(270) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.75s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.75s\"\n" +
                "                                      dur=\"1s\" repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "            <g transform=\"rotate(315) translate(34 0)\">\n" +
                "                <circle cx=\"0\" cy=\"0\" r=\"8\" fill=\"#3c868b\">\n" +
                "                    <animate attributeName=\"opacity\" from=\"1\" to=\"0.1\" begin=\"0.87s\" dur=\"1s\"\n" +
                "                             repeatCount=\"indefinite\"></animate>\n" +
                "                    <animateTransform attributeName=\"transform\" type=\"scale\" from=\"1.4\" to=\"1\" begin=\"0.87s\"\n" +
                "                                      dur=\"1s\" repeatCount=\"indefinite\"></animateTransform>\n" +
                "                </circle>\n" +
                "            </g>\n" +
                "        </g>\n" +
                "    </svg>\n" +
                "</div>\n" +
                "<h1 style=\"text-align: center;\" class=\"wb-heading--h3\">Loading...</h1>\n" +
                "<form name=\"Form1\" id=\"Form1\" method=\"post\" action=\"http://localhost:8080/iPage/Service/_2006_05_v1_0_1/service.aspx\">\n" +
                "    <input type=\"hidden\" name=\"XXX_IPGSESSION_XXX\" value=\"session\">\n" +
                "    <input type=\"hidden\" id=\"content_language\" name=\"content_language\" value=\"language\">\n" +
                "    <input type=\"hidden\" id=\"merchant_script_data_1\" name=\"merchant_script_data_1\" value=\"{&quot;language&quot;:&quot;language&quot;,&quot;departureDate&quot;:&quot;" + date + "&quot;}\">\n" +
                "    <script>document.getElementById(\"Form1\").submit();</script>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n";
        Assertions.assertEquals(html, templateService.getIPageHtml("session", "language", "paymentId", date));
    }

}
