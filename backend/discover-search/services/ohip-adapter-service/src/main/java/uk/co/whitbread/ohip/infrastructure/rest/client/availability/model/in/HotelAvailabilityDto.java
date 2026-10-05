package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HotelAvailabilityDto {

  @JsonProperty("roomStays")
  private List<RoomStayTypeDto> roomStays;

  @JsonProperty("ratePlanSet")
  private String ratePlanSet;

  @JsonProperty("hotelId")
  private String hotelId;

  @JsonProperty("closed")
  private boolean closed;

  @JsonProperty("redemption")
  private boolean redemption;

  @JsonProperty("hasMore")
  private boolean hasMore;
}
