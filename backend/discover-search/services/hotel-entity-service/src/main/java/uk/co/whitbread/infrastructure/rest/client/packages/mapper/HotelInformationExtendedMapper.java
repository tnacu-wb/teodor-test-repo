package uk.co.whitbread.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.packages.in.ExtrasCutoff;
import uk.co.whitbread.domain.model.packages.in.HotelInformationExtended;
import uk.co.whitbread.hotel.content.generated.models.ExtraCutoffDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;

/**
 * MapStruct mapper for transforming hotel information DTOs to domain models.
 *
 * <p>Converts {@link HotelInformationExtendedDto} to {@link HotelInformationExtended} and
 * {@link ExtraCutoffDto} to {@link ExtrasCutoff}.
 */
@Mapper(componentModel = "spring")
public interface HotelInformationExtendedMapper {

  /**
   * Transforms a hotel information extended DTO to its domain model representation.
   *
   * @param hotelInformationExtendedDto the hotel information DTO from the content service
   * @return the domain model representation of hotel information extended
   */
  HotelInformationExtended toModel(HotelInformationExtendedDto hotelInformationExtendedDto);

  /**
   * Transforms an extra cutoff DTO to its domain model representation.
   *
   * @param extraCutoffDto the extra cutoff DTO from the content service
   * @return the domain model representation of extra cutoff
   */
  ExtrasCutoff toModel(ExtraCutoffDto extraCutoffDto);
}

