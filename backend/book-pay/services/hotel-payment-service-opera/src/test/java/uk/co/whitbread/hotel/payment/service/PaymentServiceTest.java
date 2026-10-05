package uk.co.whitbread.hotel.payment.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import uk.co.whitbread.hotel.payment.model.ThreeDSecureVersion2Response;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.hotel.payment.model.DatacashFormParameters.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PaymentServiceTest {

    public static final String PAYMENT_ID = "PAYMENT_ID";
    public static final String PARES = "eJzVWFuTokzSvudXTMxeEu9wVnyjpyOKM2ghRxHuQJCDiCJH/fUfdk93O/P17s7OXq0RRktWZlY+\n" +
            "mU9lVoM8OdklSQQ72XWX5Bn58uUJJk0TpsmXPP7+9Rx+m+PkbE7MyK/3xWnZAFbS/Lr4rcnTKol/\n" +
            "6ExafXJp8lP1THzDv5FP2Nvj2zJMLrssrNo3wSQKdzWn6s80zjLUZPHj8WP9mFxU4ZmhcZqaERRN\n" +
            "EgSOT3qv4je/2K+On4zuLmiSB09jHj9DAQyPX/0GrnqxG/VC/f6E3TU+9OOwTZ5JnGDxOTH/Qsz+\n" +
            "pom/SfwJe5F/qJ3vO4HjqZt2J+7RPWGPog/FKdOXpNpdn1ly9oS9P30oJOP5VCWTzYTv/fc7xF8R\n" +
            "PZ3D6hl/+EybE9Pe4Ue6ne2D9zY//n84iyfsRf6h1rRh2zXP/hP249cDgLDvnwEAHOdD9bCBvnn/\n" +
            "prkJXj9TBl9UHhDt8mecmdBMf3/2A8r0dMnb7HgH+7PgHfFb+BP2O/l+PNgT5abALsmX8VhWzfev\n" +
            "Wdue/8awYRi+DdS30yXFyCkbGL7AJoV4Yug/Pvhpv/BVrfanP7Ce7PmwOlX5LizzW9hOzIZJm53i\n" +
            "L+/Bf+bPse4uCcwS+b8mn3/tCLr66y7BKYL5ij14f4f2O25/DfPShH81WUj85NFK9smdZskX11K/\n" +
            "f/3Hvzi7L/pCniZN+yfb/7r1u7NNWHbJ86w4yYy/A4WyUq+oZvKSL8nXuGevd9o8an4Ej71H/8GJ\n" +
            "jwL+VNOXrL3ay2N4uTjWNdtkUeIaVS14CmTn27mVerZ0Hs9E0elaV8gkFIuxYztvVWK3Lc10+8Ww\n" +
            "xhdIxQSBjS+NBCw3Vk+xQs7ypZ0ks55MrrR+2ForwfKAEzk8fQzjQNumOJ4QM9mmg7hwBoRUKDTw\n" +
            "NtE8m/trfxF1xNoVA/qQCeshkPG25Z2Ly6Y9R8Pvr4gewn9DtUyujygnyZbBF0LYhr+I+OTS5vuJ\n" +
            "k1NXgqoqpA7Pg65OwaByIFU51QE6lx7q7JDLiwHngOlKQOC59CauIDjIgHBFLoP8xm1GRHSAwaX6\n" +
            "ZrJ0eEkvIspqfI85QEsdROALG9NUxKGUArns/GpzDch0lBzgvNqcHEQQrSyWJcHf6idVHPFAEB3I\n" +
            "iS+78CNcbuRNEcpMH8mLKnHEDHLsyxoYoW/JEo74rp7tyDR1ZYmJvE0XKzA1cTGNc/oKOXorOICG\n" +
            "gniFN/EGBUjAzWmSqXfZuBbEG/JDOCSf4PsdeMi/wvc78JAHfAHkdm/4zDu+N3imqzuq4new4Lnq\n" +
            "9nOFEGCKEgBrrjDBkPoHPvUn+5uynvkCkI0DRpzm9jnCjRZytT6wAsPo/XB1CoZC0Y4fcYRRmHaQ\n" +
            "5nN0s+ir3laanR/LMw3sb2W+4Aw6scNcjTxUmp0gufalDRmjNdVbyuycQPcQIUZKcBhqsEt3QbHX\n" +
            "sIrL8awv0XpGGSSVBreE2IauG5+x1onHY3PZEcxE+lDuN0qYkiyPgCmfIFzzwDwNaSrKPyoRi4PJ\n" +
            "QwCGFfA1Xw1U4Ee0mYo6x7Ge4i6GwGMcFx9SxCI3XbDVpmxvDqqol7vKOgfHsvC3Vgk5/MWZkJoe\n" +
            "x5miySVsmSX8Ga5N81S27b4xEVKXh8rhuXqY6mrhBafKBVTlNOWsRgjgpVwW8SAxlVPZTrzqbSHB\n" +
            "/UMoc9nS5lKHwxBoNgNvvpBCFgdtY99EC4JXuvIcVF1S72N5zKKjOYoFMF85snN4d3MIPSZH4q1W\n" +
            "hYK4gRx8NcogfOSAo1gT8+Agvm6yEoezG3j6KToumoiMGQRa+CAPL4sTuy1xwp6ZpHQLec6OyAU+\n" +
            "5YV7IdFpOuqfHHPkTqLU5MbV4BxXbTyN9CLljXJZR0FIoSlL5/lJLBL+qkVHRyd6yW5ve/oGa6B6\n" +
            "guUEiDGviWXGX+uuPvvzNdGhOkniDnR7tJqtuYm7sY4q/BZnCKh5ybmYUtIvhehQ65vZnmWReuXz\n" +
            "HU01ioW7ET9XAgKtCNmTgo1Ddf2+LPEKx9z8MpFrpGZWY4DvU0/8ta8hn3a6hTB1uvb63uk+SwEQ\n" +
            "OOyzQiL/SSU/KyTyn1Tys0Iin1ayAIk04MNamBpGAUd4Azdd0MNJdn2ROem7DJnYLPw3JEX+Gbbf\n" +
            "hYa8YLPFQXnHlj2S1PS3ZTudaHx35QTT5shVoQ5pwA6C6WvLU6AiWb/Tp243HWIgpD4OJkgaOMkc\n" +
            "0M4BlVzzZemGx6RglIbcrBNHuSVYN8/446U4B/jssEN0cmUX6z3qqnBPs6NKMGMyX+WBJHaHWjbd\n" +
            "Fo+MHZDagXH71cpb9/w6bAe93Ul2xx4AiwS2h8YESVdGZqQpGRJ+nyxMLNu18Z6ldgdyaXAy3SXs\n" +
            "noMbY+nNsjNJm/7+SsjnY6vNEV7UCJBCDgC5SPeHId37g2K+9Jz1dFMWJXnQD9fOrXUKbI/xOtZ0\n" +
            "vFuo+tzKrQnuDbmXRLGhmIbNkIY5kOT1balBP/fwrgeOVK0k53rOZS/ArJMvi2dV5qDCjvwNaHdb\n" +
            "hAO+A8rNClriILxWQhWHzPEnYkWy1E1Dyp7ie52EI9QszzpHR43YHeMTtNxhmngvRktxGB/LB2N5\n" +
            "Is0NWK+kaRy+tEqfzLqIHPuAOoyKA6IfEUBHehyNktZHlPk+uNP0AMb9PJfWhHEewWuUFu4AU8E4\n" +
            "MEUwjQsOu9271ZQLk+XAnhUnp9P48N/J8oMr0gtX0mlZoNIU96Y+ZNnICu/X1ZGHBqPGjCPWM3dP\n" +
            "+6aXzz0C8mO+iTDTpUl5J7YjOxLzRYDjAbFkSsGpd7MVsUVWx/kxg8teGoFAs+dtYhdW4o5akeFi\n" +
            "W3cw36q9qde+vA3aYdZwWIPrHSEt0BbWO87OELo8b4wt6PCeuOVzMWt6XjiPYaHfiK51zB2FWdoY\n" +
            "DGYBUFnN9y11OTrOEj2NFyqP9rqPxIfFzktRrXfGWtnOmzMmWQJaLjgh6IL0IEKo1WC2SZ1LSwYl\n" +
            "zcjqqkvxYeBnqMRS6hURqVWL9kWxyOp9sFLjmbDYe42970/WQVgRK42uqduWH5eEl9CqeSKJ4mjb\n" +
            "kb+82is2uzVIvVzleGYouMX+bhMWk9vUhMn1WxPWANyidOa0zmxnQoDLvF3LthpRwsspd8H/GHM/\n" +
            "uXLeoPRx5UTud84fwmH/B1dO5N9dqf/dlRP55UodQ655u3K6j53cPS76mH9oxKmY3yuEPJZompkA\n" +
            "0ConDOCusAT3q4UpHP2m4giM0cnyRjPVNFI2N29rqEW9NyNkG21zfnXb4bbnG6wvREa+XKR4Jh12\n" +
            "awnV9etM3NFld2bJvbS2o3lyDhaoHy7UQ2XIwsxFKN2cCxe5V4zN3EzT2bK0uGUCCDKTLzXkAomh\n" +
            "Iz47znnl4GiJDlESW3XeLaIJIVzKvI9YZc0utPq0L5RjLc9q1b44q5CeEW4S4kc6h6qk0/PGdzbB\n" +
            "VcLdNY2txMXJyWpye1mMGYPcfG0ih4Xt8MR2yq69NChGD8ZWsxOjLU4LvbmVLrtkaJhQkrxoVwZK\n" +
            "e/OtUi+2o4E6LZLIEaUtVHpmzT1mGzh6aQnWDBeXC4KuKzKN1ho/u5CK1gmUXxcH8kbJt+Nt8zY6\n" +
            "kM9nxz8fBffZ8Tg6kD+ZHY8HEPmTE/h4AJE/OYGPBxD5k9nxODqQ354dSwCJwCKNNTcstBjl+4pp\n" +
            "AFcgyvQf59wTVIcVTizDb2yMGaZ+H9nKZekqrFyEcyUm0ByPG1RK0dLcY30y7Gl3TqKqpJ6ReH10\n" +
            "g1XeDMouaYeilHwfU2gJaoYdS5dLs3OnC5yhYmh2wfrapvEFZXbbfGuZtQtrukLyAmxNvz9RpaeM\n" +
            "gyYGVL7p1LjTxJAg8OqYF9zcQOWuTLVNdXRSpdaJxMwlRS2m0SnFCLFMjYjd+l6nRUQXaQ555dU+\n" +
            "n1ca7a1TfMqpIS/xY4yXM18+3cZiO0NN5ujm2JFYorSKGIfL4Ek6t1idZiVQRE5jpBat9kDlZEpW\n" +
            "IV7lxo7SFapVO7WDHi7UMefw+2Hl5azifjo6sF/fXjxhP73heHgP8vI2GvvxOvpu+vOr6v8DHdsQ\n" +
            "NA==";
    public static final String ENV = "ENV";

    @Autowired
    private PaymentService paymentService;

    @Test
    public void testThreeDSecureConfirmation() throws Exception {
        File expectedResponse = new File("src/test/resources/response/ThreeDSecureConfirmationResponse.html");
        String result = paymentService.threeDSecureConfirmation(PAYMENT_ID, PARES, ENV);
        assertEquals(FileUtils.readFileToString(expectedResponse, "utf-8").replaceAll("\\s+", ""),
                result.replaceAll("\\s+", ""));
    }

    @Test
    public void testThreeDSecureV2Confirmation() throws Exception {
        File expectedResponse = new File("src/test/resources/response/ThreeDSecureV2ConfirmationResponse.html");
        ThreeDSecureVersion2Response threeDSecureVersion2Response = new ThreeDSecureVersion2Response(PAYMENT_ID);
        threeDSecureVersion2Response.setSuccess(true);
        threeDSecureVersion2Response.setGatewayRecommendation("PROCEED");
        threeDSecureVersion2Response.setTransactionId("1");
        threeDSecureVersion2Response.setDatacashReference("1234");
        threeDSecureVersion2Response.setResult("SUCCESS");
        String result = paymentService.threeDSecureV2Confirmation(threeDSecureVersion2Response, "https://beta.premierinn.com", "WEB");
        assertEquals(FileUtils.readFileToString(expectedResponse, "utf-8").replaceAll("\\s+", ""),
                result.replaceAll("\\s+", ""));
    }

    @Test
    public void testThreeDSecureV2ConfirmationDefaultEnv() throws Exception {
        File expectedResponse = new File("src/test/resources/response/ThreeDSecureV2ConfirmationResponseDefaultEnv.html");
        ThreeDSecureVersion2Response threeDSecureVersion2Response = new ThreeDSecureVersion2Response(PAYMENT_ID);
        threeDSecureVersion2Response.setSuccess(true);
        threeDSecureVersion2Response.setGatewayRecommendation("PROCEED");
        threeDSecureVersion2Response.setTransactionId("1");
        threeDSecureVersion2Response.setDatacashReference("1234");
        threeDSecureVersion2Response.setResult("SUCCESS");
        String result = paymentService.threeDSecureV2Confirmation(threeDSecureVersion2Response, null, "WEB");
        assertEquals(FileUtils.readFileToString(expectedResponse, "utf-8").replaceAll("\\s+", ""),
                result.replaceAll("\\s+", ""));
    }

    @Test
    public void testThreeDSecureV2ConfirmationMobile() throws Exception {
        File expectedResponse = new File("src/test/resources/response/ThreeDSecureV2ConfirmationResponseMobile.html");
        ThreeDSecureVersion2Response threeDSecureVersion2Response = new ThreeDSecureVersion2Response(PAYMENT_ID);
        threeDSecureVersion2Response.setSuccess(true);
        threeDSecureVersion2Response.setGatewayRecommendation("PROCEED");
        threeDSecureVersion2Response.setTransactionId("1");
        threeDSecureVersion2Response.setDatacashReference("1234");
        threeDSecureVersion2Response.setResult("SUCCESS");
        String result = paymentService.threeDSecureV2Confirmation(threeDSecureVersion2Response, "*", "MOBILE");
        assertEquals(FileUtils.readFileToString(expectedResponse, "utf-8").replaceAll("\\s+", ""),
                result.replaceAll("\\s+", ""));
    }

    @Test
    public void testThreeDSecureV2ConfirmationMobileBB() throws Exception {
        File expectedResponse = new File("src/test/resources/response/ThreeDSecureV2ConfirmationResponseMobile.html");
        ThreeDSecureVersion2Response threeDSecureVersion2Response = new ThreeDSecureVersion2Response(PAYMENT_ID);
        threeDSecureVersion2Response.setSuccess(true);
        threeDSecureVersion2Response.setGatewayRecommendation("PROCEED");
        threeDSecureVersion2Response.setTransactionId("1");
        threeDSecureVersion2Response.setDatacashReference("1234");
        threeDSecureVersion2Response.setResult("SUCCESS");
        String result = paymentService.threeDSecureV2Confirmation(threeDSecureVersion2Response, "*", "CBT");
        assertEquals(FileUtils.readFileToString(expectedResponse, "utf-8").replaceAll("\\s+", ""),
                result.replaceAll("\\s+", ""));
    }

    @Test
    public void testConstructThreeDSecureVersion2Response() throws Exception {
        LinkedMultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add(DATACASH_REFERENCE.getValue(), "1234");
        multiValueMap.add(DATACASH_TRANSACTION_ID.getValue(), "1");
        multiValueMap.add(GATEWAY_RECOMMENDATION.getValue(), "PROCEED");
        multiValueMap.add(GATEWAY_RESULT.getValue(), "SUCCESS");

        ThreeDSecureVersion2Response threeDSecureVersion2Response = paymentService.constructThreeDSecureVersion2Response(multiValueMap, PAYMENT_ID);
        assertEquals("1234", threeDSecureVersion2Response.getDatacashReference());
        assertEquals("1", threeDSecureVersion2Response.getTransactionId());
        assertEquals("PROCEED", threeDSecureVersion2Response.getGatewayRecommendation());
        assertEquals(PAYMENT_ID, threeDSecureVersion2Response.getSessionId());
        assertTrue(threeDSecureVersion2Response.isSuccess());
    }

    @Test
    public void testConstructThreeDSecureVersion2ResponseResultUnsuccessful() throws Exception {
        LinkedMultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add(GATEWAY_RESULT.getValue(), "FAILURE");
        ThreeDSecureVersion2Response threeDSecureVersion2Response = paymentService.constructThreeDSecureVersion2Response(multiValueMap, PAYMENT_ID);
        assertEquals(PAYMENT_ID, threeDSecureVersion2Response.getSessionId());
        assertFalse(threeDSecureVersion2Response.isSuccess());
    }

}
