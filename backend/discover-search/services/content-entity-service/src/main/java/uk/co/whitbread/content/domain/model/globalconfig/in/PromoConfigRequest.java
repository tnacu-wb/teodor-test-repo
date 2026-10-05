package uk.co.whitbread.content.domain.model.globalconfig.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoCodeStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@AllArgsConstructor
public class PromoConfigRequest implements SelfValidation<PromoConfigRequest> {

  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
  @NotEmpty
  private String channelId;
  @NotEmpty
  private String brand;
  private String promotionCode;
  @NotEmpty
  private String bookingDate;
  @NotEmpty
  private String stayStartDate;
  @NotEmpty
  private String stayEndDate;
  private PromoKind promoKind;
  private Boolean isAmendRequest;
  private Boolean isPromoBox;
  private String operaPromoCode;
  private PromoCodeStatus uniquePromoCodeStatus;
  private String rateName;
  private String roomClass;
  private Integer noOfRooms;
}
