package uk.co.whitbread.ohip.infrastructure.rest.client.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PromotionType;

class PromotionUtilsTest {

    private static final String LONG_PROMO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // > 20 chars
    private static final String SHORT_PROMO = "SHORT_PROMO";

    @Test
    void shouldTruncatePromotionName_ForHotelReservationType_WhenLengthGreaterThan20() {
        // Arrange
        HotelReservationType reservation = buildReservationType(LONG_PROMO);

        // Act
        PromotionUtils.truncatePromotion(reservation);

        // Assert
        assertEquals("ABCDEFGHIJKLMNOPQ...",
                reservation.getRoomStay().getPromotion().getPromotionName());
    }

    @Test
    void shouldNotTruncatePromotionName_ForHotelReservationType_WhenLengthLessThanOrEqual20() {
        // Arrange
        HotelReservationType reservation = buildReservationType(SHORT_PROMO);

        // Act
        PromotionUtils.truncatePromotion(reservation);

        // Assert
        assertEquals(SHORT_PROMO,
                reservation.getRoomStay().getPromotion().getPromotionName());
    }

    @Test
    void shouldTruncatePromotionName_ForInstructionType_WhenLengthGreaterThan20() {
        // Arrange
        HotelReservationInstructionType instruction = buildInstructionType(LONG_PROMO);

        // Act
        PromotionUtils.truncatePromotion(instruction);

        // Assert
        assertEquals("ABCDEFGHIJKLMNOPQ...",
                instruction.getRoomStay().getPromotion().getPromotionName());
    }

    @Test
    void shouldTruncateStringCorrectly() {
        // Arrange
        String input = LONG_PROMO;

        // Act
        String result = PromotionUtils.truncate(input);

        // Assert
        assertEquals("ABCDEFGHIJKLMNOPQ...", result);
    }


    private HotelReservationType buildReservationType(String promoName) {
        PromotionType promotion = new PromotionType();
        promotion.setPromotionName(promoName);

        RoomStayType roomStay = new RoomStayType();
        roomStay.setPromotion(promotion);

        HotelReservationType reservation = new HotelReservationType();
        reservation.setRoomStay(roomStay);

        return reservation;
    }

    private HotelReservationInstructionType buildInstructionType(String promoName) {
        PromotionType promotion = new PromotionType();
        promotion.setPromotionName(promoName);

        RoomStayType roomStay = new RoomStayType();
        roomStay.setPromotion(promotion);

        HotelReservationInstructionType instruction = new HotelReservationInstructionType();
        instruction.setRoomStay(roomStay);

        return instruction;
    }
}
