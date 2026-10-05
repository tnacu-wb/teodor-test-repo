package uk.co.whitbread.basket.infrastructure.rest.client.promotion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.basket.domain.model.promotion.out.PromoKindResponse;
import uk.co.whitbread.basket.domain.model.promotion.out.RedeemPromoCodeResponse;
import uk.co.whitbread.basket.generated.models.promotion.PromoKindResponseDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemPromoCodeResponseDto;
import uk.co.whitbread.basket.generated.models.promotion.RedeemStatus;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper.PromoKindResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.mapper.RedeemPromoCodeResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.service.PromoServiceClient;

@ExtendWith(MockitoExtension.class)
class PromotionOutPortImplTest {

  private static final String PROMO_CODE = "BFYAKBWZPP";
  private static final String BOOKING_REF = "AQN7602623";

  @InjectMocks
  private PromoOutPortImpl promoOutPortImpl;

  @Mock
  private PromoServiceClient promoServiceClient;

  @Mock
  private PromoKindResponseMapper promoKindResponseMapper;

  @Mock
  private RedeemPromoCodeResponseMapper redeemPromoCodeResponseMapper;

  @BeforeEach
  void setUp() {
    promoOutPortImpl =
        new PromoOutPortImpl(
            promoServiceClient,
            promoKindResponseMapper,
            redeemPromoCodeResponseMapper
        );
  }

  @Test
  void testGetPromoKind_success() {
    // Arrange
    PromoKindResponseDto dto = new PromoKindResponseDto();
    PromoKindResponse model = new PromoKindResponse();

    when(promoServiceClient.getPromoKind(PROMO_CODE)).thenReturn(dto);
    when(promoKindResponseMapper.toModel(dto)).thenReturn(model);

    // Act
    PromoKindResponse response = promoOutPortImpl.getPromoKind(PROMO_CODE);

    // Assert
    assertThat(response, notNullValue());

    verify(promoServiceClient).getPromoKind(PROMO_CODE);
    verify(promoKindResponseMapper).toModel(dto);
    verifyNoMoreInteractions(
        promoServiceClient,
        promoKindResponseMapper,
        redeemPromoCodeResponseMapper
    );
  }

  @Test
  void testRedeemPromoCode_success() {
    // Arrange
    RedeemPromoCodeResponseDto dto = new RedeemPromoCodeResponseDto()
        .promoCode(PROMO_CODE)
        .status(RedeemStatus.REDEEMED);

    RedeemPromoCodeResponse model =
        RedeemPromoCodeResponse.builder()
            .promoCode(PROMO_CODE)
            .status(RedeemStatus.REDEEMED)
            .build();

    when(promoServiceClient.redeemPromoCode(PROMO_CODE, BOOKING_REF))
        .thenReturn(dto);
    when(redeemPromoCodeResponseMapper.toModel(dto))
        .thenReturn(model);

    // Act
    RedeemPromoCodeResponse response =
        promoOutPortImpl.redeemPromoCode(PROMO_CODE, BOOKING_REF);

    // Assert
    assertThat(response, notNullValue());

    verify(promoServiceClient).redeemPromoCode(PROMO_CODE, BOOKING_REF);
    verify(redeemPromoCodeResponseMapper).toModel(dto);
    verifyNoMoreInteractions(
        promoServiceClient,
        promoKindResponseMapper,
        redeemPromoCodeResponseMapper
    );
  }
}