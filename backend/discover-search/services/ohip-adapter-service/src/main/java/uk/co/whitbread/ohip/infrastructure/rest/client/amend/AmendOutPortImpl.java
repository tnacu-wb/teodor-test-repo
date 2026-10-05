package uk.co.whitbread.ohip.infrastructure.rest.client.amend;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.AmendOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;

@RequiredArgsConstructor
@Slf4j
public class AmendOutPortImpl implements AmendOutPort {

  private final OhipReservationClient ohipReservationClient;

  @Override
  public AmendSummaryResponse getRateInfoSummary(AmendSummaryRequest amendSummaryRequest) {

    Map<String, BigDecimal> deposit = new java.util.HashMap<>();
    Map<String, BigDecimal> guestPay = new java.util.HashMap<>();
    AmendSummaryResponse amendSummaryResponse = new AmendSummaryResponse(new BigDecimal(0),
        deposit,
        new BigDecimal(0),
        new BigDecimal(0),
        guestPay);

    IntStream.range(0, amendSummaryRequest.getReservationIds().size())
        .forEach(index -> {
          String reservationId = amendSummaryRequest.getReservationIds().get(index);
          var rateInfo = ohipReservationClient.getRateInfo(
              amendSummaryRequest.getHotelId(),
              reservationId,
              LocalDate.now().toString(),
              "true");

          amendSummaryResponse.setNet(amendSummaryResponse.getNet().add(rateInfo.getSummary().getNet()));
          deposit.put(reservationId, rateInfo.getSummary().getDeposit());
          amendSummaryResponse.setTotalCostOfStay(
              amendSummaryResponse.getTotalCostOfStay().add(rateInfo.getSummary().getTotalCostOfStay()));
          amendSummaryResponse.setOutStandingCostOfStay(
              amendSummaryResponse.getOutStandingCostOfStay().add(rateInfo.getSummary().getOutStandingCostOfStay()));
          guestPay.put(reservationId, rateInfo.getSummary().getOutStandingCostOfStay());
        });
    amendSummaryResponse.setDeposit(deposit);
    amendSummaryResponse.setGuestPay(guestPay);
    return amendSummaryResponse;
  }
}
