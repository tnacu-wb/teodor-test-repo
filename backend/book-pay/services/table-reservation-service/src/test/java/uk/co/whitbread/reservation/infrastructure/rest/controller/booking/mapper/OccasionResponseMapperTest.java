package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.occasion.Occasions;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsResponseDto;
@ExtendWith(MockitoExtension.class)
public class OccasionResponseMapperTest {

  @InjectMocks
  private OccasionResponseMapperImpl mapper;

  @Test
  void testToDto() {
    OccasionsResponse occasionsResponse = mockOccasionResponse();
    OccasionsResponseDto result = mapper.toDto(occasionsResponse);
    Assertions.assertNotNull(result);
  }
  @Test
  void testToDtoWithNullInput() {
    OccasionsResponseDto result = mapper.toDto(null);
    assertNull(result);
  }

  @Test
  void testToDtoWithNullOccasions() {
    // Arrange
    OccasionsResponse occasionsResponse = new OccasionsResponse();
    occasionsResponse.setOccasions(null);
    List<OccasionsDto> result = mapper.toResponseToDto(occasionsResponse);
    assertNotNull(result);
    assertEquals(Collections.emptyList(), result);
  }


  private OccasionsResponse mockOccasionResponse() {
    OccasionsResponse occasionsResponse = new OccasionsResponse();
    Occasions occasion1 = new Occasions();
    occasion1.setId("1L");
    occasion1.setName("Birthday");
    occasion1.setAvailable(true);

    Occasions occasion2 = new Occasions();
    occasion2.setId("2L");
    occasion2.setName("Anniversary");
    occasion2.setAvailable(false);
    occasionsResponse.setOccasions(List.of(occasion1, occasion2));
    return occasionsResponse;

  }
}
