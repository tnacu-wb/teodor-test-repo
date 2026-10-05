package uk.co.whitbread.basket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.getunleash.Unleash;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.model.basket.in.Charge;
import uk.co.whitbread.basket.domain.model.basket.in.ChargeAmount;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.generated.models.hotel.HotelInfoDto;
import uk.co.whitbread.basket.infrastructure.queue.producer.BasketOrderProducer;
import uk.co.whitbread.basket.infrastructure.repository.BasketRepository;
import uk.co.whitbread.basket.infrastructure.repository.PrepaidDepositRepository;
import uk.co.whitbread.basket.infrastructure.repository.id.BasketIdService;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketItemEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketItemTypeEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketStatusEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.PrepaidDepositEntity;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.service.HotelInfoClient;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.basket.infrastructure.rest.utils.DepositFolioUtils;
import uk.co.whitbread.shared.auth.security.PermissionEvaluator;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = BasketServiceApplication.class,
    properties = {
        "config.service.reservation.host=http://localhost:${wiremock.server.port}",
        "config.service.threec.host=http://localhost:${wiremock.server.port}",
        "config.service.content.host=http://localhost:${wiremock.server.port}",
        "config.service.hotel-entity.host=http://localhost:${wiremock.server.port}",
        "config.service.marketing.host=http://localhost:${wiremock.server.port}",
        "config.service.refund.host=http://localhost:${wiremock.server.port}",
        "config.service.rules-agent.host=http://localhost:${wiremock.server.port}",
        "config.service.ohip.host=http://localhost:${wiremock.server.port}/ohip",
        "config.service.cdh.host=http://localhost:${wiremock.server.port}",
        "config.service.promotion.host=http://localhost:${wiremock.server.port}"
    }
)
@EnableWireMock(@ConfigureWireMock(port = 0, filesUnderClasspath = "stubs"))
@DirtiesContext
public abstract class ContractVerifierBaseTest {

  public static final String BOOKING_REFERENCE = "LONHOL5778172";
  public static final String BASKET_ID = "LON-7b97e3f6-dce9-4572-b3e1-a5aa91ebc4b7";
  public static final String THREE_LETTER_HOTEL_ID = "LON";
  private final String HOTEL_ID = "LONEUS";

  @MockitoBean
  private BasketRepository mockedBasketRepository;

  @MockitoBean
  private HotelInfoClient hotelInfoClient;

  @MockitoBean
  private BasketIdService basketIdService;

  @MockitoBean
  private BasketOrderProducer basketOrderProducer;

  @MockitoBean
  private OhipAdapterClient ohipAdapterClient;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockitoBean(name = "permissionEvaluator")
  private PermissionEvaluator permissionEvaluator;

  @MockitoBean
  private PrepaidDepositRepository prepaidDepositEntity;

  @MockitoBean
  private UnleashWrapper unleashWrapper;

  @MockitoBean
  private Unleash unleash;

  @BeforeEach
  public void setUp() throws ExecutionException, InterruptedException, IOException {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    BasketEntity basketById = BasketEntity.builder()
        .hotelId(HOTEL_ID)
        .reference(BOOKING_REFERENCE)
        .basketId(BASKET_ID)
        .status(BasketStatusEntity.OPEN)
        .createdAt(Instant.now().toString())
        .lastModifiedAt(Instant.ofEpochMilli(1684426938941L).toString())
        .items(List.of(BasketItemEntity.builder()
            .sourceId("1234")
            .type("STAY")
            .build()))
        .itemTypes(List.of(BasketItemTypeEntity.builder()
            .type("STAY")
            .confirmationData(List.of("sourceId"))
            .build()))
        .paymentID("33661084018D")
        .build();
    given(mockedBasketRepository.getByBasketId(BASKET_ID)).willReturn(Optional.of(basketById));
    BasketEntity basketByReference = BasketEntity.builder()
        .hotelId(HOTEL_ID)
        .reference(BOOKING_REFERENCE)
        .basketId(BASKET_ID)
        .status(BasketStatusEntity.OPEN)
        .createdAt(Instant.now().toString())
        .lastModifiedAt(Instant.now().toString())
        .items(List.of(BasketItemEntity.builder()
            .sourceId("1234")
            .type("STAY")
            .build()))
        .itemTypes(List.of(BasketItemTypeEntity.builder()
            .type("STAY")
            .confirmationData(List.of("sourceId"))
            .build()))
        .paymentID("33661084018D")
        .channel("WEB")
        .paymentChannel("WEB")
        .build();
    given(mockedBasketRepository.getByReference(BOOKING_REFERENCE)).willReturn(Optional.of(basketByReference));
    given(hotelInfoClient.getHotelInfo(HOTEL_ID)).willReturn(hotelInfoDto());
    given(basketIdService.generateSortKey(THREE_LETTER_HOTEL_ID)).willReturn(THREE_LETTER_HOTEL_ID + "-" +
        UUID.randomUUID());
    given(mockedBasketRepository.updateBasket(any(BasketEntity.class))).willReturn(Mono.justOrEmpty(basketById));
    given(prepaidDepositEntity.get("123reservation")).willReturn(mockPrepaidDeposits());
    var mockedFeatureFlag = mock(FeatureFlag.class);
    given(unleashWrapper.featureFlag()).willReturn(mockedFeatureFlag);
    given(mockedFeatureFlag.getSavePaymentInstructionFolioThree()).willReturn(mock(Feature.class));
    given(unleashWrapper.isEnabled(mockedFeatureFlag.getSavePaymentInstructionFolioThree()))
        .willReturn(true);
    given(mockedFeatureFlag.getCompanyNameFeatureFlag()).willReturn(mock(Feature.class));
    given(unleashWrapper.isEnabled(mockedFeatureFlag.getCompanyNameFeatureFlag()))
        .willReturn(true);
    given(mockedBasketRepository.save(any(BasketEntity.class)))
        .willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    //auth
    RestAssuredMockMvc.authentication = mockMvcRequestSpecification -> {
      var authentication = new JwtAuthenticationToken(Mockito.mock(Jwt.class));
      authentication.setAuthenticated(true);
      mockMvcRequestSpecification.auth()
          .authentication(authentication);
    };
    when(permissionEvaluator.hasAccess(anyString())).thenReturn(true);
  }

  private HotelInfoDto hotelInfoDto() {
    HotelInfoDto hotelInfoDto = new HotelInfoDto();
    hotelInfoDto.setThreeLetterId(THREE_LETTER_HOTEL_ID);
    return hotelInfoDto;
  }

  private List<PrepaidDepositEntity> mockPrepaidDeposits() {

    var charges = List.of(
        Charge.builder()
            .postingReference("p-ref")
            .postingQuantity(12)
            .transactionCode("t-code")
            .chargeAmount(ChargeAmount.builder()
                .amount(BigDecimal.valueOf(12.4))
                .currencyCode("US")
                .build())
            .build(),
        Charge.builder()
            .postingReference("p-ref")
            .postingQuantity(13)
            .transactionCode("t-code2")
            .chargeAmount(ChargeAmount.builder()
                .amount(BigDecimal.valueOf(12.4))
                .currencyCode("US2")
                .build())
            .build());

    return List.of(
        PrepaidDepositEntity.builder()
            .paymentNo(11231231L)
            .reservationId("123reservation")
            .charges(DepositFolioUtils.getBytesFrom(charges))
            .build());
  }
}