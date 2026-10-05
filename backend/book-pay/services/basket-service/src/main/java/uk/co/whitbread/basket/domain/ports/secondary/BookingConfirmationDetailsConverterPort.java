package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

/**
 * Port for creating booking confirmation details from payment response.
 */
public interface BookingConfirmationDetailsConverterPort {

  /**
   * Create booking confirmation details from basket and payment response.
   *
   * @param basket the basket
   * @param hotelPaymentInformation the hotel payment information
   * @param paymentResponse the payment response from provider
   * @return the booking confirmation details for order processing
   */
  BookingConfirmationDetails createConfirmationDetails(Basket basket,
      HotelPaymentInformation hotelPaymentInformation,
      PaymentResponse paymentResponse,
      String paymentProvider);
}
