package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static mocks.HotelMock.buildAllHotels;
import static mocks.HotelMock.mockHotel;
import static mocks.HotelMock.mockRatePlan;
import static mocks.HotelMock.mockRoom;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mocks.HotelMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j

public class PaginationAndSortingByPriceTest {

  private PaginationAndSortingByPriceService paginationAndSortingByPriceSvc = new PaginationAndSortingByPriceService();

  @Test
  public void testSortByPriceWithRatePlans() {
    List<Hotel> hotels = HotelMock.buildHotelsWithMultiRates(Arrays.asList("LONLEI", "BASQUA", "COVCRO"));
    hotels.forEach(hotel -> {
      if (hotel.getHotelCode().equals("COVCRO")) {
        hotel.getRates().stream().forEach(ratePlan -> ratePlan.setTotalPrice(new Price(new BigDecimal(65.0), "GBP")));
      }
    });
    assertThat(paginationAndSortingByPriceSvc.sortHotelsByPrice(hotels)).extracting(Hotel::getHotelCode)
        .containsSequence("COVCRO", "LONLEI", "BASQUA");
  }

  @Test
  public void testSortByPriceWithRatePlanAmountIsNull() {
    List<Hotel> hotels = new ArrayList<>();
    List<Room> roomList = new ArrayList<>();
    List<RatePlan> ratePlansWithoutAmount = new ArrayList<>();
    List<RatePlan> ratePlans = new ArrayList<>();

    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP"), false));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP"), false));

    ratePlans.add(mockRatePlan("A", "A", roomList, new Price(new BigDecimal(100.00), "GBP")));
    ratePlans.add(mockRatePlan("F", "F", roomList, new Price(new BigDecimal(40.00), "GBP")));
    ratePlansWithoutAmount.add(mockRatePlan("C", "C", roomList, new Price(null, "GBP")));
    ratePlansWithoutAmount.add(mockRatePlan("B", "B", roomList, new Price(null, "GBP")));

    hotels.add(mockHotel("PLYPTI", ratePlansWithoutAmount));
    hotels.add(mockHotel("PLYLOC", ratePlans));

    List<RatePlan> expectedList = new ArrayList<>(ratePlans);
    expectedList.addAll(Collections.emptyList());

    List<Hotel> sortedHotels = paginationAndSortingByPriceSvc.sortHotelsByPrice(hotels);
    assertThat(sortedHotels).extracting(Hotel::getHotelCode).containsExactly("PLYLOC", "PLYPTI");
    assertThat(sortedHotels).extracting(Hotel::getRates).containsSequence(ratePlans, Collections.emptyList());
  }

  @Test
  public void testPage1Size5() {
    Assertions.assertThat(
            paginationAndSortingByPriceSvc.paginateHotels(SearchCriteria.builder().page(1).size(5).build(),
                buildAllHotels())).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI");
  }

  @Test
  public void testPage1And2() {
    List<Hotel> hotels = buildAllHotels();
    List<Hotel> pagedHotels1 = paginationAndSortingByPriceSvc.paginateHotels(
        SearchCriteria.builder().page(1).size(3).build(), hotels);
    Assertions.assertThat(pagedHotels1).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("PLYPTI", "PLYLOC", "PLYMAR");

    List<Hotel> pagedHotels2 = paginationAndSortingByPriceSvc.paginateHotels(
        SearchCriteria.builder().page(2).size(3).build(), hotels);
    Assertions.assertThat(pagedHotels2).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("LISBAR", "PAIWHI");
  }

  @Test
  public void testEmptyPage() {
    Assertions.assertThat(
        paginationAndSortingByPriceSvc.paginateHotels(SearchCriteria.builder().page(1).size(5).build(),
            Collections.emptyList())).isEmpty();
  }

}
