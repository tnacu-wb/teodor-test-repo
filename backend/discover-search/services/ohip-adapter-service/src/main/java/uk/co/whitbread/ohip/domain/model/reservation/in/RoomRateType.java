package uk.co.whitbread.ohip.domain.model.reservation.in;


import lombok.Data;

@Data
public class RoomRateType {

  private RateType rates;
  private String roomType;
  private String ratePlanCode;
  private String start;
  private String end;
  private String sourceCode;
  private Integer numberOfUnits;

}
