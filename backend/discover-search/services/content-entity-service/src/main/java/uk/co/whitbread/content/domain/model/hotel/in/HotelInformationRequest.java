package uk.co.whitbread.content.domain.model.hotel.in;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.HotelInformationRequestConstraint;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@HotelInformationRequestConstraint
public class HotelInformationRequest implements SelfValidation<HotelInformationRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  private String hotelId;
  private String slug;
  private String channel;
  private String subchannel;
  @Nullable
  private LocalDate stayStartDate;
  @Nullable
  private LocalDate stayEndDate;

  public HotelInformationRequest(String country, String language, String hotelId, String slug,
      String channel, String subchannel,
      @Nullable LocalDate stayStartDate, @Nullable LocalDate stayEndDate) {
    this.country = country;
    this.language = language;
    this.hotelId = hotelId;
    this.slug = slug;
    this.channel = channel;
    this.subchannel = subchannel;
    this.stayStartDate = stayStartDate;
    this.stayEndDate = stayEndDate;
    this.validateSelf();
  }
}
