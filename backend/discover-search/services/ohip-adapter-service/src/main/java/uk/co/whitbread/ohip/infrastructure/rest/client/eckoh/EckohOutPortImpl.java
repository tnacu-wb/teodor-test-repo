package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh;

import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_FORMAT_DATE_ECKOH_EXCEPTION;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.ports.secondary.EckohOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.exceptions.EckohException;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.mapper.EckohChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in.EckohChangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in.PaymentEckohCardDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip.EckohOhipClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.utils.EckohToOperaCardTypeHelper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;


@RequiredArgsConstructor
@Slf4j
public class EckohOutPortImpl implements EckohOutPort {

  private final EckohOhipClient eckohOhipClient;
  private final EckohChangeRequestMapper eckohChangeRequestMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public void eckohWebhook(
      EckohWebhook eckohWebhookRequest) {
    var blockId = eckohWebhookRequest.getReference();
    var reservationsDetailsOhip = eckohOhipClient.getReservationsByBlockId(
        blockId);
    if (Objects.isNull(reservationsDetailsOhip.getReservations().getReservationInfo())) {
      log.warn(
          "There are no reservation linked to block ID {}. Payment information could not be attached.",
          blockId);
      return;
    }
    reservationsDetailsOhip.getReservations().getReservationInfo()
        .forEach(reservationInfoType -> {
          log.debug("Update payment information for reservartion {}",
              getReservationId(reservationInfoType));
          var eckohChangeRequest = createEckohChangeRequest(reservationInfoType,
              eckohWebhookRequest);
          eckohOhipClient.sendChangeReservationRequest(
              eckohChangeRequest.getHotelId(),
              eckohChangeRequest.getReservationId(),
              eckohChangeRequestMapper.toChangeReservationModel(eckohChangeRequest));
        });

  }

  private EckohChangeRequestDto createEckohChangeRequest(ReservationInfoType reservationInfoType,
      EckohWebhook eckohWebhookRequest) {
    var operaMappedPaymentDetails = EckohToOperaCardTypeHelper.getOperaCardTypeFrom(
        eckohWebhookRequest.getScheme(),
        eckohWebhookRequest.getType(),
        unleashWrapper);
    return EckohChangeRequestDto.builder()
        .hotelId(reservationInfoType.getHotelId())
        .reservationId(getReservationId(reservationInfoType))
        .paymentCard(PaymentEckohCardDto.builder()
            .cardType(operaMappedPaymentDetails.getLeft())
            .paymentMethod(operaMappedPaymentDetails.getRight().name())
            .expirationDate(extractDateFromRequest(eckohWebhookRequest.getExpiry()))
            .token(eckohWebhookRequest.getToken())
            .maskedPan(eckohWebhookRequest.getMaskedPan())
            .build())
        .build();
  }

  private String extractDateFromRequest(String date) {
    try {
      SimpleDateFormat parser = new SimpleDateFormat("MMyy");
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      return formatter.format(parser.parse(date));
    } catch (ParseException error) {
      var exception = new EckohException(DIGITAL_FORMAT_DATE_ECKOH_EXCEPTION,
          String.format("Unable to format: %s", date));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private String getReservationId(ReservationInfoType reservationInfoType) {
    return reservationInfoType.getReservationIdList().stream()
        .filter(res -> res.getType().equals(
            UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
        .findFirst().orElseThrow(() -> {
          var exception = new EckohException(ErrorCode.DIGITAL_GET_RESERVATION_ID_EXCEPTION,
              "Unable to get reservationId");
          ExceptionLogger.log(log, exception);
          return exception;
        })
        .getId();
  }
}
