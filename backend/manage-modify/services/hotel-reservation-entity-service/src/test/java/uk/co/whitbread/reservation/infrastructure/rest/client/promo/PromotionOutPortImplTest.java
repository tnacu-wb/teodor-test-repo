package uk.co.whitbread.reservation.infrastructure.rest.client.promo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.promo.service.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.reservation.domain.exceptions.PromotionException;
import uk.co.whitbread.reservation.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.PromotionOutPortImpl;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.PromotionClient;
import uk.co.whitbread.reservation.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PromotionOutPortImplTest {

  @Mock
  private PromotionClient promotionClient;

  @Mock
  private PromoKindResponseMapper promoKindResponseMapper;

  @InjectMocks
  private PromotionOutPortImpl promotionOutPort;


  @Test
  void getPromoKind_shouldReturnMappedResponse() {
    // Arrange
    String promoCode = "PROMO1";

    PromoKindResponseDto dto = new PromoKindResponseDto();
    PromoKindResponse model = new PromoKindResponse();

    when(promotionClient.getPromoKind(promoCode)).thenReturn(dto);
    when(promoKindResponseMapper.toModel(dto)).thenReturn(model);

    // Act
    PromoKindResponse result = promotionOutPort.getPromoKind(promoCode);

    // Assert
    assertNotNull(result);
    assertEquals(model, result);

    verify(promotionClient).getPromoKind(promoCode);
    verify(promoKindResponseMapper).toModel(dto);
  }

  @Test
  void getPromoKind_shouldPropagateException_whenClientFails() {
    // Arrange
    String promoCode = "BAD";

    when(promotionClient.getPromoKind(promoCode))
        .thenThrow(new PromotionException(ErrorCode.DIGITAL_NOT_MATCH_OTA_EXCEPTION, "no header"));

    // Act + Assert
    assertThrows(PromotionException.class,
        () -> promotionOutPort.getPromoKind(promoCode));

    verify(promotionClient).getPromoKind(promoCode);
    verifyNoInteractions(promoKindResponseMapper);
  }
}
