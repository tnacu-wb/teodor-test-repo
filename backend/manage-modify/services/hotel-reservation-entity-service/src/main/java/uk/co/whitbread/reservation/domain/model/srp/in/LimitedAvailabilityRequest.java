package uk.co.whitbread.reservation.domain.model.srp.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Value
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class LimitedAvailabilityRequest implements SelfValidation<LimitedAvailabilityRequest> {

  List<String> hotelCodes;
  @NotNull
  String arrival;
  @NotNull
  String departure;
  @NotNull
  OldWorldChannelEnum bookingChannel;
  @NotEmpty
  List<Integer> adults;
  @NotEmpty
  List<Integer> children;
  @NotNull
  Integer rooms;
  @NotEmpty
  List<String> type;
  String companyId;
  List<String> cellCodes;
  String employeeId;

  public LimitedAvailabilityRequest(List<String> hotelCodes, String arrival,
                                    String departure,
                                    OldWorldChannelEnum bookingChannel, List<Integer> adults,
                                    List<Integer> children, Integer rooms, List<String> type,
                                    String companyId, List<String> cellCodes, String employeeId) {
    this.hotelCodes = hotelCodes;
    this.arrival = arrival;
    this.departure = departure;
    this.bookingChannel = bookingChannel;
    this.adults = adults;
    this.children = children;
    this.rooms = rooms;
    this.type = type;
    this.companyId = companyId;
    this.cellCodes = cellCodes;
    this.employeeId = employeeId;
    this.validateSelf();
  }
}
