package uk.co.whitbread.content.domain.model.globalconfig.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class GlobalConfigRequest implements SelfValidation<GlobalConfigRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String channelId;
  @NotEmpty
  private String brand;

  public GlobalConfigRequest(String country, String language, String channelId, String brand) {
    this.country = country;
    this.language = language;
    this.channelId = channelId;
    this.brand = brand;
  }
}
