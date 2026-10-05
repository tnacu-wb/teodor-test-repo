package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.events.Consent;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EnquiryResponseDto;

@ExtendWith(MockitoExtension.class)
class EnquiryResponseMapperTest {

  @InjectMocks
  EnquiryResponseMapperImpl mapper;
  @Test
  void testToDto() {
    EnquiryResponse enquiryResponse = mockEnquiryResponse();
    EnquiryResponseDto result = mapper.toDto(enquiryResponse);
    assertNotNull(result);


  }
  @Test
  void testToDtoWithNullInput() {
    EnquiryResponseDto result = mapper.toDto(null);
    assertNull(result);
  }

  @Test
  void testToConsentDto() {
    Consent consent = mockConsent();
    ConsentDto result = mapper.toConsentDto(consent);
    assertNotNull(result);


  }

  @Test
  void testToConsentDtoWithNullInput() {
    ConsentDto result = mapper.toConsentDto(null);
    assertNull(result);
  }


  private EnquiryResponse mockEnquiryResponse(){
    EnquiryResponse enquiryResponse = new EnquiryResponse();
    enquiryResponse.setAdults(2);
    enquiryResponse.setBookingReference("ABCD1234");
    enquiryResponse.setCancelLink("http://example.com/cancel");
    enquiryResponse.setChildren(1);
    enquiryResponse.setConsent(new Consent());
    enquiryResponse.setDate("2023-09-08");
    enquiryResponse.setEditLink("http://example.com/edit");
    enquiryResponse.setEmailAddress("test@example.com");
    enquiryResponse.setFirstname("John");
    enquiryResponse.setId("123");
    enquiryResponse.setLastname("Doe");
    enquiryResponse.setOccasionId("456");
    enquiryResponse.setOccasionName("Birthday");
    enquiryResponse.setPaymentLink("http://example.com/payment");
    enquiryResponse.setSiteId("789");
    enquiryResponse.setSiteName("SiteName");
    enquiryResponse.setTelephoneNumber("123-456-7890");
    enquiryResponse.setTime("14:30");
    enquiryResponse.setTurnTimeMinutes(60);
    return enquiryResponse;

  }
  /*private EnquiryResponseDto mockEnquiryResponseDto(){
    EnquiryResponseDto enquiryResponseDto=new EnquiryResponseDto();
    enquiryResponseDto.setAdults(2);
    enquiryResponseDto.setBookingReference("ABCD1234");
    enquiryResponseDto.setCancelLink("http://example.com/cancel");
    enquiryResponseDto.setChildren(1);
    enquiryResponseDto.setConsent(new ConsentDto());
    enquiryResponseDto.setDate("2023-09-08");
    enquiryResponseDto.setEditLink("http://example.com/edit");
    enquiryResponseDto.setEmailAddress("test@example.com");
    enquiryResponseDto.setFirstname("John");
    enquiryResponseDto.setId("123");
    enquiryResponseDto.setLastname("Doe");
    enquiryResponseDto.setOccasionId("456");
    enquiryResponseDto.setOccasionName("Birthday");
    enquiryResponseDto.setPaymentLink("http://example.com/payment");
    enquiryResponseDto.setSiteId("789");
    enquiryResponseDto.setSiteName("SiteName");
    enquiryResponseDto.setTelephoneNumber("123-456-7890");
    enquiryResponseDto.setTime("14:30");
    enquiryResponseDto.setTurnTimeMinutes(60);
    return enquiryResponseDto;

  }*/
  private Consent mockConsent(){
    Consent consent = new Consent();
    consent.setEmail(true);
    consent.setPhone(false);
    consent.setSms(true);
    consent.setPostal(false);
    consent.setPushNotification(true);
    consent.setProfiling(false);
    consent.setPrivacyStatement(true);
    consent.setConsentStatement(false);
    consent.setTermsAndConditions(true);
    return consent;
  }



}

