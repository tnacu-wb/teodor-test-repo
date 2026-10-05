package uk.co.whitbread.cdh.infrastructure.rest.client.spending.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.cdh.infrastructure.rest.client.spending.model.EmployeeSpendResponse;

class EmployeeSpendClientMapperTest {

  private EmployeeSpendClientMapper mapper;

  @BeforeEach
  void init() {
    mapper = Mappers.getMapper(EmployeeSpendClientMapper.class);
  }

  @Test
  void shouldMapResponseToModel() {
    // Given
    EmployeeSpendResponse response = EmployeeSpendResponse.builder()
        .companyAccountId("COMP123")
        .employeeAccountId("EMP456")
        .year(2025)
        .month(9)
        .noOfBookings(2)
        .bookingValue(96.00)
        .bookingCurrency("EUR")
        .build();

    // When
    var result = mapper.toModel(response);

    // Then
    assertNotNull(result);
    assertEquals("COMP123", result.getCompanyAccountId());
    assertEquals("EMP456", result.getEmployeeAccountId());
    assertEquals(2025, result.getYear());
    assertEquals(9, result.getMonth());
    assertEquals(2, result.getNoOfBookings());
    assertEquals(96.00, result.getBookingValue());
    assertEquals("EUR", result.getBookingCurrency());
  }
}
