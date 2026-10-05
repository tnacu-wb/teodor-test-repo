package uk.co.whitbread.infrastructure.rest.client.promotion;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.promotion.in.PromoKindRequest;
import uk.co.whitbread.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.promo.generated.models.promotion.PromoCodeStatus;
import uk.co.whitbread.promo.generated.models.promotion.PromoKind;
import uk.co.whitbread.promo.generated.models.promotion.PromoKindResponseDto;

@ExtendWith(MockitoExtension.class)
class PromotionOutPortImplTest {

  @InjectMocks
  private PromotionOutPortImpl promotionOutPort;

  @Mock
  private PromoServiceClient promoServiceClient;

  @Mock
  private PromoKindResponseMapper promoKindResponseMapper;

  @Test
  void shouldReturnMappedPromoKindResponse_whenPromoServiceReturnsData() {
    // given
    String promoCode = "PROMO123";

    PromoKindResponseDto serviceResponse =
        new PromoKindResponseDto()
            .promoKind(PromoKind.UNIQUE)
            .operaPromoCode("OPERA123")
            .uniquePromoCodeStatus(PromoCodeStatus.ISSUED);

    PromoKindResponse mappedResponse =
        PromoKindResponse.builder()
            .promoKind(PromoKind.UNIQUE)
            .operaPromoCode("OPERA123")
            .uniquePromoCodeStatus(PromoCodeStatus.ISSUED)
            .build();

    when(promoServiceClient.getPromoKind(promoKindRequest(promoCode)))
        .thenReturn(serviceResponse);

    when(promoKindResponseMapper.toModel(serviceResponse))
        .thenReturn(mappedResponse);

    // when
    PromoKindResponse result = promotionOutPort.getPromoKind(promoKindRequest(promoCode));

    // then
    assertSame(mappedResponse, result);

    verify(promoServiceClient).getPromoKind(promoKindRequest(promoCode));
    verify(promoKindResponseMapper).toModel(serviceResponse);
  }

  private PromoKindRequest promoKindRequest(String promoCode) {
    return PromoKindRequest.builder()
            .promoCode(promoCode)
            .country("GB")
            .channel("PI")
            .subChannel("WEB")
            .build();
  }
}