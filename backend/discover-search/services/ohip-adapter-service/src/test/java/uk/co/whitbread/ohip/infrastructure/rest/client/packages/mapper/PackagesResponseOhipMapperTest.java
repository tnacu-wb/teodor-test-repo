package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackagesInfoPackageCodesList;
import uk.co.whitbread.ohip.domain.model.packages.out.Meal;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.RestaurantsResponseOhipDto;

@ExtendWith(MockitoExtension.class)
public class PackagesResponseOhipMapperTest {

  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

  private PackagesResponseOhipMapper packagesResponseOhipMapper;

  @Mock
  private MealResponseOhipMapper mealResponseOhipMapper;

  @BeforeEach
  public void init() {
    packagesResponseOhipMapper = new PackagesResponseOhipMapper(mealResponseOhipMapper);
  }


  @Test
  void toPackagesResponse__ShouldReturnOK() throws IOException {
    //Act
    when(mealResponseOhipMapper.toModel(any(), any())).thenReturn(mockMealShouldReturnOK());
    var packagesResponse = packagesResponseOhipMapper
        .toModel(createRestaurantsResponseOhip(), createPackagesResponseOhip(), packageToArticles);

    //Assert
    assertNotNull(packagesResponse);
    assertNotNull(packagesResponse.getPackages());

    var meals = packagesResponse.getPackages().getMeals();
    assertNotNull(meals);

    var testMeal = meals.get(0);
    assertEquals("BKFSTTEST", testMeal.getId());
    assertEquals("Premier Inn Breakfast", testMeal.getTitle());
    assertEquals(BigDecimal.valueOf(10L), testMeal.getPrice());
    assertEquals("GBP", testMeal.getCurrency());

    var restaurant = packagesResponse.getRestaurant();
    assertNotNull(restaurant);
    assertEquals(Boolean.FALSE, restaurant.getRestaurantNotFound());
    assertEquals(Boolean.FALSE, restaurant.getNoMealsFound());

  }

  @Test
  void toPackagesResponse__ShouldReturnNullCurrency() throws IOException {
    //Act
    when(mealResponseOhipMapper.toModel(any(), any())).thenReturn(mockShouldReturnNullCurrency());
    var packagesResponse = packagesResponseOhipMapper
        .toModel(createRestaurantsResponseOhip(), createPackagesResponseOhipWithoutCurrency(), packageToArticles);

    //Assert
    assertNotNull(packagesResponse);
    assertNotNull(packagesResponse.getPackages());

    var meals = packagesResponse.getPackages().getMeals();
    assertNotNull(meals);

    var testMeal = meals.get(0);
    assertEquals("BKFSTTEST", testMeal.getId());
    assertEquals("Premier Inn Breakfast", testMeal.getTitle());
    assertEquals(BigDecimal.valueOf(10L), testMeal.getPrice());
    assertNull(testMeal.getCurrency());

  }

  @Test
  void toPackageResponse__ShouldReturnNoRestaurants() throws IOException {
    //Act
    when(mealResponseOhipMapper.toModel(any(), any())).thenReturn(mockMealShouldReturnOK());
    var packagesResponse = packagesResponseOhipMapper
        .toModel(createRestaurantsResponseOhipNoRestaurants(), createPackagesResponseOhip(), packageToArticles);

    //Assert
    assertNotNull(packagesResponse);
    assertNotNull(packagesResponse.getPackages());
    assertEquals(Boolean.FALSE, packagesResponse.getHotelHasCityTaxForLeisure());
    assertEquals(Boolean.FALSE, packagesResponse.getHotelHasCityTaxForBusiness());

    var meals = packagesResponse.getPackages().getMeals();
    assertNotNull(meals);

    var testMeal = meals.get(0);
    assertEquals("BKFSTTEST", testMeal.getId());
    assertEquals("Premier Inn Breakfast", testMeal.getTitle());
    assertEquals(BigDecimal.valueOf(10L), testMeal.getPrice());
    assertEquals("GBP", testMeal.getCurrency());

    var restaurant = packagesResponse.getRestaurant();
    assertNotNull(restaurant);
    assertEquals(Boolean.TRUE, restaurant.getRestaurantNotFound());
    assertEquals(Boolean.FALSE, restaurant.getNoMealsFound());
  }

  @ParameterizedTest
  @CsvSource({
      "__files/ohip_packageCodesListCityTaxLeisure_object.json,true,false",
      "__files/ohip_packageCodesListCityTaxBusiness_object.json,true,true",
      "__files/ohip_packageCodesList_object.json,false,false",
      "__files/ohip_packageCodesList_EmptyFormula.json,false,false"
  })
  void toPackageResponse__ShouldReturnCityTaxForLeisureOrBusiness(String fileName, boolean expectedLeisure, boolean expectedBusiness) throws IOException {
    // Arrange
    when(mealResponseOhipMapper.toModel(any(), any())).thenReturn(mockMealShouldReturnOK());
    PackagesResponseOhipDto packagesResponseOhipDto = PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(
            PackagesResponseOhipMapperTest.class.getClassLoader().getResource(fileName),
            PackagesInfoPackageCodesList.class))
        .build();
    // Act
    var packagesResponse = packagesResponseOhipMapper.toModel(createRestaurantsResponseOhip(), packagesResponseOhipDto, packageToArticles);
    // Assert
    assertNotNull(packagesResponse);
    assertEquals(expectedLeisure, packagesResponse.getHotelHasCityTaxForLeisure());
    assertEquals(expectedBusiness, packagesResponse.getHotelHasCityTaxForBusiness());
  }

  private static PackagesResponseOhipDto createPackagesResponseOhip() throws IOException {
    return PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(
            PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource("__files/ohip_packageCodesList_object.json"),
            PackagesInfoPackageCodesList.class))
        .build();
  }

  private static PackagesResponseOhipDto createPackagesResponseOhipWithoutCurrency()
      throws IOException {
    PackagesResponseOhipDto response = PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(
            PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource("__files/ohip_packageCodesList_object.json"),
            PackagesInfoPackageCodesList.class))
        .build();
    response.getPackageCodesList().getPackageCodes().get(0).getPackageCodeInfo().get(0).getHeader()
        .getTransactionDetails().setCurrency(null);
    return response;
  }

  private static RestaurantsResponseOhipDto createRestaurantsResponseOhip() throws IOException {
    return RestaurantsResponseOhipDto.builder()
        .hotelConfigInfo(mapper.readValue(
            PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource("__files/ohip_hotelConfigInfo_object.json"),
            HotelInfoType.class))
        .build();
  }

  private static RestaurantsResponseOhipDto createRestaurantsResponseOhipNoRestaurants()
      throws IOException {
    return RestaurantsResponseOhipDto.builder()
        .hotelConfigInfo(mapper.readValue(
            PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource("__files/ohip_hotelConfigInfo_NoRestaurants_object.json"),
            HotelInfoType.class))
        .build();
  }

  private static Meal mockMealShouldReturnOK() {
    return Meal.builder()
        .price(BigDecimal.valueOf(10L))
        .id("BKFSTTEST")
        .currency("GBP")
        .title("Premier Inn Breakfast")
        .build();
  }

  private static Meal mockShouldReturnNullCurrency() {
    return Meal.builder()
        .price(BigDecimal.valueOf(10L))
        .id("BKFSTTEST")
        .currency(null)
        .title("Premier Inn Breakfast")
        .build();
  }
  Map<String, String> packageToArticles = Map.of(
          "BKFSTTEST", "ECI"
  );

}
