package uk.co.whitbread.hotel.card.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineReplaceCardRequest {

  private Integer uniqueCustomerCardId;

  @Nullable
  private Boolean shouldDespatchToCardholder;

  @Nullable
  private WorldlineContactDetails contactDetails;

  @Nullable
  private WorldlineAddress address;

  @Nullable
  private Boolean cancelCard;
}
