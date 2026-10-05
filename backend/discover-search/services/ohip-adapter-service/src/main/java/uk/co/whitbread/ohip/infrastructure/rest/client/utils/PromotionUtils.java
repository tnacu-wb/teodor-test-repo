package uk.co.whitbread.ohip.infrastructure.rest.client.utils;

import java.util.Optional;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;

public class PromotionUtils {
  private PromotionUtils() {

  }

  /**
   * Truncates the promotion name to 17 chars + "..." if longer than 20.
   */
  public static void truncatePromotion(HotelReservationType hotelReservationType) {
    Optional.ofNullable(hotelReservationType.getRoomStay())
        .map(RoomStayType::getPromotion).ifPresent(promotion -> {
          if (promotion.getPromotionName() != null && promotion.getPromotionName().length() > 20) {
            promotion.setPromotionName(truncate(promotion.getPromotionName()));
          }
        });
  }

  public static void truncatePromotion(HotelReservationInstructionType hotelReservationInstructionType) {
    Optional.ofNullable(hotelReservationInstructionType.getRoomStay())
        .map(RoomStayType::getPromotion).ifPresent(promotion -> {
          if (promotion.getPromotionName() != null && promotion.getPromotionName().length() > 20) {
            promotion.setPromotionName(truncate(promotion.getPromotionName()));
          }
        });
  }

  /**
   * Truncates a string to 17 chars and adds ellipsis.
   */
  public static String truncate(String value) {

    return value.substring(0, 17) + "...";
  }
}
