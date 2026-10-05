package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.ChannelType;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentSubType;
import uk.co.whitbread.payments.model.SaveCardRequest;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.properties.ProviderAccounts;
import uk.co.whitbread.payments.service.ProviderAccountFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static uk.co.whitbread.payments.exception.ErrorCodes.PROVIDER_ACCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultProviderAccountFactory implements ProviderAccountFactory {

  private final ProviderAccounts providerAccounts;

  /**
   * Returns matching payment provider account from configuration based on payment request input.
   *
   * @param paymentRequest the incoming payment request
   * @return the matching provider account
   */
  @Override
  public ProviderAccount getAccount(PaymentRequest paymentRequest) {
    return findProviderAccount(account -> {
      var booking = paymentRequest.getBooking();
      var payment = paymentRequest.getPayment();
      return payment.isEckohTransaction(List.of(PaymentSubType.ECKOH))
          && account.getPaymentSubTypes().contains(PaymentSubType.ECKOH)
          || payment.equals(account.getPaymentType(), account.getPaymentSubTypes(), account.getCurrency().name())
          && booking.getType().equals(Optional.ofNullable(account.getBookingType()).map(Enum::name).orElse(""))
          && account.getChannelTypes().contains(ChannelType.valueOf(booking.getChannel()));
    });
  }

  /**
   * Returns matching payment provider account from configuration based on save card request input.
   *
   * @param saveCardRequest the incoming save card request
   * @return the matching provider account
   */
  @Override
  public ProviderAccount getSaveCardAccount(SaveCardRequest saveCardRequest) {
    return findProviderAccount(account -> account.getPaymentType().name().equalsIgnoreCase(saveCardRequest.getCardType())
        && account.getPaymentSubTypes().contains(PaymentSubType.SAVE_CARD));
  }

  /**
   * Returns matching payment provider account from configuration based on authorize card request input for SCA.
   *
   * @return the matching provider account
   */
  @Override
  public ProviderAccount getAuthorizeScaAccount() {
    return findProviderAccount(account -> account.getPaymentType().name().equals("CARD")
        && account.getPaymentSubTypes().contains(PaymentSubType.AUTHORIZE_CARD));
  }

  /**
   * In the future this will create the merchant id username based on the hotel identifier
   *
   * @param siteIdentifier unique identifier for a Whitbread business site
   * @return the matching provider account
   */
  @Override
  public ProviderAccount getAccountForSite(String siteIdentifier) {
    return providerAccounts.getAccounts().get(0);
  }

  private ProviderAccount findProviderAccount(Predicate<ProviderAccount> predicate) {
    return providerAccounts.getAccounts()
        .stream()
        .filter(predicate)
        .findFirst()
        .orElseThrow(() -> {
          log.error("Unable to find provider account matching request.");
          return new PaymentServiceException(BAD_REQUEST, "Unable to find provider account matching request.", PROVIDER_ACCOUNT_NOT_FOUND);
        });
  }
}
