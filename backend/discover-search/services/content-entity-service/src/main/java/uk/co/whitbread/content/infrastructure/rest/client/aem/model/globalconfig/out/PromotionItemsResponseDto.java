package uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
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
public class PromotionItemsResponseDto {

  private boolean enabled;
  private String promoCode;
  private String landingPage;
  private Integer numberOfNights;
  private Integer maxRooms;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate bookingStartDate;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate bookingEndDate;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate stayStartDate;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate stayEndDate;

  private String promoBannerColour;
  private String promoBannerIcon;
  private String promoBannerTitle;
  private String promoBannerSubtitle;
  private String promoInvalidMessage;
  private String promoExpiredMessage;
  private String promoAmendMessage;
  private List<String> promoBannerVisibility;
}
