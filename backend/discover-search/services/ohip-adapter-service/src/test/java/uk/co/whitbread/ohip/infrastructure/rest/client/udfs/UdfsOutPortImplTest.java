package uk.co.whitbread.ohip.infrastructure.rest.client.udfs;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.udfs.in.CharacterUdf;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.udfs.mapper.UpdateReservationOverrideUdfsRequestOhipMapper;

@ExtendWith(MockitoExtension.class)
class UdfsOutPortImplTest {

  private static final String HOTEL_ID = "TestHotelId";
  private static final String UDF_1 = "UDF1";
  private static final String UDF_2 = "UDF2";
  private static final String VALUE_1 = "Value1";
  private static final String VALUE_2 = "Value2";

  @InjectMocks
  private UdfsOutPortImpl udfsOutPort;
  @Mock
  private OhipReservationClient ohipReservationClient;
  @Mock
  private UpdateReservationOverrideUdfsRequestOhipMapper mapper;
  private Set<String> reservIds = Set.of("resv1", "resv2");

  @Test
  void updateUdfs__ShouldReturnOK() {
    //Arrange
    var udfsList = List.of(mockCharacterUdfs(UDF_1, VALUE_1),
        mockCharacterUdfs(UDF_2, VALUE_2));
    var udfs = List.of(new CharacterUdf(UDF_1, VALUE_1),
        new CharacterUdf(UDF_2, VALUE_2));
    var updatedReservation = mockChangeReservationDetails(udfsList);

    ChangeReservation changeReservation = mockUpdateReservationOverrideUdfsRequestDto(udfsList);
    when(mapper.toDto(anyString(), anyList())).thenReturn(changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(
        eq(HOTEL_ID),
        anyString(),
        eq(changeReservation))).thenReturn(Mono.just(updatedReservation));

    //Act
    udfsOutPort.updateUdfs(HOTEL_ID, reservIds, udfs);

    //Assert
    verify(ohipReservationClient, times(2)).sendChangeReservationRequest(eq(HOTEL_ID),
        anyString(),
        eq(changeReservation));
    verify(mapper, times(2)).toDto(anyString(), anyList());
    verifyNoMoreInteractions(ohipReservationClient);
    verifyNoMoreInteractions(mapper);
  }

  @Test
  void updateUdfs__shouldThrowException() {
    // Arrange
    var udfsList = List.of(mockCharacterUdfs(UDF_1, VALUE_1),
        mockCharacterUdfs(UDF_2, VALUE_2));
    var udfs = List.of(new CharacterUdf(UDF_1, VALUE_1),
        new CharacterUdf(UDF_2, VALUE_2));
    ChangeReservation changeReservation = mockUpdateReservationOverrideUdfsRequestDto(udfsList);
    final String error = "Error while trying to update UDFs";

    when(mapper.toDto(anyString(), anyList())).thenReturn(changeReservation);
    Mockito.when(ohipReservationClient.sendChangeReservationRequest(
            eq(HOTEL_ID),
            anyString(),
            eq(changeReservation)))
        .thenThrow(new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION, error));

    // Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () -> {
          udfsOutPort.updateUdfs(HOTEL_ID, reservIds, udfs);
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
    verify(ohipReservationClient).sendChangeReservationRequest(eq(HOTEL_ID),
        anyString(),
        eq(changeReservation));
    verify(mapper).toDto(anyString(), anyList());
    verifyNoMoreInteractions(ohipReservationClient);
    verifyNoMoreInteractions(mapper);
  }


  private CharacterUDFType mockCharacterUdfs(String name, String value) {
    var udfType = new CharacterUDFType();
    udfType.setName(name);
    udfType.setValue(value);
    return udfType;
  }

  private static ChangeReservation mockUpdateReservationOverrideUdfsRequestDto(
      List<CharacterUDFType> udfs) {
    var result = new ChangeReservation();
    var reservations = new ArrayList<HotelReservationInstructionType>();
    var hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setHotelId(HOTEL_ID);
    UserDefinedFieldsType udfType = new UserDefinedFieldsType();
    udfType.setCharacterUDFs(udfs);
    hotelReservationInstructionType.setUserDefinedFields(udfType);
    reservations.add(hotelReservationInstructionType);
    result.setReservations(reservations);
    return result;
  }

  private ChangeReservationDetails mockChangeReservationDetails(
      List<CharacterUDFType> characterUDFs) {
    ChangeReservationDetails updatedReservation = new ChangeReservationDetails();
    HotelReservationsType hotelResr = new HotelReservationsType();
    HotelReservationType hotelReserType = new HotelReservationType();
    UniqueIDType res1 = new UniqueIDType();
    res1.setId("res1");
    UniqueIDType res2 = new UniqueIDType();
    res2.setId("res2");
    hotelReserType.setReservationIdList(List.of(res1, res2));
    UserDefinedFieldsType fieldsType = new UserDefinedFieldsType();
    fieldsType.setCharacterUDFs(characterUDFs);
    hotelReserType.setUserDefinedFields(fieldsType);
    hotelResr.setReservation(List.of(hotelReserType));
    updatedReservation.setReservations(hotelResr);
    return updatedReservation;
  }
}
