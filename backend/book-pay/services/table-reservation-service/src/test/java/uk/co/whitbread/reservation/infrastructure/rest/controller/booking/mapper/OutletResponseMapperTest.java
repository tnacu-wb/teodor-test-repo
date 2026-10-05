package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.outlets.Company;
import uk.co.whitbread.reservation.domain.model.out.outlets.Consent;
import uk.co.whitbread.reservation.domain.model.out.outlets.ConsentStatement;
import uk.co.whitbread.reservation.domain.model.out.outlets.ConsentType;
import uk.co.whitbread.reservation.domain.model.out.outlets.Features;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Site;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.FeaturesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.OutletResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SiteDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.CompanyDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentStatementDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentTypeDto;

@ExtendWith(MockitoExtension.class)
class OutletResponseMapperTest {

  @InjectMocks
  OutletResponseMapperImpl mapper;

  @Test
  void testToDO() {
    OutletResponse outletResponse = mockOutletResponse();
    OutletResponseDto result = mapper.toDto(outletResponse);
    // Assert
    assertNotNull(result);
    assertEquals(1, result.getCompanies().size());
  }
  @Test
  void testToDtoWithNullInput() {
    OutletResponseDto result = mapper.toDto(null);

    // Assert
    assertNull(result);
  }


  @Test
  void testToOutletResponseDto() {
    OutletResponse outletResponse = mockOutletResponse();
    List<CompanyDto> result = mapper.toOutletResponseDto(outletResponse);
    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
  }

  @Test
  void testToOutletResponseDtoWithNullCompanies() {
    // Arrange
    OutletResponse outletResponse = new OutletResponse();
    outletResponse.setCompanies(null);
    List<CompanyDto> result = mapper.toOutletResponseDto(outletResponse);

    assertNotNull(result);
    assertEquals(Collections.emptyList(), result);
  }

  @Test
  void testToConsentDto() {
    Consent consent = mockOutletConsent();
    ConsentDto result = mapper.toConsentToDto(consent);
    assertNotNull(result);


  }
  @Test
  void testToConsentDtoWithNullInput() {
    ConsentDto result = mapper.toConsentToDto(null);
    assertNull(result);
  }

  @Test
  void testToConsentTypeToDto() {
    ConsentType consentType = new ConsentType("Sample Consent", true);
    ConsentTypeDto consentTypeDto = mapper.toConsentTypeToDto(consentType);
    assertNotNull(consentTypeDto);
    assertEquals(consentType.getText(), consentTypeDto.getText());
    assertEquals(consentType.isEnabled(), consentTypeDto.isEnabled());
  }

  @Test
  void testToConsentTypeToDtoWithNullInput() {
    ConsentTypeDto consentTypeDto = mapper.toConsentTypeToDto(null);
    assertNull(consentTypeDto);
  }

  @Test
  void toFeatureToDto() {
    Features features = new Features();
    features.setBookableAreas(true);
    FeaturesDto result = mapper.toFeatureToDto(features);
    assertNotNull(result);


  }
  @Test
  void toFeatureToDtoWithNullInput() {
    FeaturesDto result = mapper.toFeatureToDto(null);
    assertNull(result);


  }
  @Test
  void testToConsentStatementToDto() {
    // Arrange
    ConsentStatement consentStatement = new ConsentStatement();
    consentStatement.setText("Sample consent statement");
    consentStatement.setEnabled(true);
    consentStatement.setUrl("https://example.com/consent");

    // Act
    ConsentStatementDto result = mapper.toConsentStatementToDto(consentStatement);
    assertNotNull(result);
    assertEquals("Sample consent statement", result.getText());
    assertTrue(result.isEnabled());
  }
  @Test
  void testToConsentStatementToDtoWithNullInput() {
    ConsentStatementDto result = mapper.toConsentStatementToDto(null);

    // Assert
    assertNull(result);
  }

  @Test
  void toSitesToDto() {
    Site site = new Site();
    Features features = new Features();
    features.setBookableAreas(true);
    site.setName("siteName");
    site.setId("Id");
    site.setAztecSiteReference("tse");
    site.setTimezone("US");
    site.setDefaultOccasionId("1");
    site.setFeatures(features);
    List<Site> siteList = new ArrayList<>();
    siteList.add(site);
    List<SiteDto> result = mapper.toSitesToDto(siteList);
    assertNotNull(result);


  }
  @Test
  void testToSitesToDtoWithNullInput() {
    List<SiteDto> result = mapper.toSitesToDto(null);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size()); // Verify that the result is an empty list when input is null
  }



  private OutletResponse mockOutletResponse() {
    OutletResponse outletResponse = new OutletResponse();

    Company company1 = new Company();
    company1.setId("1");
    company1.setName("Company 1");
    company1.setTermsAndConditions("T&C 1");
    company1.setSites(new ArrayList<>());
    company1.setConsent(mockOutletConsent());
    outletResponse.setCompanies(List.of(company1));
    return outletResponse;
  }
  private Consent mockOutletConsent(){
    Consent consent = new Consent();
    consent.setEmail(new ConsentType());
    consent.setPhone(new ConsentType());
    consent.setSms(new ConsentType());
    consent.setPostal(new ConsentType());
    consent.setPushNotification(new ConsentType());
    consent.setProfiling(new ConsentType());
    consent.setPrivacyStatement(new ConsentStatement());
    consent.setConsentStatement(new ConsentStatement());
    consent.setTermsAndConditions("abc");
    return consent;
  }

}
