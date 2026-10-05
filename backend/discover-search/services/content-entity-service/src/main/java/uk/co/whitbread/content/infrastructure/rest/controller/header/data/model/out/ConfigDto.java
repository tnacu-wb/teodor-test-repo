package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDto {

  private RoomCodesDto roomCodes;
  private ApiDto api;
  private BookingSearchDto bookingSearch;
  private AuthenticationDto authentication;
  private PromotionBannerDto promotionBanner;
  private AmazonChatDto amazonChat;
}
