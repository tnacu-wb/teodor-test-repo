package uk.co.whitbread.infrastructure.rest.client.packages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.domain.logic.CityTaxUtils;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.packages.in.CutOffMinute;
import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.domain.model.packages.in.HotelInformationExtended;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.CutOffExtras;
import uk.co.whitbread.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.CutOffOutPort;
import uk.co.whitbread.domain.ports.secondary.PackagesOutPort;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.content.generated.models.UpsellItemsDto;
import uk.co.whitbread.hotel.content.generated.models.UpsellItemsExtraDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.InventoryAvailabilityDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryResponseDto;
import uk.co.whitbread.infrastructure.config.PackagesProperties;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.availability.model.ItemInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.content.ContentServiceClient;
import uk.co.whitbread.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.DonationPackagesRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.DonationPackagesResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.ExtrasMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.HotelInformationExtendedMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.MealsInfoResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.PackagesResponseMapper;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class PackagesOutPortImpl implements PackagesOutPort {

  private static final String PI_CHANNEL = "PI";
  private static final String BB_CHANNEL = "BB";
  private static final String CCUI_CHANNEL = "CCUI";
  private static final String EMPLOYEE_CHANNEL = "EMPLOYEE";
  private static final String HSCKIN = "HSCKIN";

  private record ExtrasContext(
      PackagesRequest request,
      Map<String, UpsellItemsDto> upsellExtrasMap,
      Map<String, String> packageToInventoryCodeMap,
      Map<String, uk.co.whitbread.hotel.content.generated.models.ExtrasDto> ancExtrasMap,
      Map<String, List<InventoryAvailabilityDto>> inventoryMap,
      CutOffExtras cutOff) {
  }

  private final OhipClient ohipClient;
  private final ContentServiceClient contentServiceClient;
  private final PackagesResponseMapper packagesResponseMapper;
  private final PackagesRequestMapper packagesRequestMapper;
  private final DonationPackagesRequestMapper donationPackagesRequestMapper;
  private final DonationPackagesResponseMapper donationPackagesResponseMapper;
  private final MealsInfoResponseMapper mealsInfoResponseMapper;
  private final ExtrasMapper extrasMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final PackagesProperties packagesProperties;
  private final ContentServiceOutPort contentServiceOutPort;
  private final CutOffOutPort cutOffOutPort;
  private final HotelInformationExtendedMapper hotelInformationExtendedMapper;

  @Override
  public PackagesResponse getPackages(PackagesRequest packagesRequest) {
    Objects.requireNonNull(packagesRequest, "packagesRequest must not be null");
    log.debug(
            "Entered getPackages with packagesRequest={}", packagesRequest);
    var packagesRequestOhipDto = packagesRequestMapper.toOhipDto(packagesRequest);
    var packagesResponse = packagesResponseMapper.toModel(ohipClient.getPackages(packagesRequestOhipDto));

    ExtrasLabelDto ancillariesContent = new ExtrasLabelDto();
    if (packagesRequest.getCountry() != null && packagesRequest.getLanguage() != null) {
      ancillariesContent = contentServiceClient.getExtrasLabels(
              packagesRequest.getCountry(),
              packagesRequest.getLanguage());
    }

    if (StringUtils.isNotBlank(packagesRequest.getPackageSelections())) {
      initialiseMeals(packagesResponse.getPackages().getMeals());
    }

    if (hasFreeMealPromotion(packagesRequest, packagesResponse) && Boolean.TRUE.equals(
        freeFnbEnabled(packagesRequest.getChannel()))) {
      applyFreeMealLogic(packagesRequest, packagesResponse);
    }

    if (Boolean.TRUE.equals(extrasEnabled(packagesRequest.getChannel()))) {
      setExtrasItems(packagesRequest, packagesResponse, ancillariesContent);
    }

    addConfiguredDrinkExtras(packagesRequest, packagesResponse, ancillariesContent);

    CityTaxUtils.updateHasCityTaxFlags(packagesRequest.getHotelId(), packagesRequest.getStartDate(), packagesResponse,
        contentServiceOutPort);

    return packagesResponse;
  }

  @Override
  public DonationPackagesResponse getDonationPackageDetails(
          DonationPackagesRequest donationPackagesRequest) {
    log.debug(
            "Entered getDonationPackageDetails with donationPackagesRequest={}",
            donationPackagesRequest);
    var packagesRequestOhipDto = donationPackagesRequestMapper.toOhipDto(donationPackagesRequest);
    return donationPackagesResponseMapper.toModel(
            ohipClient.getHotelCharityPackagesDetails(packagesRequestOhipDto));
  }

  @Override
  public MealsInfoResponse getUpsellItemsAndSoftBundles(String hotelId, String country,
      String language) {
    return mealsInfoResponseMapper.toDomainModel(
        contentServiceClient.getUpsellItemsAndSoftBundles(hotelId, country, language));
  }

  private void setExtrasItems(PackagesRequest request, PackagesResponse response,
                              ExtrasLabelDto ancillariesContent) {

    // Get configured extras
    List<String> extras = Optional.ofNullable(ancillariesContent.getExtrasList())
            .orElse(Collections.emptyList());

    if (extras.isEmpty()) {
      response.getPackages().setExtrasItems(Collections.emptyList());
      return;
    }

    // Build lookup maps for quick access during extras processing
    Map<String, Meal> packageMap = response.getPackages().getMeals().stream()
            .collect(Collectors.toMap(
                    Meal::getId,
                    Function.identity()
            ));

    // Build lookup maps for quick access during extras processing
    Map<String, String> packageToInventoryCodeMap =
            response.getPackages().getMeals().stream()
                    .filter(meal -> meal.getId() != null
                            && meal.getInventoryItem() != null
                            && !meal.getInventoryItem().isBlank())
                    .collect(Collectors.toMap(
                            Meal::getId,
                            Meal::getInventoryItem
                    ));

    boolean isFreeFnbEnabled = freeFnbEnabled(request.getChannel());

    Map<String, UpsellItemsExtraDto> paidToFreeMap = isFreeFnbEnabled
            ? getPaidToFreeMapping(request.getCountry(), request.getLanguage())
            : Collections.emptyMap();

    // Fetch upsell items
    var upsellItemsContent = contentServiceClient.getUpsellItemsAndSoftBundles(
            request.getHotelId(),
            request.getCountry(),
            request.getLanguage());

    // Keep only enabled upsell items
    Map<String, UpsellItemsDto> upsellExtrasMap =
            Optional.ofNullable(upsellItemsContent.getUpsellItems())
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(item -> Boolean.TRUE.equals(item.getShow()))
                    .collect(Collectors.toMap(
                            UpsellItemsDto::getCode,
                            Function.identity()
                    ));

    // Build ancillary extras lookup map
    Map<String, uk.co.whitbread.hotel.content.generated.models.ExtrasDto> ancExtrasMap =
            Optional.ofNullable(ancillariesContent.getExtrasLabels())
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(Collectors.toMap(
                            uk.co.whitbread.hotel.content.generated.models.ExtrasDto::getId,
                            Function.identity()
                    ));

    // Fetch inventory only for extras that require inventory validation
    List<String> inventoryCodes = extras.stream()
            .map(packageToInventoryCodeMap::get)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    Map<String, List<InventoryAvailabilityDto>> inventoryMap =
            fetchInventory(request, inventoryCodes);

    List<ExtrasDto> extrasItemsResponse = new ArrayList<>();

    Set<String> selectedPackageCodes =
            Arrays.stream(Optional.ofNullable(request.getPackageSelections())
                            .orElse("")
                            .split(","))
                    .map(String::trim)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());


    var contentInfo = contentServiceOutPort.getHotelInformation(request.getCountry(),
        request.getLanguage(), request.getHotelId());

    HotelInformationExtended cutOffMapperModel = hotelInformationExtendedMapper.toModel(contentInfo);

    CutOffMinute cutOffMinutes = cutOffOutPort.getCutOffMinute(cutOffMapperModel);

    var cutOff = cutOffOutPort.isCutOffByHotel(request, cutOffMinutes);

    ExtrasContext extrasContext = new ExtrasContext(
        request,
        upsellExtrasMap,
        packageToInventoryCodeMap,
        ancExtrasMap,
        inventoryMap,
        cutOff);

    for (String code : extras) {

      Meal selectedPkg = getSelectedPackage(
              code,
              packageMap,
              paidToFreeMap,
              isFreeFnbEnabled,
              selectedPackageCodes
      );

      if (selectedPkg == null) {
        continue;
      }

      extrasItemsResponse.addAll(
              addExtrasResponses(
                      code,
                      selectedPkg,
                      extrasContext
              )
      );
    }

    response.getPackages().setExtrasItems(extrasItemsResponse);
  }

  private boolean hasFreeMealPromotion(
          PackagesRequest request,
          PackagesResponse response) {

    if (response == null
            || response.getPackages() == null
            || response.getPackages().getMeals() == null
            || StringUtils.isBlank(request.getPackageSelections())) {
      return false;
    }

    Set<String> selectedPackageCodes =
            Arrays.stream(request.getPackageSelections().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

    Map<String, Meal> mealMap =
            response.getPackages().getMeals().stream()
                    .collect(Collectors.toMap(
                            Meal::getId,
                            Function.identity(),
                            (a, b) -> a));

    for (String selectedCode : selectedPackageCodes) {

      Meal meal = mealMap.get(selectedCode);

      if (meal != null
              && meal.getPrice() != null
              && meal.getPrice().compareTo(BigDecimal.ZERO) != 0) {

        return true;
      }
    }

    return false;
  }

  private void applyFreeMealLogic(
          PackagesRequest request,
          PackagesResponse response) {

    if (response == null
            || response.getPackages() == null
            || response.getPackages().getMeals() == null
            || StringUtils.isBlank(request.getPackageSelections())) {
      return;
    }

    List<Meal> meals = response.getPackages().getMeals();

    Map<String, Meal> mealMap = meals.stream()
            .collect(Collectors.toMap(
                    Meal::getId,
                    Function.identity(),
                    (a, b) -> a));

    Set<String> selectedPackageCodes =
            Arrays.stream(request.getPackageSelections().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

    Map<String, UpsellItemsExtraDto> paidToFreeMap =
            getPaidToFreeMapping(
                    request.getCountry(),
                    request.getLanguage());

    Map<String, String> promoToPaidMap =
            paidToFreeMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getValue().getPromoPackageCode(),
                            Map.Entry::getKey
                    ));

    for (String selectedPackageCode : selectedPackageCodes) {
      Meal promoMeal = mealMap.get(selectedPackageCode);
      String paidPackageCode = promoToPaidMap.get(selectedPackageCode);
      Meal paidMeal = paidPackageCode != null ? mealMap.get(paidPackageCode) : null;

      if (!promoToPaidMap.containsKey(selectedPackageCode)
              || promoMeal == null
              || promoMeal.getPrice() == null
              || promoMeal.getPrice().compareTo(BigDecimal.ZERO) >= 0
              || paidPackageCode == null
              || paidMeal == null
              || paidMeal.getPrice() == null) {
        continue;
      }

      UpsellItemsExtraDto mapping = paidToFreeMap.get(paidPackageCode);
      if (mapping != null) {
        promoMeal.setPromoText(mapping.getPromoText());
      }

      promoMeal.setIsFree(true);
      promoMeal.setBasePrice(paidMeal.getPrice());
      promoMeal.setPrice(BigDecimal.ZERO);

      meals.removeIf(meal ->
              paidPackageCode.equals(meal.getId()));
    }
  }

  private Meal getSelectedPackage(String code,
                                  Map<String, Meal> packageMap,
                                  Map<String, UpsellItemsExtraDto> paidToFreeMap,
                                  boolean isFreeFnbEnabled,
                                  Set<String> selectedPackageCodes) {

    Meal paidPkg = packageMap.get(code);

    if (paidPkg != null) {
      paidPkg.setIsFree(false);
      paidPkg.setBasePrice(null);
    }

    if (!isFreeFnbEnabled) {
      return paidPkg;
    }

    UpsellItemsExtraDto mapping = paidToFreeMap.get(code);

    if (mapping == null) {
      return paidPkg;
    }

    Meal freePkg = packageMap.get(mapping.getPromoPackageCode());
    if (freePkg != null) {
      freePkg.setPromoText(mapping.getPromoText());
    }
    if (freePkg != null) {
      freePkg.setIsFree(false);
    }

    if (freePkg == null) {
      return paidPkg;
    }
    String freeCode = mapping.getPromoPackageCode();

    if (selectedPackageCodes.contains(freeCode)) {

      freePkg.setIsFree(true);

      if (paidPkg != null && paidPkg.getPrice() != null) {
        freePkg.setBasePrice(paidPkg.getPrice());
      }

      freePkg.setPrice(BigDecimal.ZERO);

      return freePkg;
    }

    return paidPkg;
  }

  private List<ExtrasDto> addExtrasResponses(String code,
                                  Meal selectedPkg,
                                  ExtrasContext extrasContext) {

    List<ExtrasDto> extrasResponses = new ArrayList<>();

    // Build upsell extras response
    ExtrasDto upsellExtraResponse = addUpsellItemsExtras(
            extrasContext.request(),
            selectedPkg,
            extrasContext.upsellExtrasMap().get(code)
    );

    if (upsellExtraResponse != null) {
      extrasResponses.add(upsellExtraResponse);

      setTotalPricePerStay(
              extrasResponses,
              selectedPkg.getId(),
              extrasContext.request()
      );
    }

    // Build ancillary extras response including inventory validation
    ExtrasDto ancillaryExtra = addAncillaryExtras(
            code,
            extrasContext.request(),
            selectedPkg,
            extrasContext.packageToInventoryCodeMap(),
            extrasContext.ancExtrasMap(),
            extrasContext.inventoryMap(),
            extrasContext.cutOff()
    );

    if (ancillaryExtra != null) {
      extrasResponses.add(ancillaryExtra);
    }

    return extrasResponses;
  }

  private ExtrasDto addUpsellItemsExtras(
          PackagesRequest request,
          Meal selectedPkg,
          UpsellItemsDto upsellExtrasItem) {

    if (upsellExtrasItem == null
            || EMPLOYEE_CHANNEL.equalsIgnoreCase(request.getChannel())) {
      return null;
    }

    return extrasMapper.toDtoFromUpsell(
            selectedPkg,
            null,
            upsellExtrasItem
    );
  }

  private ExtrasDto addAncillaryExtras(
          String code,
          PackagesRequest request,
          Meal selectedPkg,
          Map<String, String> packageToInventoryCodeMap,
          Map<String, uk.co.whitbread.hotel.content.generated.models.ExtrasDto> ancExtrasMap,
          Map<String, List<InventoryAvailabilityDto>> inventoryMap,
          CutOffExtras cutOff) {

    uk.co.whitbread.hotel.content.generated.models.ExtrasDto label =
        ancExtrasMap.get(code);

    if (label == null) {
      return null;
    }

    if (label.getReferenceDateType() != null
        && !label.getReferenceDateType().isBlank()
        && !cutOffOutPort.isOutsideCutOffTime(code, request, cutOff)) {
      return null;
    }

    String inventoryCode = packageToInventoryCodeMap.get(code);

    Integer available = null;

    if (Boolean.TRUE.equals(label.getRequiresInventory())) {

      if (inventoryCode == null) {
        return null;
      }

      available = getAvailability(
          inventoryCode,
          label.getReferenceDateType(),
          request,
          inventoryMap
      );
    }

    if (HSCKIN.equals(code) && available != null) {
      var availableRooms = cutOffOutPort.availableRooms(request, cutOff, available);
      return extrasMapper.toDto(selectedPkg, availableRooms, label);
    }

    return extrasMapper.toDto(selectedPkg, available, label);

  }

  private Map<String, UpsellItemsExtraDto> getPaidToFreeMapping(
          String country,
          String language) {

    var globalConfig = contentServiceClient.getGlobalConfig(country, language);

    if (globalConfig == null || globalConfig.getUpsellItemsExtras() == null) {

      return Collections.emptyMap();
    }

    return globalConfig.getUpsellItemsExtras().stream()
            .filter(item -> item.getPackageCode() != null && item.getPromoPackageCode() != null)
            .collect(Collectors.toMap(
                    UpsellItemsExtraDto::getPackageCode,
                    Function.identity()
            ));
  }

  private Map<String, List<InventoryAvailabilityDto>> fetchInventory(
          PackagesRequest request,
          List<String> inventoryCodes) {

    if (inventoryCodes.isEmpty()) {
      return Collections.emptyMap();
    }

    ItemInventoryRequestOhipDto inventoryRequest =
            packagesRequestMapper.toItemInventoryRequestOhipDto(request, inventoryCodes);

    ItemInventoryResponseDto response =
            ohipClient.getItemInventory(inventoryRequest);

    if (response == null || response.getItemsInventory() == null) {
      return Collections.emptyMap();
    }

    return response.getItemsInventory().stream()
            .collect(Collectors.toMap(
                    ItemInventoryDto::getCode,
                    item -> Optional.ofNullable(item.getInventories())
                            .orElse(Collections.emptyList())
            ));
  }

  private Integer getAvailability(
          String inventoryCode,
          String referenceDateType,
          PackagesRequest request,
          Map<String, List<InventoryAvailabilityDto>> inventoryMap) {

    List<InventoryAvailabilityDto> inventories =
            inventoryMap.get(inventoryCode);

    if (inventories == null) {
      var message = "No inventory data found for inventory code: " + inventoryCode;
      var exception = new PackagesException(
              ErrorCode.DIGITAL_ITEMS_INVENTORY_EXCEPTION,
              message
      );

      ExceptionLogger.log(log, exception);

      throw exception;
    }

    String referenceDate;

    if ("START_DATE".equals(referenceDateType)) {
      referenceDate = request.getStartDate();
    } else if ("END_DATE".equals(referenceDateType)) {
      referenceDate = request.getEndDate();
    } else {
      var message = "Invalid referenceDateType: " + referenceDateType;

      var exception = new PackagesException(
              ErrorCode.DIGITAL_ITEMS_INVENTORY_EXCEPTION,
              message
      );

      ExceptionLogger.log(log, exception);

      throw exception;
    }

    return inventories.stream()
            .filter(inv -> referenceDate.equals(inv.getDate()))
            .map(InventoryAvailabilityDto::getAvailable)
            .findFirst()
            .orElseThrow(() -> {
              var message = "Error while trying to get items inventory!";
              var exception = new PackagesException(
                      ErrorCode.DIGITAL_ITEMS_INVENTORY_EXCEPTION,
                      message
              );
              ExceptionLogger.log(log, exception);
              return exception;
            });
  }

  private void addConfiguredDrinkExtras(PackagesRequest packagesRequest,
      PackagesResponse packagesResponse, ExtrasLabelDto ancillariesContent) {

    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getExtrasItemsBottleOfProsecco())) {
      return;
    }
    if (StringUtils.isNotEmpty(packagesRequest.getChannel())
        && packagesRequest.getChannel().equals(BB_CHANNEL)) {
      return;
    }
    log.debug("Entered addConfiguredDrinkExtras with PackagesRequest={}", packagesRequest);

    if (packagesRequest == null || packagesResponse == null) {
      log.warn("Request or response is null. Skipping extras configuration.");
      return;
    }

    var packages = Optional.ofNullable(packagesResponse.getPackages()).orElse(null);
    if (packages == null) {
      log.debug("Packages is null in PackagesResponse. Skipping extras configuration.");
      return;
    }

    var mealList = new ArrayList<>(
        Optional.ofNullable(packages.getMeals()).orElse(Collections.emptyList()));

    Set<String> extrasPackageCodes = new HashSet<>(packagesProperties.getExtrasPackageCodes());
    boolean hasExtras = hasExtras(mealList, extrasPackageCodes);
    if (!hasExtras) {
      return;
    }

    final List<uk.co.whitbread.hotel.content.generated.models.ExtrasDto> extrasLabels =
            Optional.ofNullable(ancillariesContent.getExtrasLabels())
                    .orElse(Collections.emptyList());

    var extrasItemsResponse = new ArrayList<>(
        Optional.ofNullable(packages.getExtrasItems()).orElse(Collections.emptyList())
    );
    Set<String> matchedIds = new HashSet<>();

    mealList.stream()
        .filter(meal -> extrasPackageCodes.contains(meal.getId()))
        .forEach(meal -> {
          var label = findLabel(meal, extrasLabels);

          extrasItemsResponse.add(extrasMapper.toDto(meal, null, label));
          matchedIds.add(meal.getId());
        });

    mealList.removeIf(meal -> matchedIds.contains(meal.getId()));
    packagesResponse.getPackages().setMeals(mealList);
    packagesResponse.getPackages().setExtrasItems(extrasItemsResponse);
  }

  private uk.co.whitbread.hotel.content.generated.models.ExtrasDto findLabel(Meal meal,
      List<uk.co.whitbread.hotel.content.generated.models.ExtrasDto> extrasLabels) {
    return extrasLabels.stream()
        .filter(extras -> meal.getId().equals(extras.getId()))
        .findFirst()
        .orElse(null);
  }

  private boolean hasExtras(ArrayList<Meal> mealList, Set<String> extrasPackageCodes) {
    return mealList.stream()
        .anyMatch(meal -> extrasPackageCodes.contains(meal.getId()));
  }

  private Boolean extrasEnabled(String channel) {
    return ((PI_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getExtrasItemsPi()))
            || (BB_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getExtrasItemsBb()))
            || (CCUI_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getExtrasItemsCcui()))
            || (EMPLOYEE_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getExtrasItemsPi())));
  }

  private Boolean freeFnbEnabled(String channel) {
    return ((PI_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFreeFnbExtrasPi()))
            || (BB_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFreeFnbExtrasBb()))
            || (CCUI_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFreeFnbExtrasCcui()))
            || (EMPLOYEE_CHANNEL.equalsIgnoreCase(channel)
            && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFreeFnbExtrasPi())));
  }

  private void setTotalPricePerStay(List<ExtrasDto> extrasItemsResponse, String packageCode,
                                    PackagesRequest packagesRequest) {
    var extrasItemToCalculatePrice =  extrasItemsResponse.stream()
            .filter(extrasDto -> packageCode.equals(extrasDto.getId())).findFirst();
    if (extrasItemToCalculatePrice.isPresent()) {
      var pricePerDay = extrasItemToCalculatePrice.map(ExtrasDto::getPrice);
      pricePerDay.ifPresent(price -> extrasItemToCalculatePrice.get().setPrice(
              price.multiply(BigDecimal.valueOf(packagesRequest.getNightsNumber()))));
    }
  }

  private void initialiseMeals(List<Meal> meals) {

    meals.forEach(meal -> {
      meal.setIsFree(false);
      meal.setBasePrice(null);
    });
  }
}
