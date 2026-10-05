package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Discriminated request hierarchy for unified payment initialization.
 *
 * <p>Jackson deserializes to the correct subtype based on the {@code paymentMethod}
 * discriminator field. Each subtype carries method-specific parameters (e.g.
 * {@code returnUrl} for web, absent for mobile).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "paymentMethod")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NewCardWebInitRequest.class, name = "NEW_CARD_WEB"),
    @JsonSubTypes.Type(value = NewCardMobileInitRequest.class, name = "NEW_CARD_MOBILE")
})
@ConditionalValidation
@Schema(
    description = "Discriminated payment initialization request. The paymentMethod field "
        + "selects the subtype: NEW_CARD_WEB carries a returnUrl (HTTPS, host must be in the "
        + "payment.security.allowed-return-url-hosts allowlist); NEW_CARD_MOBILE does not.",
    discriminatorProperty = "paymentMethod",
    discriminatorMapping = {
        @DiscriminatorMapping(value = "NEW_CARD_WEB", schema = NewCardWebInitRequest.class),
        @DiscriminatorMapping(value = "NEW_CARD_MOBILE", schema = NewCardMobileInitRequest.class)
    },
    oneOf = {NewCardWebInitRequest.class, NewCardMobileInitRequest.class})
public sealed interface PaymentInitRequest
    permits NewCardWebInitRequest, NewCardMobileInitRequest {

  PaymentMethod paymentMethod();

  @NotBlank String basketId();

  @NotBlank String country();

  @NotBlank String language();

  @NotBlank String userType();

  @NotBlank String clientChannel();
}
