package uk.co.whitbread.content.domain.model.hotel.in;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.HotelsInformationRequestConstraint;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@HotelsInformationRequestConstraint
public class HotelsInformationRequest implements SelfValidation<HotelsInformationRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
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

  public HotelsInformationRequest(String country, String language, List<String> hotelIdList,
      @Nullable Double latitudeRef, @Nullable Double longitudeRef,
      @Nullable Boolean tripAdvisorDataRequired,
      @Nullable LocalDate stayStartDate, @Nullable LocalDate stayEndDate) {
    this.country = country;
    this.language = language;
    this.hotelIds = hotelIdList;
    this.latitudeRef = latitudeRef;
    this.longitudeRef = longitudeRef;
    this.tripAdvisorDataRequired = tripAdvisorDataRequired;
    this.stayStartDate = stayStartDate;
    this.stayEndDate = stayEndDate;
    this.validateSelf();
  }
}
