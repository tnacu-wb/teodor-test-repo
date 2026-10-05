package uk.co.whitbread.hotel.register.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.hotel.register.model.AppsContactDetail;
import uk.co.whitbread.hotel.register.model.AppsCustomer;
import uk.co.whitbread.hotel.register.model.Customer;

class AccountMapperTest {

  private static final String EMPTY = "";
  private AccountMapper accountMapper;

  @BeforeEach
  void setUp() {
    accountMapper = Mappers.getMapper(AccountMapper.class);
  }

  @Test
  void testAppsCustomerHandle() {
    // Arrange
    AppsCustomer request = new AppsCustomer();
    AppsContactDetail contact = new AppsContactDetail();
    contact.setFirstName("firstName");
    contact.setLastName("lastName");
    contact.setEmail("email");
    request.setContactDetail(contact);
    request.setPassword("password");
    request.setCaptcha("string");

    // Act
    Customer result = accountMapper.toAccountRequest(request);

    // Assert
    assertEquals("email", result.getContactDetail().getEmail());
    assertEquals("password", result.getPassword());
    assertEquals("firstName", result.getContactDetail().getFirstName());
    assertEquals("lastName", result.getContactDetail().getLastName());
    assertEquals("string", result.getCaptcha());
    assertEquals(Collections.emptyList(), result.getAdditionalGuests());
    assertEquals(EMPTY, result.getContactDetail().getAddress().getPostCode());
    assertEquals(EMPTY, result.getContactDetail().getAddress().getCompanyName());
    assertEquals(EMPTY, result.getContactDetail().getCarRegistration());
  }

  @Test
  void testAppsCustomerNullDetailsHandle() {
    // Arrange
    AppsCustomer request = new AppsCustomer();
    request.setPassword("password");
    request.setCaptcha("string");

    // Act
    Customer result = accountMapper.toAccountRequest(request);

    // Assert
    assertEquals("password", result.getPassword());
    assertEquals("string", result.getCaptcha());
    assertEquals(Collections.emptyList(), result.getAdditionalGuests());
    assertNull(result.getContactDetail());
  }
}