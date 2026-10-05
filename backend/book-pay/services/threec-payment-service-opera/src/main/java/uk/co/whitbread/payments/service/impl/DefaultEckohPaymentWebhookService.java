package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.EckohResponse;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.WebHookEckohFormData;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.PaymentWebhookService;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultEckohPaymentWebhookService implements PaymentWebhookService {

  private final PaymentRepository paymentRepository;
  @Override
  public Mono<PaymentResponse> handleWebhook(ServerRequest serverRequest) {
    var formDataMap = serverRequest.bodyToMono(WebHookEckohFormData.class);
    return formDataMap
        .doOnSuccess(webhookData -> log.info("Incoming eckoh webhook for payment resource with id {}.", webhookData.getReference()))
        .map(webhookData -> {
          var providerResponse = ProviderResponse.builder()
              .threeCResponse(ThreeCResponse.builder()
                  .providerReason("")
                  .providerStatus("")
                  .providerResult("")
                  .providerUrl("")
                  .providerStatusText("")
                  .token("")
                  .authCode("")
                  .expiry("")
                  .fraudCheckDecision("")
                  .fraudCheckResult("")
                  .fraudCheckResultReason("")
                  .iPageHtml("")
                  .cardSchemeId("")
                  .cardholderFirstName("")
                  .cardholderLastName("")
                  .cardSchemeName("")
                  .build())
              .eckohResponse(EckohResponse.builder()
                  .result(webhookData.getResult())
                  .resultCode(Long.valueOf(webhookData.getResultCode()))
                  .maskedPan(webhookData.getMaskedPan())
                  .expiry(webhookData.getExpiryDate())
                  .scheme(webhookData.getScheme())
                  .type(webhookData.getType())
                  .reference(webhookData.getReference())
                  .token(webhookData.getToken())
                  .build())
              .build();
          var paymentId = webhookData.getReference();
          return paymentRepository
              .updatePaymentResource(paymentId, providerResponse)
              .map(paymentSchema -> PaymentResponse
                  .builder()
                  .paymentId(paymentId)
                  .providerResponse(paymentSchema.getProviderResponse())
                  .build());
        })
        .flatMap(Mono::from);
  }
}
