package uk.co.whitbread.basket.infrastructure.repository.id;

public interface BasketIdGenerator {
  String getId();

  String generateSortKey(String hotelId);

  String generateBasketId(String hotelId, String sortKey);

  String generateReference(String hotelId);

  String extractBasketIdHotelId(String basketId);

  String extractBasketIdSortKey(String basketId);
}
