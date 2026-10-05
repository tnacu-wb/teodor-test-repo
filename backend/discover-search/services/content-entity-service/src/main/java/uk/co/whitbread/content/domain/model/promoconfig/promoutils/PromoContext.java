package uk.co.whitbread.content.domain.model.promoconfig.promoutils;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromoContext {

  private LocalDate bookingDate;
  private LocalDate stayStartDate;
  private LocalDate stayEndDate;

  private String promoCode;
  private PromoKind promoKind;

  private boolean promoBox;
  private boolean amendRequest;
}
