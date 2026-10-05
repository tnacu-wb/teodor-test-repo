package uk.co.whitbread.hotel.card.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineAddress {

  private String addressLine1;

  private String addressLine2;

  @Nullable
  private String addressLine3;

  @Nullable
  private String addressLine4;

  private String postcode;

  private String countryCode;

}
