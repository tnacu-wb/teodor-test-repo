package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation.AddNewRoomFormat;

@Data
@AddNewRoomFormat
@SuperBuilder
@NoArgsConstructor
public class AddNewRoomRequestDto {

  @NotNull
  @NotEmpty
  private String tempBookingRef;
  @NotNull
  private RoomOccupancyDto roomOccupancy;
  @NotNull
  private LeadGuestDto leadGuest;
  @NotNull
  @NotEmpty
  private String roomType;
  private BookingChannelDto bookingChannel;

  private String token;
  private String ratePlanCode;
  private List<String> specialRequests;
}
