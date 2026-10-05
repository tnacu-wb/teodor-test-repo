package uk.co.whitbread.hotel.payment.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.whitbread.hotel.payment.model.DatacashFormParameters.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ExtendWith(SpringExtension.class)
class HotelPaymentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @ParameterizedTest(name = "validateBin {0} should return cardType {1}")
    @CsvSource({
            "/payment/validations/444433,            VI",
            "/payment/validations/444433?hotelCode=LONTOW, VI",
            "/payment/validations/308950,            AT",
            "/payment/validations/635629,            BD"
    })
    void validateBin_shouldGetOkResponse(String url, String expectedCardType) {
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, jsonRequestEntity(), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsEntry("cardType", expectedCardType);
        assertThat(response.getBody()).containsEntry("startDateRequired", false);
    }

    @Test
    void validateBin_shouldHandleValidationError() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/payment/validations/123",
                HttpMethod.GET,
                jsonRequestEntity(),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("code", "001");
        assertThat(response.getBody().get("details"))
                .asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.LIST)
                .hasSize(1)
                .contains("bin length must 6 characters");
    }

    @Test
    void payment_authentication_complete_success_MOBILE() {
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add(DATACASH_REFERENCE.getValue(), "34334444");
        formParams.add(DATACASH_TRANSACTION_ID.getValue(), "1");
        formParams.add(GATEWAY_RESULT.getValue(), "SUCCESS");
        formParams.add(GATEWAY_RECOMMENDATION.getValue(), "PROCEED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment/authentication/mobileSuccess/complete?bookingChannel=MOBILE",
                request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("text/html");
        assertThat(response.getBody())
                .contains("\\\"datacashReference\\\":  \\\"34334444\\\"")
                .contains("\\\"transactionId\\\":  \\\"1\\\"")
                .contains("\\\"success\\\":  true")
                .contains("\\\"sessionId\\\": \\\"mobileSuccess\\\"")
                .contains("\\\"gatewayRecommendation\\\": \\\"PROCEED\\\"");
    }

    @Test
    void payment_authentication_complete_failure_MOBILE() {
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("bookingChannel", "MOBILE");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment/authentication/mobileFailure/complete",
                request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("text/html");
        assertThat(response.getBody())
                .contains("\\\"success\\\":  false")
                .contains("\\\"sessionId\\\": \\\"mobileFailure\\\"");
    }

    @Test
    void payment_authentication_complete_success_WEB() {
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add(DATACASH_REFERENCE.getValue(), "34334444");
        formParams.add(DATACASH_TRANSACTION_ID.getValue(), "1");
        formParams.add(GATEWAY_RESULT.getValue(), "SUCCESS");
        formParams.add(GATEWAY_RECOMMENDATION.getValue(), "PROCEED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment/authentication/sessionId/complete?bookingChannel=WEB&env=https://www.beta.premierinn.com",
                request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("text/html");
        assertThat(response.getBody())
                .contains("\\\"datacashReference\\\":  \\\"34334444\\\"")
                .contains("\\\"transactionId\\\":  \\\"1\\\"")
                .contains("\\\"success\\\":  true")
                .contains("\\\"sessionId\\\": \\\"sessionId\\\"")
                .contains("\\\"gatewayRecommendation\\\": \\\"PROCEED\\\"");
    }

    @Test
    void payment_authentication_complete_defaults_to_WEB() {
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add(DATACASH_REFERENCE.getValue(), "34334444");
        formParams.add(DATACASH_TRANSACTION_ID.getValue(), "1");
        formParams.add(GATEWAY_RESULT.getValue(), "SUCCESS");
        formParams.add(GATEWAY_RECOMMENDATION.getValue(), "PROCEED");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formParams, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/payment/authentication/sessionId/complete?env=https://www.beta.premierinn.com",
                request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).contains("text/html");
    }

    private HttpEntity<Void> jsonRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(headers);
    }
}