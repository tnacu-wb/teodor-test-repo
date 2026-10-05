package uk.co.whitbread.content.domain.model.meals.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
public class MealsRequest implements SelfValidation<MealsRequest> {

  private String country;
  private String language;
  private String hotelId;

  public MealsRequest(String country, String language, String hotelId) {
    this.country = country;
    this.language = language;
    this.hotelId = hotelId;
    this.validateSelf();
  }
}
