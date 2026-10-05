package uk.co.whitbread.digitalkey.domain.ports.primary;

public interface CharacterUdfInPort {

  void updateUdfc20(String reservationId, String hotelId, String ciolStatus);

}