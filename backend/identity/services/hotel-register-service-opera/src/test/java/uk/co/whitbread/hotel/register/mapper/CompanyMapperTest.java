package uk.co.whitbread.hotel.register.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;

public class CompanyMapperTest {

  private CompanyMapper companyMapper;

  @BeforeEach
  public void setUp() {
    companyMapper = Mappers.getMapper(CompanyMapper.class);
  }

  @Test
  public void testSetSocialMediaHandle() {
    // Arrange
    InnBRegistrationStepOneRequest request = mock(InnBRegistrationStepOneRequest.class);
    when(request.getSocialMediaType()).thenReturn("Facebook");
    when(request.getSocialMediaId()).thenReturn("12345");

    CompanyAccountRequest result = new CompanyAccountRequest();

    // Act
    companyMapper.setSocialMediaHandle(request, result);

    // Assert
    assertEquals("Facebook|12345", result.getSocialMediaHandle());
  }

  @Test
  public void testSetSocialMediaHandle_BlankFields() {
    // Arrange
    InnBRegistrationStepOneRequest request = mock(InnBRegistrationStepOneRequest.class);
    when(request.getSocialMediaType()).thenReturn("");
    when(request.getSocialMediaId()).thenReturn("");

    CompanyAccountRequest result = new CompanyAccountRequest();

    // Act
    companyMapper.setSocialMediaHandle(request, result);

    // Assert
    assertNull(result.getSocialMediaHandle());
  }

}