package uk.co.whitbread.domain.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.out.Attachments;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.availability.out.SoftBundleContent;
import uk.co.whitbread.domain.model.availability.out.SoftBundles;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackageCode;
import uk.co.whitbread.domain.model.packages.out.Packages;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.model.packages.out.SoftBundle;
import uk.co.whitbread.domain.model.packages.out.UpsellItems;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.infrastructure.config.SoftBundlesProperties;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;


@Slf4j
@Component
@RequiredArgsConstructor
public class SoftBundlesAndRatesLogic {

  private static final String SOFT_BUNDLE_RATE = "rate";
  private static final String SOFT_BUNDLE_ROOM_CLASS = "roomClass";
  private static final String EARLY_CHECK_IN_CODE = "HSCKIN";
  private static final String LATE_CHECK_OUT_CODE = "HSCOU2";
  private static final String ULTIMATE_WIFI_CODE = "FI24HR";
  private static final String PREMIER_PLUS_ROOM = "PP";
  private static final String PREMIER_WITH_A_VIEW = "PV";

  private final SoftBundlesProperties softBundlesProperties;

  void updateAvailabilityResponseWithSoftBundles(
      HotelAvailabilityRequest request,
      HotelAvailability response,
      PackagesResponse packagesResponse,
      MealsInfoResponse upsellItemsAndSoftBundles, ExtrasLabelDto ancillariesContent) {

    Map<String, Meal> mealsMap = getMealsMap(packagesResponse);
    Map<String, ExtrasDto> extraItemsMap = getExtrasDtoMap(packagesResponse);
    List<String> nonMealIds = softBundlesProperties.getNonMealPackageIds();
    Map<String, UpsellItems> upsellItemsMap = getUpsellItemsMap(upsellItemsAndSoftBundles);
    log.debug("Non meal package ids: {}", nonMealIds);

    HotelFlags hotelFlags = new HotelFlags(
            hotelHasEci(extraItemsMap),
            hotelHasLco(extraItemsMap),
            hotelHasWifi(extraItemsMap),
            hotelHasMealsAndRestaurantIsOpen(packagesResponse)
    );

    String softBundleParam = request.getSoftBundle();
    List<SoftBundle> bundlesFromAem = upsellItemsAndSoftBundles.getSoftBundles();

    for (SoftBundle aemSoftBundle : bundlesFromAem) {

      List<SoftBundleContent> softBundleContentList = buildSoftBundleContent(
              aemSoftBundle, nonMealIds, mealsMap,
              extraItemsMap, upsellItemsMap, hotelFlags, softBundleParam, response, ancillariesContent);

      if (softBundleContentList.isEmpty()) {
        continue;
      }

      if (SOFT_BUNDLE_RATE.equalsIgnoreCase(softBundleParam)) {
        attachBundleToRates(response, aemSoftBundle, softBundleContentList);
      } else if (SOFT_BUNDLE_ROOM_CLASS.equalsIgnoreCase(softBundleParam)) {
        attachBundleToRooms(response, aemSoftBundle, softBundleContentList);
      } else {
        log.warn("Unknown soft bundle parameter: {}", softBundleParam);
        return;
      }
    }
  }

  private List<SoftBundleContent> buildSoftBundleContent(
      SoftBundle aemSoftBundle, List<String> nonMealIds,
      Map<String, Meal> mealsMap, Map<String, ExtrasDto> extraItemsMap,
      Map<String, UpsellItems> upsellItemsMap,
      HotelFlags hotelFlags, String softBundleParam, HotelAvailability hotelAvailabilityResponse,
      ExtrasLabelDto ancillariesContent) {

    List<String> packageCodeIds = Optional.ofNullable(aemSoftBundle.getPackageCodes())
            .orElse(List.of())
            .stream()
            .map(PackageCode::getId)
            .filter(Objects::nonNull)
            .toList();

    boolean hotelHasEci = hotelFlags.hotelHasEci();
    boolean hotelHasLco = hotelFlags.hotelHasLco();
    boolean hotelHasWifi = hotelFlags.hotelHasWifi();
    boolean hotelHasMealsAndRestaurantIsOpen = hotelFlags.hotelHasMealsAndRestaurantIsOpen();

    boolean bundleHasMeal = packageCodeIds.stream().anyMatch(id ->
            !nonMealIds.contains(id));

    if (bundleHasMeal && !hotelHasMealsAndRestaurantIsOpen) {
      return List.of();
    }

    boolean bundleHasEciLco = packageCodeIds.contains(EARLY_CHECK_IN_CODE)
            || packageCodeIds.contains(LATE_CHECK_OUT_CODE);

    boolean bundleHasWifi = packageCodeIds.contains(ULTIMATE_WIFI_CODE);

    List<SoftBundleContent> result = new ArrayList<>();

    if (bundleHasMeal) {
      addMealsToBundle(aemSoftBundle, mealsMap, upsellItemsMap, result, nonMealIds);
    }
    if (bundleHasWifi) {
      addWifiToBundle(aemSoftBundle, extraItemsMap, hotelHasWifi, result,
          hotelAvailabilityResponse, ancillariesContent);
    }
    if (bundleHasEciLco) {
      int bundleSize = bundleSizeConsideringEciLcoAsOne(packageCodeIds);
      boolean hotelHasEciLco = hotelHasEci || hotelHasLco;

      if (SOFT_BUNDLE_RATE.equalsIgnoreCase(softBundleParam)
              && (bundleSize == 2 && !hotelHasEciLco)) {
        result.clear();
        return List.of();
      }
      addEciLcoToBundle(aemSoftBundle, packageCodeIds, extraItemsMap, !hotelHasEci,
          !hotelHasLco, result, ancillariesContent);
    }
    return result;
  }

  private static Map<String, Meal> getMealsMap(PackagesResponse packagesResponse) {
    return Optional.of(packagesResponse)
            .map(PackagesResponse::getPackages)
            .map(Packages::getMeals)
            .orElse(Collections.emptyList())
            .stream()
            .filter(Objects::nonNull)
            .filter(meal -> meal.getId() != null)
            .collect(Collectors.toMap(Meal::getId, Function.identity()));
  }

  private static Map<String, ExtrasDto> getExtrasDtoMap(PackagesResponse packagesResponse) {
    return Optional.of(packagesResponse)
            .map(PackagesResponse::getPackages)
            .map(Packages::getExtrasItems)
            .orElse(Collections.emptyList())
            .stream()
            .filter(Objects::nonNull)
            .filter(extraItem -> extraItem.getId() != null)
            .collect(Collectors.toMap(ExtrasDto::getId, Function.identity()));
  }

  private static Map<String, UpsellItems> getUpsellItemsMap(MealsInfoResponse mealsInfoResponse) {
    return Optional.ofNullable(mealsInfoResponse.getUpsellItems())
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .filter(upsell -> upsell.getCode() != null)
        .collect(Collectors.toMap(UpsellItems::getCode, Function.identity()));
  }

  private int bundleSizeConsideringEciLcoAsOne(List<String> packageCodeIds) {

    boolean hasEci = packageCodeIds.contains(EARLY_CHECK_IN_CODE);
    boolean hasLco = packageCodeIds.contains(LATE_CHECK_OUT_CODE);

    int eciLcoCount = (hasEci || hasLco) ? 1 : 0;

    long otherCount = packageCodeIds.stream()
            .filter(id -> !EARLY_CHECK_IN_CODE.equals(id))
            .filter(id -> !LATE_CHECK_OUT_CODE.equals(id))
            .count();

    return (int) otherCount + eciLcoCount;
  }

  private void addMealsToBundle(SoftBundle bundle, Map<String, Meal> mealsMap,
                                Map<String, UpsellItems> upsellItemsMap,
                                List<SoftBundleContent> result, List<String> nonMealIds) {
    if (mealsMap.isEmpty()) {
      return;
    }
    bundle.getPackageCodes().stream()
        .filter(Objects::nonNull)
        .map(PackageCode::getId)
        .filter(mealsMap::containsKey)
        .filter(id -> !nonMealIds.contains(id))
        .forEach(codeId -> addMealContent(bundle, codeId, mealsMap.get(codeId),
            upsellItemsMap.get(codeId), result));
  }

  private void addMealContent(SoftBundle bundle, String codeId, Meal meal,
                               UpsellItems upsellItems, List<SoftBundleContent> result) {
    var packageCode = retrievePackageCode(bundle, codeId);
    boolean packageCodeIncomplete = softBundleNotComplete(packageCode);

    String name;
    String description;
    String imageSrc;
    List<Attachments> attachments;

    if (packageCodeIncomplete && upsellItems != null) {
      name = upsellItems.getName();
      description = upsellItems.getDescription();
      imageSrc = resolveUpsellImageSrc(upsellItems);
      attachments = getSoftBundlesAttachments(upsellItems.getAttachments());
    } else if (packageCodeIncomplete) {
      name = meal.getName();
      description = meal.getIdDesc();
      imageSrc = meal.getIdImg();
      attachments = buildMealAttachments(meal);
    } else {
      name = packageCode.getName();
      description = packageCode.getDescription();
      imageSrc = packageCode.getImages().get(0);
      attachments = getSoftBundlesAttachments(packageCode.getAttachments());
    }

    result.add(SoftBundleContent.builder()
        .id(codeId)
        .name(name)
        .description(description)
        .attachments(attachments)
        .price(meal.getPrice())
        .imageSrc(imageSrc)
        .build());
  }

  private static String resolveUpsellImageSrc(UpsellItems upsellItems) {
    var images = upsellItems.getImages();
    return (images == null || images.isEmpty()) ? null : images.get(0);
  }

  private static List<Attachments> buildMealAttachments(Meal meal) {
    return List.of(Attachments.builder()
        .path(meal.getAllergyInfoSrc())
        .label(meal.getAllergyInfoSrc())
        .type(meal.getAllergyInfoSrc())
        .build());
  }

  private static boolean softBundleNotComplete(PackageCode packageCode) {
    return packageCode.getName() == null || packageCode.getName().isEmpty()
        || packageCode.getDescription() == null || packageCode.getDescription().isEmpty()
        || packageCode.getImages() == null || packageCode.getImages().isEmpty();
  }

  private static List<Attachments> getSoftBundlesAttachments(
          List<uk.co.whitbread.domain.model.packages.out.Attachments> packageAttachments) {
    if (packageAttachments != null && !packageAttachments.isEmpty()) {
      return packageAttachments
          .stream()
          .map(a -> Attachments.builder()
              .path(a.getPath())
              .label(a.getLabel())
              .type(a.getType())
              .build())
          .toList();
    }
    return Collections.emptyList();
  }

  private void addWifiToBundle(SoftBundle bundle, Map<String, ExtrasDto> extraItemsMap,
                               boolean hotelHasWifi, List<SoftBundleContent> result,
                               HotelAvailability hotelAvailabilityResponse, ExtrasLabelDto ancillariesContent) {
    if (!hotelHasWifi) {
      return;
    }
    ExtrasDto wifi = extraItemsMap.get(ULTIMATE_WIFI_CODE);
    if (wifi == null) {
      return;
    }
    int noNights = calculateNoNights(hotelAvailabilityResponse);
    var packageCode = retrievePackageCode(bundle, ULTIMATE_WIFI_CODE);
    var ancillariesWifi = findAncillariesExtras(ancillariesContent, ULTIMATE_WIFI_CODE);
    addWifiContent(wifi, packageCode, ancillariesWifi, noNights, result);
  }

  private void addWifiContent(ExtrasDto wifi, PackageCode packageCode,
                               uk.co.whitbread.hotel.content.generated.models.ExtrasDto ancillariesWifi,
                               int noNights, List<SoftBundleContent> result) {
    boolean packageCodeIncomplete = softBundleNotComplete(packageCode);

    String name;
    String description;
    String imageSrc;
    List<Attachments> attachments;

    if (packageCodeIncomplete && ancillariesWifi != null) {
      name = ancillariesWifi.getName();
      description = ancillariesWifi.getDescription();
      imageSrc = ancillariesWifi.getImageSrc();
      attachments = null;
    } else if (packageCodeIncomplete) {
      name = wifi.getName();
      description = wifi.getDescription();
      imageSrc = wifi.getImageSrc();
      attachments = null;
    } else {
      name = packageCode.getName();
      description = packageCode.getDescription();
      imageSrc = packageCode.getImages().get(0);
      attachments = getSoftBundlesAttachments(packageCode.getAttachments());
    }

    var wifiPerDay = Optional.ofNullable(wifi.getPrice()).orElse(BigDecimal.ZERO)
        .divide(BigDecimal.valueOf(noNights), RoundingMode.HALF_UP);

    result.add(SoftBundleContent.builder()
        .id(wifi.getId())
        .name(name)
        .price(wifiPerDay)
        .description(description)
        .imageSrc(imageSrc)
        .attachments(attachments)
        .build());
  }

  private static int calculateNoNights(HotelAvailability hotelAvailabilityResponse) {
    int noNights = Math.toIntExact(ChronoUnit.DAYS.between(
        LocalDate.parse(hotelAvailabilityResponse.getStartDate()),
        LocalDate.parse(hotelAvailabilityResponse.getEndDate())));
    return noNights <= 0 ? 1 : noNights;
  }

  private static uk.co.whitbread.hotel.content.generated.models.ExtrasDto findAncillariesExtras(
      ExtrasLabelDto ancillariesContent, String code) {
    if (ancillariesContent == null || ancillariesContent.getExtrasLabels() == null) {
      return null;
    }
    return ancillariesContent.getExtrasLabels().stream()
        .filter(e -> code.equals(e.getId()))
        .findFirst()
        .orElse(null);
  }

  private void addEciLcoToBundle(SoftBundle bundle, List<String> packageCodeIds,
                                 Map<String, ExtrasDto> extraItemsMap,
                                 boolean strikeEci,
                                 boolean strikeLco, List<SoftBundleContent> result, ExtrasLabelDto ancillariesContent) {
    if (packageCodeIds.contains(EARLY_CHECK_IN_CODE)) {
      attachEciLco(bundle, extraItemsMap, strikeEci, result, EARLY_CHECK_IN_CODE, ancillariesContent);
    }
    if (packageCodeIds.contains(LATE_CHECK_OUT_CODE)) {
      attachEciLco(bundle, extraItemsMap, strikeLco, result, LATE_CHECK_OUT_CODE, ancillariesContent);
    }
  }

  private void attachEciLco(SoftBundle bundle, Map<String, ExtrasDto> extraItemsMap,
                            boolean strikeThrough, List<SoftBundleContent> result, String codeId,
                            ExtrasLabelDto ancillariesContent) {
    ExtrasDto extrasDto = extraItemsMap.get(codeId);

    var packageCode = retrievePackageCode(bundle, codeId);
    if (packageCode != null) {
      var ancillariesData = findAncillariesExtras(ancillariesContent, codeId);

      boolean useExtrasFallback = softBundleNotComplete(packageCode) && ancillariesData != null;

      String name = useExtrasFallback ? ancillariesData.getName() : packageCode.getName();
      String description = useExtrasFallback ? ancillariesData.getDescription() : packageCode.getDescription();
      String imageSrc = useExtrasFallback ? ancillariesData.getImageSrc() : packageCode.getImages().get(0);
      List<Attachments> attachments = getSoftBundlesAttachments(packageCode.getAttachments());

      result.add(SoftBundleContent.builder()
              .id(codeId)
              .name(name)
              .price(extrasDto == null ? BigDecimal.ZERO : extrasDto.getPrice())
              .description(description)
              .imageSrc(imageSrc)
              .attachments(attachments)
              .strikeThrough(strikeThrough)
              .build());
    }

  }

  private static PackageCode retrievePackageCode(SoftBundle bundle, String codeId) {
    return bundle.getPackageCodes().stream()
            .filter(Objects::nonNull)
            .filter(packageCode -> codeId.equals(packageCode.getId()))
            .findFirst()
            .orElse(null);
  }

  private boolean hotelHasEci(Map<String, ExtrasDto> extraItemsMap) {
    return extraItemsMap.values().stream()
            .anyMatch(e ->
                    (EARLY_CHECK_IN_CODE.equals(e.getId()))
                            && (e.getAvailable() == null || e.getAvailable() > 0)
            );
  }

  private boolean hotelHasLco(Map<String, ExtrasDto> extraItemsMap) {
    return extraItemsMap.values().stream()
            .anyMatch(e ->
                    (LATE_CHECK_OUT_CODE.equals(e.getId()))
                            && (e.getAvailable() == null || e.getAvailable() > 0)
            );
  }

  private boolean hotelHasWifi(Map<String, ExtrasDto> extraItemsMap) {
    return extraItemsMap.values().stream()
            .anyMatch(e ->
                    ULTIMATE_WIFI_CODE.equals(e.getId())
                            && (e.getAvailable() == null || e.getAvailable() > 0)
            );
  }

  private boolean hotelHasMealsAndRestaurantIsOpen(PackagesResponse response) {
    return (response.getRestaurant() != null)
            && (!Boolean.TRUE.equals(response.getRestaurant().getRestaurantNotFound())
            && (!Boolean.TRUE.equals(response.getRestaurant().getNoMealsFound())));
  }

  private void attachBundleToRates(HotelAvailability response,
                                   SoftBundle aemSoftBundle, List<SoftBundleContent> bundleContentList) {
    List<String> bundleRates = aemSoftBundle.getRate();

    if (bundleRates != null && !bundleRates.isEmpty()) {
      response.getRoomRates().stream()
              .filter(roomRate -> bundleRates.contains(roomRate.getRatePlanCode()))
              .flatMap(roomRate -> roomRate.getRoomTypes().stream())
              .flatMap(typeInfo -> typeInfo.getRooms().stream())
              .filter(room -> room.getSoftBundles() == null)
              .forEach(room -> {
                List<SoftBundleContent> filteredBundle =
                        removeWifiIfPremierPlus(bundleContentList, room.getRoomClass());
                addBundleToRoom(room, filteredBundle, aemSoftBundle);
                priceCalculationsForSoftBundles(room, filteredBundle, aemSoftBundle, response);
              });
    }
  }

  private void addBundleToRoom(Room room, List<SoftBundleContent> filteredBundle, SoftBundle aemSoftBundle) {

    if (!filteredBundle.isEmpty()) {
      room.setSoftBundles(SoftBundles.builder()
              .softBundleContent(filteredBundle)
              .isOptional(aemSoftBundle.getOptional())
              .build());
    }
  }

  private void attachBundleToRooms(HotelAvailability response,
                                   SoftBundle aemSoftBundle, List<SoftBundleContent> bundleContentList) {
    List<String> bundleRoomClasses = aemSoftBundle.getRoomClass();

    if (bundleRoomClasses != null && !bundleRoomClasses.isEmpty()) {
      response.getRoomRates().stream()
              .flatMap(roomRate -> roomRate.getRoomTypes().stream())
              .flatMap(typeInfo -> typeInfo.getRooms().stream())
              .filter(room -> bundleRoomClasses.contains(room.getRoomClass()))
              .filter(room -> room.getSoftBundles() == null)
              .forEach(room -> {
                List<SoftBundleContent> filteredBundle =
                        removeWifiIfPremierPlus(bundleContentList, room.getRoomClass());
                addBundleToRoom(room, filteredBundle, aemSoftBundle);
                priceCalculationsForSoftBundles(room, filteredBundle, aemSoftBundle, response);
              });
    }
  }

  private List<SoftBundleContent> removeWifiIfPremierPlus(
          List<SoftBundleContent> original, String roomClass) {

    if (!isPremierPlusOrView(roomClass)) {
      return original;
    }

    return original.stream()
            .filter(code -> !ULTIMATE_WIFI_CODE.equals(code.getId()))
            .toList();
  }

  private boolean isPremierPlusOrView(String roomClass) {
    if (roomClass == null) {
      return false;
    }
    return roomClass.equalsIgnoreCase(PREMIER_PLUS_ROOM) || roomClass.equalsIgnoreCase(PREMIER_WITH_A_VIEW);
  }

  private void priceCalculationsForSoftBundles(
          Room room, List<SoftBundleContent> bundleContentList,
          SoftBundle aemSoftBundle, HotelAvailability response
  ) {
    if (Boolean.TRUE.equals(aemSoftBundle.getOptional())) {
      return;
    }

    List<String> nonMealIds = softBundlesProperties.getNonMealPackageIds();
    int noAdults = getNoAdults(response);
    int noNights = getNoNights(response);
    if (noNights <= 0) {
      noNights = 1;
    }

    var totalIncrement = BigDecimal.ZERO;
    var dailyIncrements = new BigDecimal[noNights];
    Arrays.fill(dailyIncrements, BigDecimal.ZERO);
    for (SoftBundleContent softBundleContent : bundleContentList) {
      if (softBundleContent.getStrikeThrough() != Boolean.TRUE) {
        totalIncrement = totalIncrement.add(
                calculatePriceIncrement(softBundleContent, nonMealIds, noAdults, noNights, dailyIncrements));
      }
    }
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    breakdown.setTotalNetAmount(breakdown.getTotalNetAmount().add(totalIncrement));

    for (int i = 0; i < noNights; i++) {
      var dailyPrice = breakdown.getDailyPrices().get(i);

      BigDecimal netPrice = dailyPrice.getNetPrice() != null ? dailyPrice.getNetPrice() : BigDecimal.ZERO;
      BigDecimal effectiveRate =
              dailyPrice.getEffectiveRate() != null ? dailyPrice.getEffectiveRate() : BigDecimal.ZERO;

      dailyPrice.setNetPrice(netPrice.add(dailyIncrements[i]));
      dailyPrice.setEffectiveRate(effectiveRate.add(dailyIncrements[i]));
    }
  }

  private BigDecimal calculatePriceIncrement(
          SoftBundleContent softBundleContent, List<String> nonMealIds,
          int noAdults, int noNights, BigDecimal[] dailyIncrements) {
    var price = softBundleContent.getPrice();
    String id = softBundleContent.getId();

    switch (id) {
      case ULTIMATE_WIFI_CODE: // price * nights
        var wifiTotal = price.multiply(BigDecimal.valueOf(noNights));
        for (int i = 0; i < noNights; i++) {
          dailyIncrements[i] = dailyIncrements[i].add(price);
        }
        return wifiTotal;
      case EARLY_CHECK_IN_CODE: // add price for first night only
        dailyIncrements[0] = dailyIncrements[0].add(price);
        return price;
      case LATE_CHECK_OUT_CODE: // add price for last night only
        dailyIncrements[noNights - 1] = dailyIncrements[noNights - 1].add(price);
        return price;
      default:
        // NON-MEAL generic (price × nights)
        if (nonMealIds.contains(id)) {
          return applyNonMealPerNightPricing(price, noNights, dailyIncrements);
        }
        // MEAL:  price * adults × nights
        return applyMealPricing(price, noAdults, noNights, dailyIncrements);
    }
  }

  private static Integer getNoAdults(HotelAvailability response) {
    return response.getRoomRates().stream()
            .flatMap(roomRate -> roomRate.getRoomTypes().stream())
            .map(RoomTypeInfo::getAdults)
            .filter(adults -> adults > 0)
            .findFirst()
            .orElse(1);
  }

  private static Integer getNoNights(HotelAvailability response) {
    LocalDate start = LocalDate.parse(response.getStartDate());
    LocalDate end = LocalDate.parse(response.getEndDate());
    return Integer.parseInt(String.valueOf(ChronoUnit.DAYS.between(start, end)));
  }

  private BigDecimal applyNonMealPerNightPricing(
          BigDecimal price, int noNights, BigDecimal[] dailyIncrements) {

    var total = price.multiply(BigDecimal.valueOf(noNights));

    for (int i = 0; i < noNights; i++) {
      dailyIncrements[i] = dailyIncrements[i].add(price);
    }
    return total;
  }

  private BigDecimal applyMealPricing(
          BigDecimal price, int noAdults, int noNights, BigDecimal[] dailyIncrements) {

    var mealDaily = price.multiply(BigDecimal.valueOf(noAdults));
    var mealTotal = mealDaily.multiply(BigDecimal.valueOf(noNights));

    for (int i = 0; i < noNights; i++) {
      dailyIncrements[i] = dailyIncrements[i].add(mealDaily);
    }
    return mealTotal;
  }

  private record HotelFlags(
          boolean hotelHasEci,
          boolean hotelHasLco,
          boolean hotelHasWifi,
          boolean hotelHasMealsAndRestaurantIsOpen
  ) {}
}