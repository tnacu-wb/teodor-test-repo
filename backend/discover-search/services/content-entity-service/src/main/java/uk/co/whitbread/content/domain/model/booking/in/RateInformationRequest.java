package uk.co.whitbread.content.domain.model.booking.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class RateInformationRequest implements SelfValidation<RateInformationRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String brand;

  private List<String> ratePlans;

  private String hotelId;

  private String channel;

  public RateInformationRequest(String country, String language, String brand,
      List<String> ratePlans, String hotelId, String channel) {
    this.country = country;
    this.language = language;
    this.brand = brand;
    this.ratePlans = ratePlans;
    this.hotelId = hotelId;
    this.channel = channel;
    this.validateSelf();
  }
}
