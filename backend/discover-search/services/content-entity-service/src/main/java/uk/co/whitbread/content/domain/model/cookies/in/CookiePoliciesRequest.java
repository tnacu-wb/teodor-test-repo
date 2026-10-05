package uk.co.whitbread.content.domain.model.cookies.in;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class CookiePoliciesRequest implements SelfValidation<CookiePoliciesRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String brand;

  public CookiePoliciesRequest(String country, String language, String brand) {
    this.country = country;
    this.language = language;
    this.brand = brand;
    this.validateSelf();
  }

}
