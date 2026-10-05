package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingInformationDto {

  private String brand;
  private List<BookingFlowStepDto> bookingFlowSteps;
  private List<ItemDto> upsellItems;
  private String restaurantClosedTitle;
  private String restaurantClosedMessage;
  private String mealsNotAvailableTitle;
  private String mealsNotAvailableMessage;
  private DonationDto donation;
  private List<InfoMessageDto> infoMessages;
  private List<PaymentInfoMessageDto> paymentInfoMessages;
  private PrivacyPolicyDto privacyPolicy;
  private List<TermsAndConditionsDto> termsAndConditions;
  private List<PromotionPanelDto> promotionPanels;
  private List<BookingSpinnerConfigDto> bookingSpinnerConfig;
}
