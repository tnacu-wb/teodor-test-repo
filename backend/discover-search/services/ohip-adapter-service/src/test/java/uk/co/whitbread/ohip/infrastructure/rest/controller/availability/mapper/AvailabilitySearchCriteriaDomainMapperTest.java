package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityRequestDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AvailabilitySearchCriteriaDomainMapperImpl.class)
class AvailabilitySearchCriteriaDomainMapperTest {

  @Autowired
  AvailabilitySearchCriteriaDomainMapper availabilitySearchCriteriaDomainMapper;

  @Test
  void toDomain__ShouldReturnOK() {
    //Arrange
    AvailabilityRequestDto availabilityRequestDto = AvailabilityRequestDto.builder()
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-03")
        .roomTypes(new String[]{"DOUBLE"})
        .adults(new Integer[]{1})
        .children(new Integer[]{0})
        .build();

    //Act

    AvailabilitySearchCriteria availabilitySearchCriteria =
        availabilitySearchCriteriaDomainMapper.toDomainModel("TestHotelId", availabilityRequestDto);

    //Assert
    assertEquals("TestHotelId", availabilitySearchCriteria.getHotelId());
    assertEquals("2022-01-01", availabilitySearchCriteria.getArrivalDate());
    assertEquals("2022-01-03", availabilitySearchCriteria.getDepartureDate());
    assertEquals(Arrays.asList("DOUBLE"), availabilitySearchCriteria.getRoomTypes());
    assertEquals(Arrays.asList(1), availabilitySearchCriteria.getAdults());
    assertEquals(Arrays.asList(0), availabilitySearchCriteria.getChildren());
  }

}