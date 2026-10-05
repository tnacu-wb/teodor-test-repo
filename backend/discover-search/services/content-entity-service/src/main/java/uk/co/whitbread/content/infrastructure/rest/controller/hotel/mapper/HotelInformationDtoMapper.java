package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import uk.co.whitbread.content.domain.model.hotel.out.ExtraCutoff;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformationExtended;
import uk.co.whitbread.content.domain.model.hotel.out.HotelPaymentInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelShortInformation;
import uk.co.whitbread.content.domain.model.hotel.out.Menu;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.ExtraCutoffDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationExtendedDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelPaymentInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelShortInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.MenuDto;

@Mapper(componentModel = "spring", uses = {
    HotelFacilityDtoMapper.class,
    HotelGalleryImageDtoMapper.class,
    RoomConfigurationDtoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HotelInformationDtoMapper {

  DateTimeFormatter AEM_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  HotelInformationExtendedDto toDto(HotelInformationExtended hotelInformation);

  HotelInformationDto toDto(HotelInformation hotelInformation);

  HotelShortInformationDto toDto(HotelShortInformation hotelShortInformation);

  ExtraCutoffDto toDto(ExtraCutoff extraCutoff);

  List<ExtraCutoffDto> toDto(List<ExtraCutoff> extrasCutoffs);

  List<HotelInformationDto> toListDto(List<HotelInformation> hotelInformationList);

  HotelPaymentInformationDto toHotelPaymentMethodDto(HotelPaymentInformation hotelInformation);

  @Mapping(target = "stayStartDate", source = "stayStartDate", qualifiedByName = "toLocalDateModel")
  @Mapping(target = "stayEndDate", source = "stayEndDate", qualifiedByName = "toLocalDateModel")
  MenuDto toMenuDto(Menu menu);

  @Named("toLocalDateModel")
  default LocalDate toLocalDateModel(String date) {
    if (!StringUtils.hasText(date)) {
      return null;
    }
    try {
      return LocalDate.parse(date, AEM_DATE_FORMATTER);
    } catch (Exception e) {
      LoggerFactory.getLogger(HotelInformationDtoMapper.class)
          .warn("Failed to parse AEM date value '{}': {}", date, e.getMessage());
      return null;
    }
  }
}
