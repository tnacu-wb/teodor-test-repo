package uk.co.whitbread.basket.domain.model.email.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class RefundDetail extends PaymentDetail {

  @JsonInclude(Include.NON_NULL)
  private Boolean isRefundSuccess;
}
