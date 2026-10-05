package uk.co.whitbread.hotel.account.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardNumberMapperTest {

  private static final String CARD_NUMBER = "1234567890123456";
  private static final String MASKED_CARD_NUMBER = "************3456";

  private CardNumberMapper cardNumberMapper;

  @BeforeEach
  void setUp() {
    cardNumberMapper = Mappers.getMapper(CardNumberMapper.class);
  }

  @Test
  void maskCardNumber_success() {
    String result = cardNumberMapper.maskCardNumber(CARD_NUMBER);
    assertEquals(MASKED_CARD_NUMBER, result);
  }
}
