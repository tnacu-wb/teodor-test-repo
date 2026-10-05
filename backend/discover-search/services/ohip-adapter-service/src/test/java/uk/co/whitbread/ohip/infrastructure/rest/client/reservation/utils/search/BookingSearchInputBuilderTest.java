package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.ARRIVAL_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_COMMUNICATION_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_PROFILE_NAME_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BOOKER_PROFILE_TYPE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CANCELLATION_DATE_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CONTACT_PROFILE_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REFERENCE_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOTEL_ID_PARAM;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;

@ExtendWith(MockitoExtension.class)
class BookingSearchInputBuilderTest {

  @Test
  void buildSearchInput_ByBookingReference_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .bookingReference("TestBookingReference")
        .bookerLastName("TestBookerLastName")
        .guestLastName("TestGuestLastName")
        .bookerPostcode("TestBookerPostcode")
        .hotelId("TestHotelId")
        .bookerEmail("TestBookerEmail")
        .bookerPhone("TestBookerPhone")
        .arrivalDate("TestArrivalDate")
        .cancellationDate("TestCancellationDate")
        .companyName("TestCompanyName")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.RESERVATION_SEARCH, result.getSearchBookingsType());

    assertEquals(3, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(EXTERNAL_REFERENCE_IDS_PARAM));
    assertTrue(result.getSearchFields().containsKey(ARRIVAL_DATE_PARAM));
    assertTrue(result.getSearchFields().containsKey(CANCELLATION_DATE_PARAM));

    assertEquals("TestArrivalDate", result.getSearchFields().get(ARRIVAL_DATE_PARAM).get(0));
    assertEquals("TestCancellationDate", result.getSearchFields().get(CANCELLATION_DATE_PARAM).get(0));
    assertEquals("TestBookingReference", result.getSearchFields().get(EXTERNAL_REFERENCE_IDS_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByBookerEmail_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .guestLastName("TestGuestLastName")
        .hotelId("TestHotelId")
        .bookerEmail("TestBookerEmail")
        .arrivalDate("TestArrivalDate")
        .cancellationDate("TestCancellationDate")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.BOOKER_SEARCH, result.getSearchBookingsType());

    assertEquals(2, result.getSearchFields().entrySet().size());

    assertTrue(result.getSearchFields().containsKey(BOOKER_COMMUNICATION_PARAM));
    assertTrue(result.getSearchFields().containsKey(BOOKER_PROFILE_TYPE_PARAM));

    assertEquals("TestBookerEmail", result.getSearchFields().get(BOOKER_COMMUNICATION_PARAM).get(0));
    assertEquals(CONTACT_PROFILE_TYPE, result.getSearchFields().get(BOOKER_PROFILE_TYPE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByBookerPhone_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .guestLastName("TestGuestLastName")
        .hotelId("TestHotelId")
        .bookerPhone("TestBookerPhone")
        .arrivalDate("TestArrivalDate")
        .cancellationDate("TestCancellationDate")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.BOOKER_SEARCH, result.getSearchBookingsType());

    assertEquals(2, result.getSearchFields().entrySet().size());

    assertTrue(result.getSearchFields().containsKey(BOOKER_COMMUNICATION_PARAM));
    assertTrue(result.getSearchFields().containsKey(BOOKER_PROFILE_TYPE_PARAM));

    assertEquals("TestBookerPhone", result.getSearchFields().get(BOOKER_COMMUNICATION_PARAM).get(0));
    assertEquals(CONTACT_PROFILE_TYPE, result.getSearchFields().get(BOOKER_PROFILE_TYPE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByCompanyName_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .guestLastName("TestGuestLastName")
        .hotelId("TestHotelId")
        .companyName("TestCompanyName")
        .arrivalDate("TestArrivalDate")
        .cancellationDate("TestCancellationDate")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.BOOKER_SEARCH, result.getSearchBookingsType());

    assertEquals(2, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(BOOKER_PROFILE_NAME_PARAM));
    assertTrue(result.getSearchFields().containsKey(BOOKER_PROFILE_TYPE_PARAM));

    assertEquals("TestCompanyName", result.getSearchFields().get(BOOKER_PROFILE_NAME_PARAM).get(0));
    assertEquals(CONTACT_PROFILE_TYPE, result.getSearchFields().get(BOOKER_PROFILE_TYPE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByHotelId_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .bookerLastName("TestBookerLastName")
        .guestLastName("TestGuestLastName")
        .hotelId("TestHotelId")
        .arrivalDate("TestArrivalDate")
        .cancellationDate("TestCancellationDate")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.HOTEL_SEARCH, result.getSearchBookingsType());

    assertEquals(3, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(HOTEL_ID_PARAM));
    assertTrue(result.getSearchFields().containsKey(ARRIVAL_DATE_PARAM));
    assertTrue(result.getSearchFields().containsKey(CANCELLATION_DATE_PARAM));

    assertEquals("TestHotelId", result.getSearchFields().get(HOTEL_ID_PARAM).get(0));
    assertEquals("TestArrivalDate", result.getSearchFields().get(ARRIVAL_DATE_PARAM).get(0));
    assertEquals("TestCancellationDate", result.getSearchFields().get(CANCELLATION_DATE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByArrivalDate_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .bookerLastName("TestBookerLastName")
        .guestLastName("TestGuestLastName")
        .arrivalDate("TestArrivalDate")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.RESERVATION_SEARCH, result.getSearchBookingsType());

    assertEquals(1, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(ARRIVAL_DATE_PARAM));

    assertEquals("TestArrivalDate", result.getSearchFields().get(ARRIVAL_DATE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByCancelationDate_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .bookerLastName("TestBookerLastName")
        .guestLastName("TestGuestLastName")
        .cancellationDate("TestCancellationDate")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.RESERVATION_SEARCH, result.getSearchBookingsType());

    assertEquals(1, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(CANCELLATION_DATE_PARAM));

    assertEquals("TestCancellationDate", result.getSearchFields().get(CANCELLATION_DATE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByThirdParty_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .bookerLastName("TestBookerLastName")
        .guestLastName("TestGuestLastName")
        .bookerPostcode("TestBookerPostcode")
        .hotelId("TestHotelId")
        .bookerEmail("TestBookerEmail")
        .bookerPhone("TestBookerPhone")
        .arrivalDate("TestArrivalDate")
        .cancellationDate("TestCancellationDate")
        .companyName("TestCompanyName")
        .thirdPartyBookingReferenceNumber("TestThirdPartyBookingReferenceNumber")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.RESERVATION_SEARCH, result.getSearchBookingsType());

    assertEquals(3, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(EXTERNAL_REFERENCE_IDS_PARAM));
    assertTrue(result.getSearchFields().containsKey(ARRIVAL_DATE_PARAM));
    assertTrue(result.getSearchFields().containsKey(CANCELLATION_DATE_PARAM));

    assertEquals("TestArrivalDate", result.getSearchFields().get(ARRIVAL_DATE_PARAM).get(0));
    assertEquals("TestCancellationDate", result.getSearchFields().get(CANCELLATION_DATE_PARAM).get(0));
    assertEquals("TestThirdPartyBookingReferenceNumber",
        result.getSearchFields().get(EXTERNAL_REFERENCE_IDS_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }

  @Test
  void buildSearchInput_ByBookerLastName_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder()
        .bookerLastName("TestBookerLastName")
        .guestLastName("TestGuestLastName")
        .build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.BOOKER_SEARCH, result.getSearchBookingsType());

    assertEquals(2, result.getSearchFields().entrySet().size());
    assertTrue(result.getSearchFields().containsKey(BOOKER_PROFILE_NAME_PARAM));
    assertTrue(result.getSearchFields().containsKey(BOOKER_PROFILE_TYPE_PARAM));

    assertEquals("TestBookerLastName", result.getSearchFields().get(BOOKER_PROFILE_NAME_PARAM).get(0));
    assertEquals(CONTACT_PROFILE_TYPE, result.getSearchFields().get(BOOKER_PROFILE_TYPE_PARAM).get(0));

    assertThat(result.getFilterChain()).isNotNull();
  }


  @Test
  void buildSearchInput_ByNullObject_ShouldReturnOK() {
    //Arrange
    var bookingSearchCriteria = BookingSearchCriteria.builder().build();
    //Act
    var result = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);
    //Assert
    assertEquals(SearchBookingsType.INVALID, result.getSearchBookingsType());

    assertEquals(0, result.getSearchFields().entrySet().size());

    assertThat(result.getFilterChain()).isNull();
  }
}