package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationBaseClassDto;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class HotelsInformationRequestDto extends LocalizationBaseClassDto {

  @NotEmpty
  private List<String> hotelIds;
  @Nullable
  private Double latitudeRef;
  @Nullable
  private Double longitudeRef;
  @Nullable
  private Boolean tripAdvisorDataRequired;
  @Nullable
  private LocalDate stayStartDate;
  @Nullable
  private LocalDate stayEndDate;

  public HotelsInformationRequestDto(String country, String language, List<String> hotelIds,
      @Nullable Double latitudeRef, @Nullable Double longitudeRef,
      @Nullable Boolean tripAdvisorDataRequired,
      @Nullable LocalDate stayStartDate, @Nullable LocalDate stayEndDate) {
    super(country, language);
    this.hotelIds = hotelIds;
    this.latitudeRef = latitudeRef;
    this.longitudeRef = longitudeRef;
    this.tripAdvisorDataRequired = tripAdvisorDataRequired;
    this.stayStartDate = stayStartDate;
    this.stayEndDate = stayEndDate;
  }

  private HotelsInformationRequestDto(final HotelsInformationRequestDto.HotelsInformationRequestDtoBuilder<?, ?> b,
      List<String> hotelIds, @Nullable Double latitudeRef, @Nullable Double longitudeRef,
      @Nullable Boolean tripAdvisorDataRequired,
      @Nullable LocalDate stayStartDate, @Nullable LocalDate stayEndDate) {
    super(b);
    this.hotelIds = hotelIds;
    this.latitudeRef = latitudeRef;
    this.longitudeRef = longitudeRef;
    this.tripAdvisorDataRequired = tripAdvisorDataRequired;
    this.stayStartDate = stayStartDate;
    this.stayEndDate = stayEndDate;
  }

}
