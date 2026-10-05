package uk.co.whitbread.ohip.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class AvailabilityRoomSearchCriteria implements
    SelfValidation<AvailabilityRoomSearchCriteria> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String ratePlanCode;
  @NotEmpty
  private String roomType;
  @NotNull
  private int numberOfRooms;
  @NotEmpty
  private String arrivalDate;
  @NotEmpty
  private String departureDate;
  @Positive
  @NotNull
  private int adults;
  @PositiveOrZero
  private int children;

  public AvailabilityRoomSearchCriteria(String hotelId, String ratePlanCode,
      String roomType, int numberOfRooms, String arrivalDate, String departureDate, int adults,
      int children) {
    this.hotelId = hotelId;
    this.ratePlanCode = ratePlanCode;
    this.roomType = roomType;
    this.numberOfRooms = numberOfRooms;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.adults = adults;
    this.children = children;
    this.validateSelf();
  }
}
