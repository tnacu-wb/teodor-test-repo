package uk.co.whitbread.content.domain.model.pricefinder.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class PriceFinderGlobalConfigRequest implements SelfValidation<PriceFinderGlobalConfigRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String channelId;
  @NotEmpty
  private String brand;
  private String path;

  public PriceFinderGlobalConfigRequest(String country, String language, String channelId, String brand, String path) {
    this.country = country;
    this.language = language;
    this.channelId = channelId;
    this.brand = brand;
    this.path = path;
  }
}
