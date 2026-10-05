package uk.co.whitbread.reservation.infrastructure.rest.client.cdh;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.out.CdhBooker;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhSearchBookingsRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.mapper.CdhSearchBookingsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.CdhAdapterClient;

@ExtendWith(MockitoExtension.class)

class CdhSearchBookingOutPortImplTest {

  private static final String HOTEL_ID = "hotelId";
  @Mock
  private CdhSearchBookingsRequestMapper cdhSearchBookingsRequestMapper;
  @Mock
  private CdhAdapterClient cdhAdapterClient;

  @Mock
  private CdhSearchBookingsResponseMapper cdhSearchBookingsResponseMapper;
  @InjectMocks
  private CdhSearchBookingOutPortImpl cdhSearchBookingOutPort;
  
  private List<CdhResults> cdhResults;
  private CdhSearchBookingsResponse response;

  @BeforeEach
  void before() {
    cdhSearchBookingOutPort = new CdhSearchBookingOutPortImpl(cdhAdapterClient,
        cdhSearchBookingsRequestMapper,
        cdhSearchBookingsResponseMapper);
    cdhResults = new ArrayList<>();
    response = new CdhSearchBookingsResponse();
    
    LocalDate today = LocalDate.now();
    cdhResults.add(createCdhResult("abcdt", "Boyce", "CHECKEDIN",
        today.minusDays(1), today.plusDays(2), null));
    cdhResults.add(createCdhResult("abc", "Boyce", "UPCOMING",
        today.plusDays(5), today.plusDays(10), null));
    cdhResults.add(createCdhResult("test", "Boyce", "PAST",
        today.minusDays(10), today.minusDays(5), null));
    cdhResults.add(createCdhResult("abcef", "Boyce", "CANCELLED",
        today.minusDays(20), today.minusDays(15), today.minusDays(18)));
    
    response.setResults(cdhResults);
  }
  
  private CdhResults createCdhResult(String firstName, String lastName, String status, LocalDate arrivalDate,
                                     LocalDate departureDate, LocalDate cancellationDate) {
    return CdhResults.builder()
        .booker(CdhBooker.builder().firstName(firstName).lastName(lastName).build())
        .status(status)
        .arrivalDate(arrivalDate)
        .departureDate(departureDate)
        .cancellationDate(cancellationDate)
        .hotelId(HOTEL_ID)
        .build();
  }
  
  @Test
  void testCheckedInResultsAreSortedByArrivalDate() {
    List<CdhResults> sorted = cdhSearchBookingOutPort.getSortedResults(response, "CHECKEDIN", new ArrayList<>());
    
    assertThat(sorted).hasSize(1);
    assertThat(sorted.get(0).getStatus()).isEqualTo("CHECKEDIN");
  }
  
  @Test
  void testUpcomingResultsFilteredWithinOneYearAheadAndSorted() {
    List<CdhResults> sorted = cdhSearchBookingOutPort.getSortedResults(response, "UPCOMING", new ArrayList<>());
    
    assertThat(sorted).hasSize(1);
    assertThat(sorted.get(0).getStatus()).isEqualTo("UPCOMING");
    assertThat(sorted.get(0).getArrivalDate()).isAfterOrEqualTo(LocalDate.now());
  }
  
  @Test
  void testPastResultsWithinLastYearAndSortedByDepartureDescending() {
    List<CdhResults> sorted = cdhSearchBookingOutPort.getSortedResults(response, "PAST", new ArrayList<>());
    
    assertThat(sorted).hasSize(1);
    assertThat(sorted.get(0).getStatus()).isEqualTo("PAST");
    assertThat(sorted.get(0).getDepartureDate()).isBefore(LocalDate.now());
  }
  
  @Test
  void testCancelledResultsWithinLastYearSortedByArrivalDateDescending() {
    List<CdhResults> sorted = cdhSearchBookingOutPort.getSortedResults(response, "CANCELLED", new ArrayList<>());
    
    assertThat(sorted).hasSize(1);
    assertThat(sorted.get(0).getStatus()).isEqualTo("CANCELLED");
    assertThat(sorted.get(0).getCancellationDate()).isBefore(LocalDate.now());
  }

  @ParameterizedTest
  // first source follows opera confirmation number format, second does not
  @CsvSource(value = {"51511792,51511792", "A123456789,null"}, nullValues = {"null"})
  void searchBookingsFromCdh_success(String bookingReferenceIn, String operaConfNumberOut) {

    CdhReservationSearchCriteriaDto requestDto = buildCdhSearchCriteria(bookingReferenceIn);

    //Arrange
    when(cdhSearchBookingsRequestMapper.toDto(mockCdhSearchBookingsRequest(bookingReferenceIn)))
        .thenReturn(requestDto);
    when(cdhAdapterClient.searchReservation(any(CdhReservationSearchCriteriaDto.class)))
        .thenReturn(new CdhReservationSearchDto());
    when(cdhSearchBookingsResponseMapper.toModel(new CdhReservationSearchDto(), 10, 1,
        null, null)).thenReturn(mockCdhSearchBookingsResponse());

    //Act

    var bookings = cdhSearchBookingOutPort
        .searchBookingsFromCdh(mockCdhSearchBookingsRequest(bookingReferenceIn));

    //Assert
    verifyNoMoreInteractions(cdhAdapterClient);
    verifyNoMoreInteractions(cdhSearchBookingsResponseMapper);
    assertNotNull(bookings);

    assertEquals("Upcoming", bookings.getResults().stream()
        .filter(cdhResults -> cdhResults.getBooker().getFirstName().equalsIgnoreCase("abcdt")
            && cdhResults.getBooker().getLastName().equalsIgnoreCase("Boyce")).findFirst()
        .orElse(new CdhResults()).getStatus());
    assertEquals(operaConfNumberOut, bookings.getOperaConfNumber());
  }

  @ParameterizedTest
  @ValueSource(strings = {"12345678", "ABCDEF12345678", "abcdef12345678"})
  void searchBookingsFromCdh_operaUiRsv_success(String bookingReferenceIn) {

     CdhReservationSearchCriteriaDto requestDto = buildCdhSearchCriteria(bookingReferenceIn);

    //Arrange
    when(cdhSearchBookingsRequestMapper.toDto(mockCdhSearchBookingsRequest(bookingReferenceIn)))
            .thenReturn(requestDto);
    when(cdhAdapterClient.searchReservation(any(CdhReservationSearchCriteriaDto.class)))
            .thenReturn(new CdhReservationSearchDto());
    when(cdhSearchBookingsResponseMapper.toModel(new CdhReservationSearchDto(), 10, 1,
            null, null)).thenReturn(mockCdhSearchBookingsResponse());

    //Act

    var bookings = cdhSearchBookingOutPort
            .searchBookingsFromCdh(mockCdhSearchBookingsRequest(bookingReferenceIn));

    //Assert
    verifyNoMoreInteractions(cdhAdapterClient);
    verifyNoMoreInteractions(cdhSearchBookingsResponseMapper);
    assertNotNull(bookings);

    assertEquals("Upcoming", bookings.getResults().stream()
            .filter(cdhResults -> cdhResults.getBooker().getFirstName().equalsIgnoreCase("abcdt")
                    && cdhResults.getBooker().getLastName().equalsIgnoreCase("Boyce")).findFirst()
            .orElse(new CdhResults()).getStatus());
    assertEquals(bookings.getOperaConfNumber(), bookingReferenceIn);
  }


  @Test
  void setPaginationForCdhResults_success() {
    var request = CdhSearchBookingsRequest.builder()
        .pageNumber(1)
        .pageSize(10)
        .bookingsDatabaseSearch(false)
        .build();

   LocalDate today = LocalDate.now();
    var response = CdhSearchBookingsResponse.builder()
        .results(CdhResults.builder().status("Past")
            .departureDate(today.minusDays(240))
            .arrivalDate(today.minusDays(241))
            .booker(CdhBooker.builder().firstName("abc").lastName("Boyce").build()).hotelId("a").build())
        .results(CdhResults.builder()
            .booker(CdhBooker.builder().firstName("test").lastName("Boyce").build()).hotelId("b").build())
        .results(CdhResults.builder().status("Past")
            .departureDate(today.minusDays(341))
            .arrivalDate(today.minusDays(340))
            .booker(CdhBooker.builder().firstName("test2").lastName("Boyce").build()).hotelId("c").build())
        .results(CdhResults.builder().status("Undefined")
            .departureDate(LocalDate.parse("2023-03-10"))
            .arrivalDate(LocalDate.parse("2023-03-09"))
            .booker(CdhBooker.builder().firstName("abcef").lastName("Boyce").build()).hotelId("d").build())
        .results(CdhResults.builder().status("Upcoming")
            .departureDate(today.plusDays(3))
            .arrivalDate(today.plusDays(2))
            .booker(CdhBooker.builder().firstName("abcds").lastName("Boyce").build()).hotelId("e").build())
        .results(CdhResults.builder().status("Checked-In")
                .departureDate(LocalDate.parse("2025-10-18"))
                .arrivalDate(LocalDate.parse("2025-10-16"))
                .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("f").build())
        .results(CdhResults.builder().status("Upcoming")
            .departureDate(today.plusDays(32))
            .arrivalDate(today.plusDays(31))
            .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("g").build())
        .results(CdhResults.builder().status("Cancelled")
            .departureDate(LocalDate.parse("2024-03-10"))
            .arrivalDate(LocalDate.parse("2024-03-09"))
            .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("h").build())
        .results(CdhResults.builder().status("Cancelled")
            .departureDate(LocalDate.parse("2024-03-08"))
            .arrivalDate(LocalDate.parse("2024-03-07"))
            .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("i").build())
        .results(CdhResults.builder().status("Cancelled")
            .departureDate(LocalDate.parse("2024-06-10"))
            .arrivalDate(LocalDate.parse("2024-06-09"))
            .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("j").build())
        .results(CdhResults.builder().status("Cancelled")
            .departureDate(LocalDate.parse("2024-03-10"))
            .arrivalDate(LocalDate.parse("2024-03-09"))
            .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("k").build())
        .results(CdhResults.builder().status("Cancelled")
            .departureDate(LocalDate.parse("2024-03-10"))
            .arrivalDate(LocalDate.parse("2024-03-09"))
            .booker(CdhBooker.builder().firstName("abcsd").lastName("Boyce").build()).hotelId("l").build())
        .cdhSearchResults(12)
        .searchResults(10)
        .pageResults(12)
        .build();

    var actualResponse = cdhSearchBookingOutPort.setPaginationForCdhResults(request, response);

    assertEquals(6, actualResponse.size());

    Map<String, List<CdhResults>> groupedByStatus = actualResponse.stream()
        .collect(Collectors.groupingBy(r -> r.getStatus().toLowerCase()));
    
    if (groupedByStatus.containsKey("Checked-In")) {
      List<LocalDate> arrivals = groupedByStatus.get("Checked-In").stream()
          .map(CdhResults::getArrivalDate).toList();
      assertTrue(isSortedAscending(arrivals));
    }
    
    if (groupedByStatus.containsKey("Past")) {
      List<LocalDate> departures = groupedByStatus.get("Past").stream()
          .map(r -> Optional.ofNullable(r.getDepartureDate()).orElse(r.getArrivalDate()))
          .toList();
      assertTrue(isSortedDescending(departures));
    }
  }
  
  private boolean isSortedAscending(List<LocalDate> dates) {
    return IntStream.range(0, dates.size() - 1)
        .allMatch(i -> !dates.get(i).isAfter(dates.get(i + 1)));
  }
  
  private boolean isSortedDescending(List<LocalDate> dates) {
    return IntStream.range(0, dates.size() - 1)
        .allMatch(i -> !dates.get(i).isBefore(dates.get(i + 1)));
  }

  @Test
  void searchBookingsFromCdh_emptySearchResultList() {

    //Arrange
    var bookingReference = "";
    var requestDto = buildCdhSearchCriteria(bookingReference);
    requestDto.setBookingsDatabaseSearch(true);
    var searchRequest = mockCdhSearchBookingsRequest(bookingReference);
    var cdhReservationSearchDto = new CdhReservationSearchDto();
    when(cdhSearchBookingsRequestMapper.toDto(searchRequest)).thenReturn(requestDto);
    when(cdhAdapterClient.searchReservation(requestDto)).thenReturn(cdhReservationSearchDto);
    when(cdhSearchBookingsResponseMapper.toModel(cdhReservationSearchDto, 10, 1,
        null, null)).thenReturn(mockEmptyCdhSearchBookingsResponse());

    //Act
    var bookings = cdhSearchBookingOutPort.searchBookingsFromCdh(searchRequest);

    //Assert
    assertNotNull(bookings);
    assertEquals(0, bookings.getResults().size());
    assertEquals(0, bookings.getSearchResults());
    assertEquals(0, bookings.getPageResults());
  }

  private CdhReservationSearchCriteriaDto buildCdhSearchCriteria(String bookingReferenceIn) {
    CdhReservationSearchCriteriaDto requestDto = new CdhReservationSearchCriteriaDto();
    requestDto.setBookingReference(bookingReferenceIn);
    requestDto.setPageNumber(1);
    requestDto.setPageSize(10);
    requestDto.setBookingsDatabaseSearch(false);

    return requestDto;
  }

  private static CdhSearchBookingsRequest mockCdhSearchBookingsRequest(String bookingReferenceIn) {
    return CdhSearchBookingsRequest.builder()
        .bookingsDatabaseSearch(false)
        .pageSize(10)
        .pageNumber(1)
        .bookingReference(bookingReferenceIn)
        .build();
  }

  private static CdhSearchBookingsResponse mockCdhSearchBookingsResponse() {
    return CdhSearchBookingsResponse.builder()
        .results(CdhResults.builder().status("Past")
            .departureDate(LocalDate.parse("2024-02-23"))
            .arrivalDate(LocalDate.parse("2024-02-22"))
            .booker(CdhBooker.builder().firstName("abc").lastName("Boyce").build()).hotelId("a")
            .build())
        .results(CdhResults.builder().status("Past")
            .departureDate(LocalDate.parse("2024-02-23"))
            .arrivalDate(LocalDate.parse("2024-02-22"))
            .booker(CdhBooker.builder().firstName("test").lastName("Boyce").build()).hotelId("b")
            .build())
        .results(CdhResults.builder().status("Past")
            .departureDate(LocalDate.parse("2024-02-23"))
            .arrivalDate(LocalDate.parse("2024-02-22"))
            .booker(CdhBooker.builder().firstName("test2").lastName("Boyce").build()).hotelId("c")
            .build())
        .results(CdhResults.builder().status("Past")
            .departureDate(LocalDate.parse("2024-02-23"))
            .arrivalDate(LocalDate.parse("2024-02-22"))
            .booker(CdhBooker.builder().firstName("abcef").lastName("Boyce").build()).hotelId("d")
            .build())
        .results(CdhResults.builder().status("Upcoming")
            .departureDate(LocalDate.now().plusDays(1))
            .arrivalDate(LocalDate.now())
            .booker(CdhBooker.builder().firstName("abcdt").lastName("Boyce").build()).hotelId("e")
            .build())
        .cdhSearchResults(5)
        .searchResults(3)
        .pageResults(5)
        .build();
  }

  private static CdhSearchBookingsResponse mockEmptyCdhSearchBookingsResponse() {
    return CdhSearchBookingsResponse.builder()
        .results(Collections.emptyList())
        .cdhSearchResults(0)
        .searchResults(0)
        .pageResults(0)
        .build();
  }
}
