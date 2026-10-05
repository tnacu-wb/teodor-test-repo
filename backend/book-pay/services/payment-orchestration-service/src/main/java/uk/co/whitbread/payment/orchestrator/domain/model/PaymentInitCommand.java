package uk.co.whitbread.payment.orchestrator.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Sealed command hierarchy for unified payment initialization.
 *
 * <p>Each subtype carries method-specific parameters needed for a particular Datatrans
 * integration type. The controller maps incoming polymorphic requests 1:1 onto these
 * commands before passing them to the workflow adapter.
 *
 * <p>Jackson type information is required for Temporal's {@code JacksonJsonPayloadConverter}
 * to correctly serialize and deserialize polymorphic command instances in workflow history.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "paymentMethod")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NewCardWebInitCommand.class, name = "NEW_CARD_WEB"),
    @JsonSubTypes.Type(value = NewCardMobileInitCommand.class, name = "NEW_CARD_MOBILE")
})
public sealed interface PaymentInitCommand
    permits NewCardWebInitCommand, NewCardMobileInitCommand {

  /**
   * The Datatrans integration type this command targets.
   */
  PaymentMethod paymentMethod();

  /**
   * The basket identifier correlating this payment to a reservation.
   */
  String basketId();

  /**
   * The country code (e.g. "gb", "de").
   */
  String country();

  /**
   * The language code (e.g. "en", "de").
   */
  String language();

  /**
   * The user type (e.g. "LEISURE", "BUSINESS").
   */
  String userType();

  /**
   * The client channel (e.g. "PI", "APPS_IOS", "APPS_ANDROID").
   */
  String clientChannel();
}
