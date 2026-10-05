package uk.co.whitbread.payments.infrastructure.rest.hotelentity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.exception.HotelInfoException;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.model.out.HotelInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.service.HotelEntityClient;

@ExtendWith(MockitoExtension.class)
class HotelInfoPortImplTest {

  @Mock
  private HotelEntityClient hotelEntityClient;

  @InjectMocks
  private HotelInfoPortImpl hotelInfoPortImpl;

  private static final String HOTEL_CODE = "HOTEL123";
  private static final String UK_COUNTRY_CODE = "GB";
  private static final String DE_COUNTRY_CODE = "DE";
  private static final String THREE_LETTER_ID = "HTL";

  @Test
  void findHotelCountry_success_returnsUKCountryCode() {
    // Arrange
    HotelInfoDto hotelInfoDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(UK_COUNTRY_CODE)
        .build();

    when(hotelEntityClient.getHotelInfo(HOTEL_CODE)).thenReturn(hotelInfoDto);

    // Act
    String result = hotelInfoPortImpl.findHotelCountry(HOTEL_CODE);

    // Assert
    assertEquals(UK_COUNTRY_CODE, result);
    verify(hotelEntityClient).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_success_returnsDECountryCode() {
    // Arrange
    HotelInfoDto hotelInfoDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(DE_COUNTRY_CODE)
        .build();

    when(hotelEntityClient.getHotelInfo(HOTEL_CODE)).thenReturn(hotelInfoDto);

    // Act
    String result = hotelInfoPortImpl.findHotelCountry(HOTEL_CODE);

    // Assert
    assertEquals(DE_COUNTRY_CODE, result);
    verify(hotelEntityClient).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_nullHotelInfo_returnsNull() {
    // Arrange
    when(hotelEntityClient.getHotelInfo(HOTEL_CODE)).thenReturn(null);

    // Act
    String result = hotelInfoPortImpl.findHotelCountry(HOTEL_CODE);

    // Assert
    assertNull(result);
    verify(hotelEntityClient).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_nullCountryCode_returnsNull() {
    // Arrange
    HotelInfoDto hotelInfoDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(null)
        .build();

    when(hotelEntityClient.getHotelInfo(HOTEL_CODE)).thenReturn(hotelInfoDto);

    // Act
    String result = hotelInfoPortImpl.findHotelCountry(HOTEL_CODE);

    // Assert
    assertNull(result);
    verify(hotelEntityClient).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_clientThrowsException_propagatesException() {
    // Arrange
    String errorMessage = "Error fetching hotel info";
    HotelInfoException exception = new HotelInfoException("message", errorMessage,
        new Exception(), 500);

    when(hotelEntityClient.getHotelInfo(HOTEL_CODE)).thenThrow(exception);

    // Act & Assert
    HotelInfoException thrown = assertThrows(HotelInfoException.class,
        () -> hotelInfoPortImpl.findHotelCountry(HOTEL_CODE));

    assertEquals(exception, thrown);
    verify(hotelEntityClient).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_differentHotelCodes_callsClientWithCorrectCode() {
    // Arrange
    String differentHotelCode = "HOTEL999";
    HotelInfoDto hotelInfoDto = HotelInfoDto.builder()
        .threeLetterId("HTL")
        .hotelCountryCode(UK_COUNTRY_CODE)
        .build();

    when(hotelEntityClient.getHotelInfo(differentHotelCode)).thenReturn(hotelInfoDto);

    // Act
    String result = hotelInfoPortImpl.findHotelCountry(differentHotelCode);

    // Assert
    assertEquals(UK_COUNTRY_CODE, result);
    verify(hotelEntityClient).getHotelInfo(differentHotelCode);
    verify(hotelEntityClient, never()).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_emptyHotelCode_passesEmptyCodeToClient() {
    // Arrange
    String emptyHotelCode = "";
    HotelInfoDto hotelInfoDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(UK_COUNTRY_CODE)
        .build();

    when(hotelEntityClient.getHotelInfo(emptyHotelCode)).thenReturn(hotelInfoDto);

    // Act
    String result = hotelInfoPortImpl.findHotelCountry(emptyHotelCode);

    // Assert
    assertEquals(UK_COUNTRY_CODE, result);
    verify(hotelEntityClient).getHotelInfo(emptyHotelCode);
  }

  @Test
  void findHotelCountry_multipleCallsWithSameCode_callsClientEachTime() {
    // Arrange
    HotelInfoDto hotelInfoDto = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode(UK_COUNTRY_CODE)
        .build();

    when(hotelEntityClient.getHotelInfo(HOTEL_CODE)).thenReturn(hotelInfoDto);

    // Act
    String result1 = hotelInfoPortImpl.findHotelCountry(HOTEL_CODE);
    String result2 = hotelInfoPortImpl.findHotelCountry(HOTEL_CODE);

    // Assert
    assertEquals(UK_COUNTRY_CODE, result1);
    assertEquals(UK_COUNTRY_CODE, result2);
    verify(hotelEntityClient, org.mockito.Mockito.times(2)).getHotelInfo(HOTEL_CODE);
  }

  @Test
  void findHotelCountry_variousCountryCodes_returnsCorrectCodes() {
    // Test FR country code
    HotelInfoDto frHotelInfo = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode("FR")
        .build();

    when(hotelEntityClient.getHotelInfo("HOTEL_FR")).thenReturn(frHotelInfo);
    assertEquals("FR", hotelInfoPortImpl.findHotelCountry("HOTEL_FR"));

    // Test ES country code
    HotelInfoDto esHotelInfo = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode("ES")
        .build();

    when(hotelEntityClient.getHotelInfo("HOTEL_ES")).thenReturn(esHotelInfo);
    assertEquals("ES", hotelInfoPortImpl.findHotelCountry("HOTEL_ES"));

    // Test IT country code
    HotelInfoDto itHotelInfo = HotelInfoDto.builder()
        .threeLetterId(THREE_LETTER_ID)
        .hotelCountryCode("IT")
        .build();

    when(hotelEntityClient.getHotelInfo("HOTEL_IT")).thenReturn(itHotelInfo);
    assertEquals("IT", hotelInfoPortImpl.findHotelCountry("HOTEL_IT"));
  }
}

