package uk.co.whitbread.basket.infrastructure.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposit;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.ports.secondary.DepositFolioOutPort;
import uk.co.whitbread.basket.infrastructure.repository.model.PrepaidDepositEntity;
import uk.co.whitbread.basket.infrastructure.rest.utils.DepositFolioUtils;

@Slf4j
@RequiredArgsConstructor
public class DepositFolioOutPortImpl implements DepositFolioOutPort {

  private final PrepaidDepositRepository prepaidDepositRepository;

  private static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.SECONDS);
  }


  @Override
  public void createBasketPrepaidDeposit(final PrepaidDepositsRequest request) {
    log.debug("Entering create createBasketDeposit with PrepaidDepositsRequest={}", request);

    request.getPrepaidDeposits().forEach(prepaidDeposit -> {
      final PrepaidDepositEntity prepaidDepositEntity = PrepaidDepositEntity.builder()
          .reservationId(prepaidDeposit.getReservationId())
          .paymentNo(System.currentTimeMillis())
          .charges(DepositFolioUtils.getBytesFrom(prepaidDeposit.getCharges()))
          .cleanUpTime(prepaidDeposit.getCleanUpTime())
          .createdAt(now().toString())
          .build();

      prepaidDepositRepository.save(prepaidDepositEntity);
    });
  }

  @Override
  public void updateBasketPrepaidDeposit(PrepaidDeposits prepaidDeposits) {
    log.debug("Entering updateBasketDeposit with prepaidDeposits={}", prepaidDeposits);

    prepaidDeposits.getPrepaidDeposits().forEach(prepaidDeposit -> {
      final PrepaidDepositEntity prepaidDepositEntity = PrepaidDepositEntity.builder()
          .reservationId(prepaidDeposit.getReservationId())
          .paymentNo(prepaidDeposit.getPaymentNo())
          .charges(DepositFolioUtils.getBytesFrom(prepaidDeposit.getCharges()))
          .cleanUpTime(prepaidDeposit.getCleanUpTime())
          .createdAt(now().toString())
          .build();

      prepaidDepositRepository.save(prepaidDepositEntity);
    });
  }

  @Override
  public PrepaidDeposits getBasketPrepaidDeposit(String reservationId) {
    List<PrepaidDepositEntity> prepaidDepositEntity = prepaidDepositRepository.get(reservationId);

    return PrepaidDeposits.builder()
        .prepaidDeposits(prepaidDepositEntity.stream()
            .map(item -> PrepaidDeposit.builder()
                .charges(DepositFolioUtils.getChargesFrom(item.getCharges()))
                .paymentNo(item.getPaymentNo())
                .reservationId(item.getReservationId())
                .build())
            .toList())
        .build();
  }

  @Override
  public PrepaidDeposits getBasketPrepaidDeposits(List<String> reservationIds) {
    List<PrepaidDepositEntity> destList = new CopyOnWriteArrayList<>();

    reservationIds
        .parallelStream()
        .map(prepaidDepositRepository::get)
        .forEach(destList::addAll);

    return PrepaidDeposits.builder()
        .prepaidDeposits(destList.stream()
            .map(item -> PrepaidDeposit.builder()
                .charges(DepositFolioUtils.getChargesFrom(item.getCharges()))
                .paymentNo(item.getPaymentNo())
                .reservationId(item.getReservationId())
                .build())
            .toList())
        .build();
  }
}
