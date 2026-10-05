package uk.co.whitbread.infrastructure.rest.client.groupBooking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper.GroupBookingRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper.GroupBookingRequestMapperImpl;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.in.GroupBookingRequestDynamicsDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GroupBookingRequestMapperImpl.class)
class GroupBookingRequestMapperTest {

  private final GroupBookingRequestMapper mapper = Mappers.getMapper(GroupBookingRequestMapper.class);

  private static GroupBookingRequest createGroupBookingRequest() {
    return GroupBookingRequest.builder()
        .language("en")
        .title("Mr")
        .firstName("John")
        .lastName("Doe")
        .emailAddress("john.doe@example.com")
        .phoneNumber("1234567890")
        .bookerType("Individual")
        .purposeOfStay("Business")
        .companyName("Test Company")
        .reasonForVisit("Conference")
        .reasonForVisitOther("Other Reason")
        .hotelName("Test Hotel")
        .hotelCode("TH123")
        .hotelBrand("Test Brand")
        .isPackageTypeMealDeal(true)
        .isPackageTypeBf(false)
        .arrivalDate("2023-10-01")
        .departureDate("2023-10-10")
        .isSchoolOrYouth(false)
        .singleOccupancy(5)
        .doubleOccupancy(5)
        .twinRooms(1)
        .familyOf21A1C(1)
        .familyOf31A2C(1)
        .familyOf32A1C(1)
        .familyOf42A2C(1)
        .accessibleSingle(0)
        .accessibleDouble(0)
        .accessibleTwin(0)
        .additionalInformation("Additional Info")
        .build();
  }

  private static Stream<Arguments> provideTestArguments() {
    return Stream.of(
        Arguments.of("de", "Webformular-Gruppenbuchungsanfrage", "Webform - DE"),
        Arguments.of("en", "Webform Group Booking Enquiry", "Webform - GB"),
        Arguments.of(null, "Webform Group Booking Enquiry", "Webform - GB"),
        Arguments.of("", "Webform Group Booking Enquiry", "Webform - GB"),
        Arguments.of("ZZ", "Webform Group Booking Enquiry", "Webform - GB")
    );
  }

  @ParameterizedTest
  @MethodSource("provideTestArguments")
  void testToDynamicsDto_language_en__ShouldReturnOK(String language, String expectedTitle, String expectedSource) {
    // Arrange
    GroupBookingRequest request = createGroupBookingRequest();
    request.setLanguage(language);

    // Act
    GroupBookingRequestDynamicsDto dto = mapper.toDynamicsDto(request);

    // Assert
    assertEquals(expectedTitle, dto.getTitle());
    assertEquals(expectedSource, dto.getEbecsWfsource());
    assertEquals("John", dto.getEbecsWffname());
    assertEquals("Doe", dto.getEbecsWflname());
    assertEquals("john.doe@example.com", dto.getEbecsWfemail());
    assertEquals("1234567890", dto.getEbecsWfphone());
    assertEquals("Individual", dto.getEbecsWftypeofbooker());
    assertEquals("Business", dto.getEbecsWfpurposeofstay());
    assertEquals("Test Company", dto.getEbecsWfcompanyname());
    assertEquals("Conference", dto.getEbecsWfreasonforvisit());
    assertEquals("Other Reason", dto.getEbecsWfreasonforvisitother());
    assertEquals("Test Hotel", dto.getEbecsWfhotelname());
    assertEquals("TH123", dto.getEbecsWfhotelcode());
    assertEquals("Test Brand", dto.getEbecsWfhotelbrand());
    assertEquals("true", dto.getEbecsWfpackagetypemealdeal());
    assertEquals("false", dto.getEbecsWfpackagetypebf());
    assertEquals("2023-10-01", dto.getEbecsWfarrivaldate());
    assertEquals("2023-10-10", dto.getEbecsWfdeparturedate());
    assertEquals("Mr", dto.getEbecsWftitle());
    assertEquals("/contacts(2fc6b1df-2814-ef11-9f89-000d3a4709b8)", dto.getCustomeridContactOdataBind());
    assertEquals(false, dto.getEbecsWfschoolyouthgroup());
    assertEquals(5, dto.getEbecsWfsingleoccupancy());
    assertEquals(5, dto.getEbecsWfdoubleoccupancy());
    assertEquals(1, dto.getEbecsWftwinrooms());
    assertEquals(1, dto.getEbecsWffamilyof2());
    assertEquals(1, dto.getEbecsWffamilyof31a());
    assertEquals(1, dto.getEbecsWffamilyof32a());
    assertEquals(1, dto.getEbecsWffamilyof4());
    assertEquals(0, dto.getEbecsWfaccessiblesingle());
    assertEquals(0, dto.getEbecsWfaccessibledouble());
    assertEquals(0, dto.getEbecsWfaccessibletwin());
    assertEquals("Additional Info", dto.getEbecsWfcomments());
  }
}