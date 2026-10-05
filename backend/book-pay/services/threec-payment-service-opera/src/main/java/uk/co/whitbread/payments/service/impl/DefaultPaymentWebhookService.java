package uk.co.whitbread.payments.service.impl;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.PaymentWebhookService;
import uk.co.whitbread.payments.util.ThreeCResponseUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultPaymentWebhookService implements PaymentWebhookService {
    private static final String PAYMENT_ID = "paymentId";
    private final PaymentRepository paymentRepository;

    @Override
    public Mono<PaymentResponse> handleWebhook(ServerRequest serverRequest) {
        String paymentId = serverRequest.pathVariable(PAYMENT_ID);
        return serverRequest.formData()
                .map(MultiValueMap::toSingleValueMap)
                .map(Map::entrySet)
                .doOnSuccess(formData -> log.info("Incoming webhook for payment resource with id {}.", paymentId))
                .map(ThreeCResponseUtil::addFormParamsToJsonNode)
                .map(objectNode -> ProviderResponse
                        .builder()
                        .providerReference(objectNode.get("TxID").asText())
                        .transactionReference(objectNode.get("ref").asText())
                        .threeCResponse(ThreeCResponse.builder()
                                .authCode(objectNode.get("AuthorisationCode").asText())
                                .binRange(objectNode.get("CardNumberFirst6").asText())
                                .last4Digits(objectNode.get("card_pan_last4digits").asText())
                                .scaReference(getStringFromJsonObjectNode(objectNode, "SCATransRef").trim())
                                .token(getStringFromJsonObjectNode(objectNode, "TokenNo"))
                                .threeDSIndicator(getStringFromJsonObjectNode(objectNode, "3DSIndicator"))
                                .cardSchemeId(objectNode.get("CardType").asText())
                                .cardSchemeName(objectNode.get("CardTypeName").asText())
                                .expiry(getExpiryDate(objectNode))
                                .cardholderFirstName(Base64.getEncoder().encodeToString(getStringFromJsonObjectNode(objectNode,"FirstName").getBytes(StandardCharsets.UTF_8)))
                                .cardholderLastName(Base64.getEncoder().encodeToString(getStringFromJsonObjectNode(objectNode,"LastName").getBytes(StandardCharsets.UTF_8)))
                                .providerStatus(objectNode.get("TxState").asText())
                                .providerResult(objectNode.get("ReturnCode").asText())
                                .fraudCheckDecision(getStringFromJsonObjectNode(objectNode,"fraud_check_decision"))
                                .fraudCheckResult(getStringFromJsonObjectNode(objectNode,"fraud_check_result"))
                                .fraudCheckResultReason(getStringFromJsonObjectNode(objectNode,"fraud_check_result_reason"))
                                .date(LocalDate.now().toString())
                                .time(LocalTime.now().toString())
                                .build())
                        .build())
                .flatMap(providerResponse -> paymentRepository.updatePaymentResource(paymentId, providerResponse)
                        .map(paymentSchema -> PaymentResponse.builder()
                                .paymentId(paymentId)
                                .booking(paymentSchema.getBooking())
                                .providerResponse(paymentSchema.getProviderResponse())
                                .saveCardDetails(paymentSchema.getSaveCardDetails())
                                .payment(paymentSchema.getPayment())
                                .build()));
    }

    String getExpiryDate(ObjectNode objectNode) {
        var cardExpiry = ofNullable(objectNode.get("CardExpiry"));
        var tokenExpiry = ofNullable(objectNode.get("TokenExpiry"));
        if (cardExpiry.isPresent() && !cardExpiry.get().asText().isBlank()) {
            return formattedWebhookExpiryDate(cardExpiry.get().asText());
        }
        if (tokenExpiry.isPresent() && !tokenExpiry.get().asText().isBlank()) {
            return formattedWebhookExpiryDate(tokenExpiry.get().asText());
        }
        return "";
    }

    private String formattedWebhookExpiryDate(String expiryDate) {
        YearMonth yearMonth = YearMonth.parse(expiryDate, DateTimeFormatter.ofPattern("yyMM"));
        return yearMonth.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private String getStringFromJsonObjectNode(ObjectNode objectNode, String label) {

        return isNull(objectNode.get(label)) ? "" : objectNode.get(label).asText();
    }
}
