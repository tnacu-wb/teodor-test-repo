package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class AcceptedCardType {
  private String type;
  private String name;
  private String logoSrc;
}
