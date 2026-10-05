package uk.co.whitbread.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackageDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.InventoryAvailabilityDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MealDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RestaurantDto;
import uk.co.whitbread.infrastructure.rest.client.availability.model.ItemInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.ohip.exceptions.OhipClientException;
import uk.co.whitbread.infrastructure.rest.client.packages.model.DonationPackagesRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.packages.model.PackagesRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PackagesOhipClientTest {

  private static final String HOTEL_ID = "TKINPT";
  private static final String START_DATE = "2022-03-01";
  private static final String END_DATE = "2022-03-03";
  public static final String ITEM_CODE = "ECI";
  public static final int ADULTS = 2;
  public static final int CHILDREN = 0;
  public static final int NR_NIGHTS = 2;
  public static final String TITLE = "Premier Inn Breakfast";
  public static final String ID = "BFADBF";
  public static final String CURRENCY = "EUR";
  public static final BigDecimal PRICE = BigDecimal.valueOf(10L);
  public static final String OHIP_ADAPTER_ERROR_MESSAGE = "An error was returned by OHIP Adapter !!";

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @InjectMocks
  private OhipClient ohipClient;

  @Test
  void getPackages__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PackagesResponseDto.class)).thenReturn(mockPackagesResponseDto());

    var packagesRequestOhip = mockPackagesRequestOhip();

    //Act
    var packagesResponse = this.ohipClient.getPackages(packagesRequestOhip);

    //Assert
    assertThat(packagesResponse, notNullValue());

    var restaurant = packagesResponse.getRestaurant();
    assertThat(restaurant, notNullValue());

    var meal = packagesResponse.getPackages().getMeals().get(0);
    assertThat(meal.getTitle(), is(TITLE));
    assertThat(meal.getId(), is(ID));
    assertThat(meal.getPrice(), is(PRICE));
    assertThat(meal.getCurrency(), is(CURRENCY));

    assertFalse(packagesResponse.getHotelHasCityTaxForBusiness());
    assertTrue(packagesResponse.getHotelHasCityTaxForLeisure());
  }

  @Test
  void getPackages__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(PackagesResponseDto.class))
            .thenReturn(Mono.error(new OhipClientException("message",
                    OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getPackages(mockPackagesRequestOhip()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getDonationsPackagesDetails__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DonationPackagesResponseDto.class)).thenReturn(mockDonationsPackagesResponseDto());

    var donationPackagesRequestOhip = mockDonationPackagesRequestOhip();

    //Act
    var donationPackagesResponse = this.ohipClient.getHotelCharityPackagesDetails(
        donationPackagesRequestOhip);

    //Assert
    assertThat(donationPackagesResponse, notNullValue());
    assertThat(donationPackagesResponse.getDonationPackages().get(0).getCode(), is("CHRTY3"));
    assertThat(donationPackagesResponse.getDonationPackages().get(1).getCurrency(), is("GBP"));
    assertThat(donationPackagesResponse.getDonationPackages().get(2).getUnitPrice(),
        is(BigDecimal.ONE));
  }

  @Test
  void getHotelCharityPackagesDetails__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(DonationPackagesResponseDto.class))
            .thenReturn(Mono.error(new OhipClientException("message",
                    OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
        () -> ohipClient.getHotelCharityPackagesDetails(mockDonationPackagesRequestOhip()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getItemsInventory__shouldReturnOk() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ItemInventoryResponseDto.class)).thenReturn(mockItemInventoryResponseDto());

    // Act
    ItemInventoryResponseDto response = ohipClient.getItemInventory(
            createItemInventoryRequest());

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getItemsInventory(), hasSize(1));
    assertThat(response.getItemsInventory().get(0).getInventories().get(0).getAvailable(), is(10));
    assertThat(response.getItemsInventory().get(0).getInventories().get(0).getDate(), is(START_DATE));
    assertThat(response.getItemsInventory().get(0).getCode(), is(ITEM_CODE));

  }


  @Test
  void getItemsInventory__shouldReturnException() {

    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(ItemInventoryResponseDto.class))
        .thenReturn(Mono.error(new OhipClientException("message",
            OHIP_ADAPTER_ERROR_MESSAGE, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(OhipClientException.class,
            () -> ohipClient.getItemInventory(createItemInventoryRequest()));

    //Assert
    String debugMessage = thrownException.getDebugMessage();
    Assertions.assertEquals(OHIP_ADAPTER_ERROR_MESSAGE, debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  private Mono<PackagesResponseDto> mockPackagesResponseDto() {

    var restaurant = new RestaurantDto();
    restaurant.setRestaurantNotFound(false);

    var meal = new MealDto();
    meal.setTitle(TITLE);
    meal.setId(ID);
    meal.setPrice(PRICE);
    meal.setCurrency(CURRENCY);

    var packages = new PackagesDto();
    packages.setMeals(Collections.singletonList(meal));

    var packagesResponseDto = new PackagesResponseDto();
    packagesResponseDto.setPackages(packages);
    packagesResponseDto.setRestaurant(restaurant);
    packagesResponseDto.setHotelHasCityTaxForBusiness(Boolean.FALSE);
    packagesResponseDto.setHotelHasCityTaxForLeisure(Boolean.TRUE);

    return Mono.just(packagesResponseDto);
  }

  private PackagesRequestOhipDto mockPackagesRequestOhip() {
    return PackagesRequestOhipDto
        .builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .nightsNumber(NR_NIGHTS)
        .build();
  }

  private Mono<DonationPackagesResponseDto> mockDonationsPackagesResponseDto() {

    var donationPackagesOhip = new DonationPackagesResponseDto();
    donationPackagesOhip.setDonationPackages(
        List.of(mockDonationPackageOhip("CHRTY3", BigDecimal.valueOf(5), "GBP"),
            mockDonationPackageOhip("CHRTY4", BigDecimal.valueOf(3), "GBP"),
            mockDonationPackageOhip("CHRTY5", BigDecimal.valueOf(1), "GBP"))
    );
    return Mono.just(donationPackagesOhip);
  }

  private DonationPackageDto mockDonationPackageOhip(String code, BigDecimal unitPrice,
      String currency) {
    var donationPackageDetails = new DonationPackageDto();
    donationPackageDetails.setCode(code);
    donationPackageDetails.currency(currency);
    donationPackageDetails.unitPrice(unitPrice);
    return donationPackageDetails;
  }

  private DonationPackagesRequestOhipDto mockDonationPackagesRequestOhip() {
    return DonationPackagesRequestOhipDto.builder()
        .hotelId(HOTEL_ID)
        .packageCodes(List.of("CHRTY3", "CHRTY4", "CHRTY5"))
        .build();
  }

  private ItemInventoryRequestOhipDto createItemInventoryRequest() {
    return ItemInventoryRequestOhipDto.builder().hotelId("MANOLD").startDate(START_DATE)
            .endDate(END_DATE).build();
  }

  private Mono<ItemInventoryResponseDto> mockItemInventoryResponseDto() {

    var inventoryAvailability = new InventoryAvailabilityDto();
    inventoryAvailability.setAvailable(10);
    inventoryAvailability.setDate(START_DATE);

    ItemInventoryDto item = new ItemInventoryDto();
    item.setInventories(List.of(inventoryAvailability));
    item.setCode(ITEM_CODE);

    var response = new ItemInventoryResponseDto();
    response.setItemsInventory(List.of(item));

    return Mono.just(response);
  }
}
