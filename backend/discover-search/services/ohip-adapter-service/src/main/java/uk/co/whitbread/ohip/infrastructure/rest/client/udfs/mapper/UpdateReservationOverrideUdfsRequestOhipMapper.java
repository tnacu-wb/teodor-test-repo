package uk.co.whitbread.ohip.infrastructure.rest.client.udfs.mapper;


import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.udfs.in.CharacterUdf;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateReservationOverrideUdfsRequestOhipMapper {

  @Mapping(expression = "java(mapUdfs(udfs, hotelId))", target = "reservations")
  public abstract ChangeReservation toDto(String hotelId, List<CharacterUdf> udfs);

  protected List<HotelReservationInstructionType> mapUdfs(List<CharacterUdf> characterUDF,
      String hotelId) {

    final var userDefinedFields = new UserDefinedFieldsType();

    if (characterUDF != null) {
      userDefinedFields.setCharacterUDFs(characterUDF.stream().map(udf -> {
        final var characterUDFType = new CharacterUDFType();
        characterUDFType.setName(udf.name());
        characterUDFType.setValue(udf.value());
        return characterUDFType;
      }).toList());
    }

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setHotelId(hotelId);
    hotelReservation.setUserDefinedFields(userDefinedFields);
    return List.of(hotelReservation);
  }
}
