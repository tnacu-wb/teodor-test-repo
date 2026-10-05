package uk.co.whitbread.basket.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.model.basket.in.Charge;
import uk.co.whitbread.basket.domain.model.basket.in.ChargeAmount;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposit;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.ports.secondary.DepositFolioOutPort;
import uk.co.whitbread.basket.infrastructure.repository.model.PrepaidDepositEntity;

@ExtendWith(MockitoExtension.class)
class DepositFolioOutPortTest {

  private DepositFolioOutPort depositFolioOutPort;
  @Mock
  private PrepaidDepositRepository prepaidDepositRepository;

  @BeforeEach
  public void init() {
    depositFolioOutPort = new DepositFolioOutPortImpl(prepaidDepositRepository);
  }

  @Test
  void test_CreateBasketPrepaidDeposit() {

    // Arrange
    when(prepaidDepositRepository.save(any(PrepaidDepositEntity.class)))
        .thenAnswer(i -> Mono.just(i.getArguments()[0]));

    // Act

    Charge charge = Charge.builder().transactionCode("9010").postingReference("ref1")
        .chargeAmount(
            ChargeAmount.builder().amount(BigDecimal.valueOf(10)).currencyCode("EUR").build())
        .postingQuantity(1).build();
    PrepaidDeposit prepaidDeposit = PrepaidDeposit.builder()
        .reservationId("11111").paymentNo(1L).charges(Collections.singletonList(charge)).build();

    PrepaidDepositsRequest request = PrepaidDepositsRequest.builder()
        .prepaidDeposits(Collections.singletonList(prepaidDeposit)).build();

    depositFolioOutPort.createBasketPrepaidDeposit(request);

    // Assert
    verify(prepaidDepositRepository).save(any());
  }

  @Test
  void test_UpdateBasketPrepaidDeposit() {

    // Arrange
    when(prepaidDepositRepository.save(any(PrepaidDepositEntity.class)))
        .thenAnswer(i -> Mono.just(i.getArguments()[0]));

    // Act

    Charge charge = Charge.builder().transactionCode("9010").postingReference("ref1")
        .chargeAmount(
            ChargeAmount.builder().amount(BigDecimal.valueOf(10)).currencyCode("EUR").build())
        .postingQuantity(1).build();
    PrepaidDeposit prepaidDeposit = PrepaidDeposit.builder()
        .reservationId("11111").paymentNo(1L).charges(Collections.singletonList(charge)).build();

    PrepaidDeposits request = PrepaidDeposits.builder()
        .prepaidDeposits(Collections.singletonList(prepaidDeposit)).build();

    depositFolioOutPort.updateBasketPrepaidDeposit(request);

    // Assert
    verify(prepaidDepositRepository).save(any());
  }

  @Test
  void test_getPrepaidDeposit() throws IOException {
    when(prepaidDepositRepository.get("reservationId")).thenReturn(List.of(mockPrepaidDepositEntity()));

    var depositFolio = depositFolioOutPort.getBasketPrepaidDeposit("reservationId");

    verify(prepaidDepositRepository).get("reservationId");
    assertThat(depositFolio).isNotNull();
  }

  @Test
  void test_getPrepaidDeposits() throws IOException {
    when(prepaidDepositRepository.get("reservationId")).thenReturn(
        List.of(mockPrepaidDepositEntity()));

    var depositFolio = depositFolioOutPort.getBasketPrepaidDeposits(List.of("reservationId"));

    verify(prepaidDepositRepository).get("reservationId");
    assertThat(depositFolio).isNotNull();
  }

  PrepaidDepositEntity mockPrepaidDepositEntity() throws IOException {
    List<Charge> charges = List.of(Charge.builder()
        .chargeAmount(ChargeAmount.builder().amount(BigDecimal.ONE).currencyCode("$").build())
        .postingReference("postingReference").build());

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
    
    ObjectMapper mapper = new ObjectMapper();
    byte[] jsonBytes = mapper.writeValueAsBytes(charges);
    gzipOut.write(jsonBytes);
    gzipOut.finish();
    gzipOut.close();

    return PrepaidDepositEntity.builder().reservationId("reservationId")
        .charges(byteArrayOutputStream.toByteArray()).paymentNo(103L)
        .createdAt("12-08-2023")
        .build();
  }

}
