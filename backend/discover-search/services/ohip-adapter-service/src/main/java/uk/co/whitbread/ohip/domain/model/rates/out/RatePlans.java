package uk.co.whitbread.ohip.domain.model.rates.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RatePlans {

  private List<RoomTypeList> roomTypeList;
  private List<RatePlanBasedOnRate> ratePlanBasedOnRates;
  private RateScheduleInfo rateScheduleInfo;
  private Boolean mobileCheckinAllowed;
  private Boolean mobileCheckoutAllowed;
  private String hotelId;
  private String ratePlanCode;
  private Boolean printRate;
  private Boolean discountAllowed;
  private Boolean redemption;
  private Boolean bARRate;
  private Boolean daily;
  private Boolean tiered;
  private Boolean dayUse;
  private Boolean dayType;
  private Boolean complimentary;
  private Boolean houseUse;
  private Boolean negotiated;
  private Boolean ownerRate;
  private Boolean membershipEligible;
  private Boolean advancedDaily;
  private Boolean advancedDailyRate;

}
