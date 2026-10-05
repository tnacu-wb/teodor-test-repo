package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppsCustomer {
  private String captcha;

  @NotNull
  private String password;

  @NotNull
  @Valid
  private AppsContactDetail contactDetail;
}
