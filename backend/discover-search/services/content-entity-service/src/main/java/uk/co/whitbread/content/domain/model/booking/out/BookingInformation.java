package uk.co.whitbread.content.domain.model.booking.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingInformation {

  private String brand;
  private List<BookingFlowStep> bookingFlowSteps;
  private List<Item> upsellItems;
  private String restaurantClosedTitle;
  private String restaurantClosedMessage;
  private String mealsNotAvailableTitle;
  private String mealsNotAvailableMessage;
  private BookingDonation bookingDonation;
  private List<InfoMessage> infoMessages;
  private List<PaymentInfoMessage> paymentInfoMessages;
  private PrivacyPolicy privacyPolicy;
  private List<TermsAndConditions> termsAndConditions;
  private List<PromotionPanel> promotionPanels;
  private List<BookingSpinnerConfig> bookingSpinnerConfig;

}