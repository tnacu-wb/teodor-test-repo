package uk.co.whitbread.content.infrastructure.rest.client.booking.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AemBookingInformationDto {

  private String brand;
  private List<BookingFlowStepDto> bookingFlowSteps;
  private List<ItemDto> upsellitemsConfiguration;
  private String restaurantClosedTitle;
  private String restaurantClosedMessage;
  private String mealsNotAvailableTitle;
  private String mealsNotAvailableMessage;
  private String miscellaneousMessage;
  private PrivacyPolicyDto privacyPolicy;
  private List<InfoMessageDto> infoMessage;
  @JsonProperty("paymentInfoMessage")
  private List<PaymentInfoDto> paymentInfoMessages;
  private List<TermsAndConditionsDto> termsAndConditions;
  private DonationDto donation;
  private CheckinOnlineDto checkinOnlineText;
  private List<PromotionPanelDto> promotionPanels;
  private List<BookingSpinnerConfigDto> bookingSpinnerConfig;

}
