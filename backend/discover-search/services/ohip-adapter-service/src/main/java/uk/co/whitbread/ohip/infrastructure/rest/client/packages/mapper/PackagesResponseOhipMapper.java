package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelRestaurantType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.FunctionArgumentType;
import uk.co.whitbread.ohip.domain.model.packages.out.Meal;
import uk.co.whitbread.ohip.domain.model.packages.out.Packages;
import uk.co.whitbread.ohip.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.ohip.domain.model.packages.out.Restaurant;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.RestaurantsResponseOhipDto;

@Component
@RequiredArgsConstructor
public class PackagesResponseOhipMapper {

  private static final String LEISURE = "LEI";
  private static final String BUSINESS = "BUS";
  private static final String CITY_TAX_PACKAGE_CODE = "CITYTAX";
  private static final String PURPOSE_OF_STAY_FORMULA_FUNCTION_ARG = "IN_PURPOSEOFSTAY_STAY_LIST";

  private final MealResponseOhipMapper mealResponseOhipMapper;

  public PackagesResponse toModel(
      RestaurantsResponseOhipDto restaurantsResponseOhip,
      PackagesResponseOhipDto packagesResponseOhip,
      Map<String, String> packageToArticle) {
    AtomicReference<String> packagesCurrency = new AtomicReference<>();

    List<HotelRestaurantType> restaurants = restaurantsResponseOhip.getHotelConfigInfo()
        .getHotelRestaurants().stream().toList();

    List<Meal> meals = filterMeals(packagesResponseOhip, packagesCurrency);

    return PackagesResponse.builder()
        .packages(createPackages(meals, packageToArticle))
        .hotelHasCityTaxForLeisure(checkCityTaxForStay(packagesResponseOhip, LEISURE))
        .hotelHasCityTaxForBusiness(checkCityTaxForStay(packagesResponseOhip, BUSINESS))
        .restaurant(createRestaurantResponse(meals, restaurants))
        .build();
  }

  private List<Meal> filterMeals(PackagesResponseOhipDto packagesResponseOhip,
                                 AtomicReference<String> packagesCurrency) {
    return packagesResponseOhip.getPackageCodesList().getPackageCodes().stream()
            .flatMap(packageCode -> packageCode.getPackageCodeInfo()
                    .stream()
                    .map(packageCodeType ->
                            this.mealResponseOhipMapper.toModel(packageCodeType, packagesCurrency)))
            .toList();
  }

  private Restaurant createRestaurantResponse(List<Meal> meals, List<HotelRestaurantType> restaurants) {
    return Restaurant
            .builder()
            .restaurantNotFound(CollectionUtils.isEmpty(restaurants))
            .noMealsFound(CollectionUtils.isEmpty(meals))
            .build();
  }

  private Packages createPackages(List<Meal> meals,
                                  Map<String, String> packageToArticles) {

    List<Meal> enrichedMeals = meals.stream()
            .map(meal -> meal.toBuilder()
                    .inventoryItem(packageToArticles.get(meal.getId()))
                    .build()
            )
            .toList();

    return Packages.builder()
        .meals(enrichedMeals)
        .build();
  }


  private boolean checkCityTaxForStay(PackagesResponseOhipDto packagesResponseOhipDto,
      String purposeOfStay) {
    var packageCodeTypeOptional = packagesResponseOhipDto.getPackageCodesList()
        .getPackageCodes()
        .stream()
        .flatMap(packageCode -> packageCode.getPackageCodeInfo()
            .stream()
            .filter(
                packageCodeType ->
                    packageCodeType.getCode() != null
                        && packageCodeType.getCode().contains(CITY_TAX_PACKAGE_CODE))
        )
        .findFirst();

    return packageCodeTypeOptional
        .filter(packageType -> packageType.getHeader() != null
            && packageType.getHeader().getPostingAttributes() != null
            && packageType.getHeader().getPostingAttributes().getFormulaFunctionArguments() != null)
        .map(packageType -> packageType.getHeader().getPostingAttributes()
            .getFormulaFunctionArguments()
            .stream()
            .filter(functionArgumentType -> PURPOSE_OF_STAY_FORMULA_FUNCTION_ARG.equals(functionArgumentType.getName()))
            .map(FunctionArgumentType::getValue)
            .anyMatch(s -> s.contains(purposeOfStay)))
        .orElse(false);
  }

}
