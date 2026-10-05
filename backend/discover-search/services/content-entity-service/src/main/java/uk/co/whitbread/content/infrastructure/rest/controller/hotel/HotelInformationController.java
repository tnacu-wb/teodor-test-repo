package uk.co.whitbread.content.infrastructure.rest.controller.hotel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper.HotelInformationDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper.HotelInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper.HotelsInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelShortInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelsInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.SlugRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationExtendedDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelPaymentInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelShortInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.MenuDto;


@RestController
@Slf4j
public class HotelInformationController implements HotelInformationApiDocumentation {

  private final ContentInPort contentInPort;
  private final CacheManager cacheManager1Hour;
  private final HotelInformationRequestDtoMapper hotelInformationRequestDtoMapper;
  private final HotelInformationDtoMapper hotelInformationDtoMapper;
  private final HotelsInformationRequestDtoMapper hotelsInformationRequestDtoMapper;

  public HotelInformationController(
      ContentInPort contentInPort,
      @Qualifier("cacheManager1Hour") CacheManager cacheManager1Hour,
      HotelInformationRequestDtoMapper hotelInformationRequestDtoMapper,
      HotelInformationDtoMapper hotelInformationDtoMapper,
      HotelsInformationRequestDtoMapper hotelsInformationRequestDtoMapper
  ) {
    this.contentInPort = contentInPort;
    this.cacheManager1Hour = cacheManager1Hour;
    this.hotelInformationRequestDtoMapper = hotelInformationRequestDtoMapper;
    this.hotelInformationDtoMapper = hotelInformationDtoMapper;
    this.hotelsInformationRequestDtoMapper = hotelsInformationRequestDtoMapper;
  }

  @GetMapping(value = HOTEL_INFO_PATH
      + "/hotels/information", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<HotelInformationDto>> getHotelsInformation(
      @Valid @ParameterObject HotelsInformationRequestDto hotelsInformationRequestDto) {

    final var hotelsInformationRequest = hotelsInformationRequestDtoMapper.toDomainModel(
        hotelsInformationRequestDto);
    var hotelInformationDto = hotelInformationDtoMapper.toListDto(
        contentInPort.getHotelsInformation(hotelsInformationRequest));

    return ResponseEntity.status(HttpStatus.OK).body(hotelInformationDto);
  }

  @GetMapping(value = HOTEL_INFO_PATH
      + "/hotels/{hotelId}/information", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelInformationExtendedDto> getHotelInformation(
      @PathVariable("hotelId") @NotNull String hotelId,
      @Valid @ParameterObject HotelInformationRequestDto hotelInformationRequestDto,
      @RequestParam(required = false) LocalDate stayStartDate,
      @RequestParam(required = false) LocalDate stayEndDate) {

    final var hotelInformationRequest = hotelInformationRequestDtoMapper
        .toDomainModel(hotelId, hotelInformationRequestDto);

    // Try to get DTO from cache
    var valueFromCache = HotelInformationCacheUtils.getValueFromCache(
        cacheManager1Hour, hotelInformationRequest, hotelId);

    HotelInformationExtendedDto hotelInformationDto;

    if (valueFromCache.isPresent()) {
      hotelInformationDto = valueFromCache.get();
    } else {
      // Get domain model from port, map to DTO, store in cache
      var hotelInformationExtended = contentInPort.getHotelInformation(hotelInformationRequest);
      hotelInformationDto = hotelInformationDtoMapper.toDto(hotelInformationExtended);
      HotelInformationCacheUtils.storeValueInCache(
          cacheManager1Hour, hotelInformationRequest, hotelId, hotelInformationDto);
    }

    // Apply stay date filtering after cache retrieval —
    // stayStartDate/stayEndDate are intentionally excluded from cache key
    var filteredDto = applyStayDateFilter(hotelInformationDto, stayStartDate, stayEndDate);

    return ResponseEntity.status(HttpStatus.OK).body(filteredDto);
  }

  @GetMapping(value = HOTEL_INFO_PATH + "/hotels/{hotelId}/payment-information",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelPaymentInformationDto> getHotelPaymentInformation(
      @PathVariable("hotelId") String hotelId,
      @Valid HotelInformationRequestDto requestDto) {

    var request = hotelInformationRequestDtoMapper.toDomainModel(hotelId, requestDto);
    var paymentInformation = contentInPort.getHotelPaymentInformation(request);
    var paymentInformationDto =
        hotelInformationDtoMapper.toHotelPaymentMethodDto(paymentInformation);

    return ResponseEntity.ok(paymentInformationDto);
  }

  @GetMapping(value = HOTEL_INFO_PATH + "/hotels", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HotelInformationDto> getHotelInformationBySlug(
      @Valid @ParameterObject SlugRequestDto slugRequestDto,
      @Valid @ParameterObject HotelInformationRequestDto hotelInformationRequestDto,
      @RequestParam(required = false) LocalDate stayStartDate,
      @RequestParam(required = false) LocalDate stayEndDate) {

    final var hotelInformationRequest =
        hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto,
            hotelInformationRequestDto, stayStartDate, stayEndDate);
    var hotelInformationDto = hotelInformationDtoMapper.toDto(
        contentInPort.getHotelInformationBySlug(hotelInformationRequest));
    var filteredDto = applyStayDateFilter(hotelInformationDto, stayStartDate, stayEndDate);
    return ResponseEntity.status(HttpStatus.OK).body(filteredDto);
  }

  @GetMapping(value = HOTEL_INFO_PATH + "/allhotels", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<HotelShortInformationDto>> getAllHotelsShortInformation(
      @Valid @ParameterObject HotelShortInformationRequestDto hotelShortInformationRequestDto) {

    final var hotelShortInformationRequest = hotelInformationRequestDtoMapper
        .toDomainModel(hotelShortInformationRequestDto);
    var allHotelsShortInformation = contentInPort.getAllHotelsShortInformation(
        hotelShortInformationRequest.getCountry(), hotelShortInformationRequest.getLanguage());
    var allHotelsShortInformationDto = allHotelsShortInformation.stream()
        .map(hotelInformationDtoMapper::toDto)
        .toList();
    return ResponseEntity.status(HttpStatus.OK).body(allHotelsShortInformationDto);
  }

  @GetMapping(value = HOTEL_INFO_PATH
      + "/hotels/facilities/updater/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> triggerUpdateHotelsFacilitiesCache() {
    contentInPort.updateHotelsFacilitiesCache();
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping(value = HOTEL_INFO_PATH
      + "/hotels/opening-soon/updater/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> triggerUpdateHotelsOpeningSoonCache() {
    contentInPort.updateHotelsOpeningSoonCache();
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  private HotelInformationDto applyStayDateFilter(
      HotelInformationDto dto,
      LocalDate stayStartDate,
      LocalDate stayEndDate) {
    if (dto.getRestaurant() == null || dto.getRestaurant().getMenus() == null) {
      return dto;
    }
    var filteredRestaurant = dto.getRestaurant().toBuilder()
        .menus(computeFilteredMenus(dto.getRestaurant().getMenus(), stayStartDate, stayEndDate))
        .build();
    return dto.toBuilder().restaurant(filteredRestaurant).build();
  }

  private HotelInformationExtendedDto applyStayDateFilter(
      HotelInformationExtendedDto dto,
      LocalDate stayStartDate,
      LocalDate stayEndDate) {
    if (dto.getRestaurant() == null || dto.getRestaurant().getMenus() == null) {
      return dto;
    }
    // Build a filtered restaurant using toBuilder to avoid fragile manual field copying
    var filteredRestaurant = dto.getRestaurant().toBuilder()
        .menus(computeFilteredMenus(dto.getRestaurant().getMenus(), stayStartDate, stayEndDate))
        .build();
    // Return a shallow copy of the DTO with the filtered restaurant to avoid mutating the cached object
    return dto.toBuilder().restaurant(filteredRestaurant).build();
  }

  private List<MenuDto> computeFilteredMenus(
      List<MenuDto> menus,
      LocalDate stayStartDate,
      LocalDate stayEndDate) {
    if (stayStartDate == null || stayEndDate == null) {
      // If either stay date is missing, return only unscheduled menu items
      return menus.stream().filter(m -> !isScheduled(m)).toList();
    }
    return filterMenusByStayDates(menus, stayStartDate, stayEndDate);
  }

  private List<MenuDto> filterMenusByStayDates(
      List<MenuDto> menus,
      LocalDate stayStartDate,
      LocalDate stayEndDate) {

    var grouped = new LinkedHashMap<String, List<MenuDto>>();
    for (MenuDto menu : menus) {
      grouped.computeIfAbsent(menu.getName(), ignored -> new ArrayList<>()).add(menu);
    }

    var result = new ArrayList<MenuDto>();

    for (var entry : grouped.entrySet()) {
      List<MenuDto> group = entry.getValue();

      // First: look for a scheduled item that overlaps with stay dates
      MenuDto scheduled = group.stream()
          .filter(this::isScheduled)
          .filter(m -> overlapsStay(m, stayStartDate, stayEndDate))
          .findFirst()
          .orElse(null);

      if (scheduled != null) {
        result.add(scheduled);
      } else {
        // Fallback: item with no dates
        group.stream()
            .filter(m -> !isScheduled(m))
            .findFirst()
            .ifPresent(result::add);
      }
    }

    return result;
  }

  private boolean isScheduled(MenuDto menu) {
    return menu.getStayStartDate() != null && menu.getStayEndDate() != null;
  }

  private boolean overlapsStay(
      MenuDto menu,
      LocalDate stayStart,
      LocalDate stayEnd) {
    LocalDate menuStart = menu.getStayStartDate();
    LocalDate menuEnd = menu.getStayEndDate();
    // A menu is active if at least one day of the stay falls within [menuStart, menuEnd]
    return !stayStart.isAfter(menuEnd) && !stayEnd.isBefore(menuStart);
  }
}
