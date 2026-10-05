package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class UserInfoDto implements SelfValidation<UserInfoDto> {

  private String userId;
  private String idContext;

  public UserInfoDto(String userId, String idContext) {
    this.userId = userId;
    this.idContext = idContext;
    this.validateSelf();
  }
}

