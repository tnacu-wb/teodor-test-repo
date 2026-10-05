package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.aem.ZonalUuidResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ZonalUuidResponseDto;

@ExtendWith(MockitoExtension.class)
public class ZonalUuidResponseMapperTest {

  @InjectMocks
  private ZonalUuidResponseMapperImpl mapper;

  @Test
  void testToDto() {
    List<ZonalUuidResponse> mockResponses =new ArrayList<>();
    ZonalUuidResponse zonalUuidResponse = new ZonalUuidResponse();
    zonalUuidResponse.setId("1");
    zonalUuidResponse.setTitle("Sample Title");
    zonalUuidResponse.setPath("/sample/path");
    zonalUuidResponse.setAddress1("Address 1");
    zonalUuidResponse.setAddress2("Address 2");
    zonalUuidResponse.setAddress3("Address 3");
    zonalUuidResponse.setAddress4("Address 4");
    zonalUuidResponse.setExternalSystemIdentifier("External Identifier");
    zonalUuidResponse.setLatitude("12.345");
    zonalUuidResponse.setLongitude("67.890");
    zonalUuidResponse.setExternalSourceSystem("External Source");
    mockResponses.add(zonalUuidResponse);

//   List<ZonalUuidResponseDto> expectedResponse = new ArrayList<>();
//   expectedResponse.add(new ZonalUuidResponseDto("1", "Sample Title", "/sample/path", "12.345",
//       "67.890", "Address 1", "Address 2", "Address 2", "Address 4", "External Identifier",
//       "External Source"));
   // when(mapper.toDto(mockResponses)).thenReturn(expectedResponse);
    List<ZonalUuidResponseDto> zonalUuidResponseDtoS = mapper.toDto(mockResponses);
    assertNotNull(zonalUuidResponseDtoS);
    assertEquals(mockResponses.size(), zonalUuidResponseDtoS.size());
  }


  @Test
  void testToDtoWithNullInput() {

    List<ZonalUuidResponseDto> result = mapper.toDto(null);
    assertNotNull(result);
    assertEquals(0, result.size());

  }

}
