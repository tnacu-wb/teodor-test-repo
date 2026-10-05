package uk.co.whitbread.availabilitycacheservice.domain.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Price implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  private BigDecimal amount;

  @NotEmpty
  private String currency;
}
