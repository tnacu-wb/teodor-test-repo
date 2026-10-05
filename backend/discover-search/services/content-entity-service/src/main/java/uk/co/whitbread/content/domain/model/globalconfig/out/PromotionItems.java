package uk.co.whitbread.content.domain.model.globalconfig.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionItems {

  private boolean enabled;
  private String promoCode;
  private String landingPage;
  private Integer numberOfNights;
  private Integer maxRooms;
  private LocalDate bookingStartDate;
  private LocalDate bookingEndDate;
  private LocalDate stayStartDate;
  private LocalDate stayEndDate;
  private String promoBannerColour;
  private String promoBannerIcon;
  private String promoBannerTitle;
  private String promoBannerSubtitle;
  private String promoInvalidMessage;
  private String promoExpiredMessage;
  private String promoAmendMessage;
  private String termsLink;
  private String appPromoBannerTitle;
  private String appPromoBannerSubtitle;
  private String appPromoInvalidMessage;
  private String appPromoExpiredMessage;
  private String appPromoAmendMessage;
  private String ratePlanCode;
  private List<String> roomClass;
  private String promoWrongRoomClassAndRatePlanMessage;
  private String promoWrongRatePlanMessage;
  private String promoWrongRoomClassMessage;
  private List<String> promoBannerVisibility;
  private Integer minRooms;
}
