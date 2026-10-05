package uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CookiePoliciesRequestDto {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String brand;

  public CookiePoliciesRequestDto(String country, String language, String brand) {
    this.country = country;
    this.language = language;
    this.brand = brand;
  }

}
