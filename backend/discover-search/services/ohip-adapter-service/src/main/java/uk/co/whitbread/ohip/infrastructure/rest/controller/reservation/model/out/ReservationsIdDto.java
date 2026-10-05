package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ReservationsIdDto {

  private List<ReservationIdDetailsCheckDto> reservation;
  private int totalPages;
  private int offset;
  private int limit;
  private boolean hasMore;
  private int totalResults;
}