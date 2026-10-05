package uk.co.whitbread.kiosk.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CardRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.kiosk.domain.model.checkin.in.PaymentDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.in.StayingGuestDetails;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.kiosk.domain.model.checkin.out.CurrentRoomInfo;
import uk.co.whitbread.kiosk.domain.model.checkin.out.Reservation;
import uk.co.whitbread.kiosk.domain.model.checkin.out.RoomStay;
import uk.co.whitbread.kiosk.domain.ports.primary.KioskInPort;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.KioskController;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.mapper.CheckInRequestMapper;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.CardRequestDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.CheckInRequestDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.PaymentDetailsDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.StayingGuestDetailsDto;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out.CheckInResponseDto;

@ExtendWith(MockitoExtension.class)
public class KioskControllerTest {
  
  @InjectMocks
  private KioskController kioskController;
  
  @Mock
  private KioskInPort kioskInPort;
  
  @Mock
  private CheckInRequestMapper checkInRequestMapper;
  
  private MockMvc mockMvc;
  
  @BeforeEach
  void initTest(){
    mockMvc = MockMvcBuilders.standaloneSetup(kioskController)
        .setControllerAdvice()
        .build();
  }
  
  @Test
  public void test_checkInController_authenticated() {
    
    when(checkInRequestMapper.toCheckInRequestModel(any())).thenReturn(mockCheckInRequest());
    when(kioskInPort.getCheckInResponse(any())).thenReturn(mockCheckInResponse());
    
    CheckInResponseDto checkInResponseDto = new CheckInResponseDto();
    checkInResponseDto.setNumberOfKeys(2);
    checkInResponseDto.setRoomNumber("324");
    checkInResponseDto.setWifiAccessCode("WBBWYZNE7S");
    
    var response = kioskController.checkInController(mockCheckInRequestDto());
    
    assertNotNull(response);
  }
  
  @Test
  public void test_checkInController_withNullPayload() throws Exception {
    // Note: standalone MockMvc does not enforce @PreAuthorize security.
    // Security integration tests are covered in KioskControllerSecurityIT.
    mockMvc.perform(MockMvcRequestBuilders.post("/v1/kiosk/checkIn")).andExpect(status().isBadRequest());
  }
  
  
  private CheckInRequest mockCheckInRequest() {
    return CheckInRequest.builder()
        .reservationNumber("2777648")
        .hotelId("GATGAT")
        .roomId("324")
        .roomType("PPLDBL")
        .paymentDetails(PaymentDetails.builder()
            .provider(null)
            .offline(false)
            .amount(new BigDecimal(82.0))
            .card(CardRequest.builder()
                .cardholderName("Mr X Y ZZZZZ")
                .cardType("VA")
                .expiryMonth("12")
                .expiryYear("25")
                .token("4216333880397891103")
                .cnpRequired(false)
                .logoUrl(null)
                .type("1104")
                .cardSchemeId("VS")
                .cardSchemeName(null)
                .build())
            .build())
        .stayingGuestDetails(Collections.singletonList(StayingGuestDetails.builder()
            .title("Ms")
            .firstName("Penny")
            .lastName("user")
            .address(null)
            .build()))
        .build();
  }
  
  private CheckInRequestDto mockCheckInRequestDto() {
    return CheckInRequestDto.builder()
        .reservationNumber("2777648")
        .hotelId("GATGAT")
        .roomId("324")
        .roomType("PPLDBL")
        .paymentDetails(PaymentDetailsDto.builder()
            .provider(null)
            .offline(false)
            .amount(new BigDecimal(82.0))
            .card(CardRequestDto.builder()
                .cardholderName("Mr X Y ZZZZZ")
                .cardType("VA")
                .expiryMonth("12")
                .expiryYear("25")
                .token("4216333880397891103")
                .cnpRequired(false)
                .logoUrl(null)
                .type("1104")
                .cardSchemeId("VS")
                .cardSchemeName(null)
                .build())
            .build())
        .stayingGuestDetails(Collections.singletonList(StayingGuestDetailsDto.builder()
            .title("Ms")
            .firstName("Penny")
            .lastName("user")
            .address(null)
            .build()))
        .build();
  }
  
  private CheckInResponse mockCheckInResponse() {
    return CheckInResponse.builder()
        .reservation(Collections.singletonList(Reservation.builder()
            .roomStay(RoomStay.builder()
                .currentRoomInfo(CurrentRoomInfo.builder()
                    .roomId("324")
                    .build())
                .build())
            .build()))
        .build();
  }
  
}
