package uk.co.whitbread.ohip.infrastructure.rest.controller.depositfolios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.ohip.domain.logic.DepositFoliosInPortImpl;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmount;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.DepositFoliosController;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.mapper.DepositFoliosMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.CurrencyAmountDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFolioChargeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFolioRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFoliosRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out.DepositFoliosResponseDto;


@ExtendWith(MockitoExtension.class)
class DepositFoliosControllerTest {

  @InjectMocks
  DepositFoliosController depositFoliosControllerUnderTest;

  @Mock
  private DepositFoliosInPortImpl depositFoliosInPort;

  @Mock
  private DepositFoliosMapper depositFoliosMapper;

  @Test
  void createDepositFolioForReservations_shouldReturnCreated() {
    //Arrange

    DepositFoliosRequestDto depositFoliosRequestDto = mockDepositFoliosRequestDto();
    DepositFoliosResponse depositFoliosResponse = mockDepositFoliosResponse();

    when(depositFoliosMapper.toModel(depositFoliosRequestDto)).thenReturn(depositFoliosResponse);

    doNothing().when(depositFoliosInPort).createDepositFolios(any());

    //Act
    ResponseEntity<Void> response = depositFoliosControllerUnderTest
        .saveDepositFolios(depositFoliosRequestDto);

    //Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void getDepositFolioForReservations__emptyList() {
    //Arrange
    DepositFoliosResponse depositFoliosResponse = DepositFoliosResponse.builder()
        .depositFolios(Collections.emptyList()).build();
    DepositFoliosResponseDto depositFoliosResponseDto = DepositFoliosResponseDto.builder()
        .depositFolios(Collections.emptyList()).build();
    when(depositFoliosInPort.getDepositFolios(anyString(), anySet()))
        .thenReturn(depositFoliosResponse);
    when(depositFoliosMapper.toDto(any())).thenReturn(depositFoliosResponseDto);

    //Act
    ResponseEntity<DepositFoliosResponseDto> response = depositFoliosControllerUnderTest
        .getDepositFolioForReservations("hotelId", Set.of("res1", "res2"));

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getDepositFolios());
    assertTrue(response.getBody().getDepositFolios().isEmpty());
  }


  private DepositFoliosRequestDto mockDepositFoliosRequestDto() {
    DepositFolioChargeDto charge = DepositFolioChargeDto.builder().quantity(1)
        .transactionCode("9016").reference("2034-07-27")
        .currencyAmount(CurrencyAmountDto.builder().amount(
            BigDecimal.valueOf(123)).currencyCode("EUR").build()).build();
    DepositFolioRequestDto depositFoliosDto = DepositFolioRequestDto.builder().reservationId("11")
        .hotelId("DONTAB").charges(Collections.singletonList(charge))
        .build();

    return DepositFoliosRequestDto.builder().depositFolios(Collections.singletonList(depositFoliosDto))
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosResponse() {
    DepositFolioCharge charge = DepositFolioCharge.builder().quantity(1).transactionCode("9016")
        .reference("2034-07-27").currencyAmount(CurrencyAmount.builder().amount(
            BigDecimal.valueOf(123)).currencyCode("EUR").build()).build();
    DepositFolio depositFolio = DepositFolio.builder().reservationId("11").hotelId("DONTAB")
        .charges(Collections.singletonList(charge)).build();

    return DepositFoliosResponse.builder().depositFolios(Collections.singletonList(depositFolio))
        .build();
  }

}
