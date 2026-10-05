package uk.co.whitbread.domain.model.srp.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import uk.co.whitbread.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class LimitedAvailabilityRequest implements SelfValidation<LimitedAvailabilityRequest> {

  private List<String> hotelCodes;
  @NotNull
  private String arrival;
  @NotNull
  private String departure;
  @NotNull
  private OldWorldChannelEnum bookingChannel;
  @NotEmpty
  private List<Integer> adults;
  @NotEmpty
  private List<Integer> children;
  @NotNull
  private Integer rooms;
  @NotEmpty
  private List<String> type;
  private String companyId;
  private List<String> cellCodes;
  private String employeeId;

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
