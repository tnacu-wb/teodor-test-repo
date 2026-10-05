package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;


@Data
@Builder
@NoArgsConstructor
public class ReservationBasketInfoDto implements SelfValidation<ReservationBasketInfoDto> {

  private String migratedResNo;
  private String originalBasketId;
  private Map<String, String> linkAmendReservations;

  public ReservationBasketInfoDto(String migratedResNo,
                                  String originalBasketId,
                                  Map<String, String> linkAmendReservations) {
    this.migratedResNo = migratedResNo;
    this.originalBasketId = originalBasketId;
    this.linkAmendReservations = linkAmendReservations;
    this.validateSelf();
  }
}

