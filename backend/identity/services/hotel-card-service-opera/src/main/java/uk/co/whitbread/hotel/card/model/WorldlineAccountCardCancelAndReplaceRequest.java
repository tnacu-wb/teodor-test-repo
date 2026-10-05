package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineAccountCardCancelAndReplaceRequest {

  private Scheme scheme;
  private boolean issueReplacement;
  private String apiUserGuid;
  private AddressCorrespondenceEnum cardDeliveryAddressType;
  private InnBusinessCorrespondenceAddress cardCorrespondenceAddress;

}
