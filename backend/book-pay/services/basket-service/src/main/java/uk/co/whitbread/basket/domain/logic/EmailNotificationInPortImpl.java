package uk.co.whitbread.basket.domain.logic;

import static java.util.Optional.ofNullable;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import uk.co.whitbread.basket.domain.exception.BasketReferenceNotValidException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.email.in.EmailRequest;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.ports.primary.EmailNotificationInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@AllArgsConstructor
public class EmailNotificationInPortImpl implements EmailNotificationInPort {

  private static final String DISTRIBUTION_CHANNEL = "DISTR";
  private final BasketOutPort basketOutPort;
  private final HotelReservationOutPort reservationOutPort;
  private final EmailNotificationService emailNotificationService;

  @Async
  public void triggerEmailNotification(final EmailRequest emailRequest) {
    var basket = basketOutPort.getBasketById(emailRequest.getBookingReference());
    var reservations = ofNullable(basket).map(
            bsk -> reservationOutPort.getReservationsByBasketReference(bsk.getBasketId(), "false", false))
        .orElseThrow(() -> {
          var exception = new BasketReferenceNotValidException(
              ErrorCode.DIGITAL_EMAIL_BASKET_INVALID_EXCEPTION,
              String.format("Basket is no longer valid: %s", emailRequest.getBookingReference()));
          ExceptionLogger.log(log, exception);
          return exception;
        });
    String requestEmail = emailRequest.getEmail();
    String reservationEmail = reservations.getReservationByIdList().stream().findFirst()
        .map(reservation -> reservation.getBilling().getEmail()).orElse(null);
    String toEmailAddress = StringUtils.isNotBlank(requestEmail) ? requestEmail : reservationEmail;
    LocalDate departureDate = reservations.getReservationByIdList().stream().findFirst()
        .map(reservation -> LocalDate.parse(reservation.getRoomStay().getDepartureDate())).orElse(null);
    if (LocalDate.now().isAfter(departureDate)) {
      var invoiceEmail = DISTRIBUTION_CHANNEL.equalsIgnoreCase(Optional.of(basket)
          .map(Basket::getChannel)
          .orElse(""))
          ? toEmailAddress : reservationEmail;
      emailNotificationService.sendEmailNotificationEvent(basket, EmailNotificationEventType.INVOICE, invoiceEmail,
          false);
    } else if (Objects.nonNull(emailRequest.getEmailRequestType())
        && EmailNotificationEventType.AMEND.toString().equals(emailRequest.getEmailRequestType().toUpperCase())) {
      emailNotificationService.sendEmailNotificationEvent(basket, EmailNotificationEventType.AMEND, toEmailAddress,
          emailRequest.isFailedRefund(), emailRequest.getDeposits());
    } else if (Objects.nonNull(emailRequest.getEmailRequestType())
        && EmailNotificationEventType.CANCEL.toString().equals(emailRequest.getEmailRequestType().toUpperCase())) {
      emailNotificationService.sendEmailNotificationEvent(basket, EmailNotificationEventType.CANCEL, toEmailAddress,
          emailRequest.isFailedRefund(), emailRequest.getDeposits());
    } else if (basket != null && basket.getStatus() == BasketStatus.COMPLETED) {
      emailNotificationService.sendEmailNotificationEvent(basket,
          EmailNotificationEventType.RESEND_CONF, toEmailAddress, true);
    }
  }
  /*  TODO Handle SPLIT case. CONFIRM, FAIL and CANCEL should be handled after the basket status
        will be changed to these states */
}
