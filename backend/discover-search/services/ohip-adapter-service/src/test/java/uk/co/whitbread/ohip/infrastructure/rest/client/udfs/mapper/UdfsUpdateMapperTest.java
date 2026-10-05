package uk.co.whitbread.ohip.infrastructure.rest.client.udfs.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.udfs.in.CharacterUdf;

@ExtendWith(MockitoExtension.class)
class UdfsUpdateMapperTest {


  private UpdateReservationOverrideUdfsRequestOhipMapper mapper = new UpdateReservationOverrideUdfsRequestOhipMapperImpl();

  @ParameterizedTest
  @MethodSource("provideTestData")
  void toDto__ShouldReturnOK(String hotelId, List<CharacterUdf> udfs,
      List<HotelReservationInstructionType> expected) {
    // Act
    var result = mapper.toDto(hotelId, udfs);

    // Assert
    assertNotNull(result);
    assertEquals(expected, result.getReservations());
  }

  private static Stream<Object[]> provideTestData() {
    return Stream.of(
        new Object[]{
            "TestHotelId1",
            List.of(new CharacterUdf("UDF1", "Value1"), new CharacterUdf("UDF2", "Value2")),
            mockHotelReservationInstructionType("TestHotelId1",
                List.of(mockCharacterUdfs("UDF1", "Value1"), mockCharacterUdfs("UDF2", "Value2")))
        },
        new Object[]{
            "TestHotelId2",
            List.of(),
            mockHotelReservationInstructionType("TestHotelId2", List.of())
        },
        new Object[]{
            "TestHotelId3", null,
            mockHotelReservationInstructionType("TestHotelId3", null)
        }
    );
  }

  private static CharacterUDFType mockCharacterUdfs(String name, String value) {
    var udfType = new CharacterUDFType();
    udfType.setName(name);
    udfType.setValue(value);
    return udfType;
  }

  private static List<HotelReservationInstructionType> mockHotelReservationInstructionType(
      String hotelId, List<CharacterUDFType> udfs) {
    var reservations = new ArrayList<HotelReservationInstructionType>();
    var hotelReservation = new HotelReservationInstructionType();
    UserDefinedFieldsType udfType = new UserDefinedFieldsType();
    udfType.setCharacterUDFs(udfs);
    hotelReservation.setHotelId(hotelId);
    hotelReservation.setUserDefinedFields(udfType);
    reservations.add(hotelReservation);
    return reservations;
  }
}
