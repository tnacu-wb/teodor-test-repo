package uk.co.whitbread.domain.model.distance.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.domain.model.validation.SelfValidation;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class DistanceFromSearchRequest implements SelfValidation<DistanceFromSearchRequest> {

  private String hotelId;
  @NotEmpty
  private String location;
  @NotEmpty
  private String locationFormat;
  private Integer radius;
  private String radiusUnit;

  public DistanceFromSearchRequest(String hotelId, String location, String locationFormat,
      Integer radius, String radiusUnit) {
    this.hotelId = hotelId;
    this.location = location;
    this.locationFormat = locationFormat;
    this.radius = radius;
    this.radiusUnit = radiusUnit;
    this.validateSelf();
  }
}
