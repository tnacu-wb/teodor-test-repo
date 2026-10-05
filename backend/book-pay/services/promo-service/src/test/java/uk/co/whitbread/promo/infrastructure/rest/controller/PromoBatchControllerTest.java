package uk.co.whitbread.promo.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoBatchRequest;
import uk.co.whitbread.promo.domain.model.promobatch.in.PromoKindRequest;
import uk.co.whitbread.promo.domain.model.promobatch.out.*;
import uk.co.whitbread.promo.domain.model.promocode.out.PromoCodeStatus;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoBatchDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoBatchRequestDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoBatchSummaryDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoKindRequestDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoBatchRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoKindRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchSummaryDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoKindResponseDto;

@ExtendWith(MockitoExtension.class)
class PromoBatchControllerTest {

  @InjectMocks private PromoBatchController promoBatchController;
  @Mock private PromoBatchInPort promoBatchInPort;
  @Mock private PromoBatchRequestDtoMapper promoBatchRequestDtoMapper;
  @Mock private PromoBatchDtoMapper promoBatchDtoMapper;
  @Mock private PromoBatchSummaryDtoMapper promoBatchSummaryDtoMapper;
  @Mock private PromoKindRequestDtoMapper promoKindRequestDtoMapper;

  private static final String TEST_PREFIX = "SALE10";
  private static final int TEST_BATCH_COUNT = 100_000;
  private static final int TEST_CODE_LENGTH = 6;
  private static final LocalDate TEST_EXPIRY_DATE = LocalDate.of(2026, 3, 31);
  private static final String TEST_OPERA_CODE = "ABCD";

  @Test
  void createPromoBatch_shouldReturnDto() {
    PromoBatchRequestDto requestDto = createPromoBatchRequestDto();
    PromoBatchRequest domainRequest = createPromoBatchRequest();
    PromoBatchResponse domainResponse = createPromoBatchResponse();
    PromoBatchResponseDto responseDto = createPromoBatchResponseDto();

    when(promoBatchRequestDtoMapper.toModel(any(PromoBatchRequestDto.class))).thenReturn(domainRequest);
    when(promoBatchInPort.createPromoBatch(any(PromoBatchRequest.class))).thenReturn(domainResponse);
    when(promoBatchDtoMapper.toDto(any(PromoBatchResponse.class))).thenReturn(responseDto);

    PromoBatchResponseDto result = promoBatchController.createPromoBatch(requestDto);

    assertNotNull(result);
    assertEquals(PromoBatchStatus.PENDING, result.getStatus());
    verify(promoBatchInPort).createPromoBatch(domainRequest);
  }

  @Test
  void getPromoBatchById_shouldReturnSummaryDto() {
    UUID batchId = UUID.randomUUID();
    PromoBatchSummary domainSummary = PromoBatchSummary.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .build();
    PromoBatchSummaryDto summaryDto = PromoBatchSummaryDto.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .build();

    when(promoBatchInPort.getPromoBatchById(batchId)).thenReturn(domainSummary);
    when(promoBatchSummaryDtoMapper.toDto(domainSummary)).thenReturn(summaryDto);

    PromoBatchSummaryDto result = promoBatchController.getPromoBatchById(batchId);

    assertNotNull(result);
    assertEquals(batchId, result.getBatchId());
  }

  @Test
  void getPromoBatchSummary_shouldReturnPagedResponse() {
    Pageable pageable = PageRequest.of(0, 20);
    UUID batchId = UUID.randomUUID();
    PromoBatchSummary domainSummary = PromoBatchSummary.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .build();
    PromoBatchSummaryPage domainPage = PromoBatchSummaryPage.builder()
        .promoBatchSummary(List.of(domainSummary))
        .pageable(pageable)
        .totalElements(1L)
        .totalPages(1)
        .pageSize(20)
        .build();
    PromoBatchSummaryDto summaryDto = PromoBatchSummaryDto.builder()
        .batchId(batchId)
        .status(PromoBatchStatus.COMPLETED)
        .build();

    when(promoBatchInPort.getPromoBatchSummary(pageable)).thenReturn(domainPage);
    when(promoBatchSummaryDtoMapper.toDto(domainSummary)).thenReturn(summaryDto);

    var response = promoBatchController.getPromoBatchSummary(pageable);

    assertNotNull(response);
    assertEquals(1, response.getItems().size());
    assertEquals(batchId, ((PromoBatchSummaryDto) response.getItems().get(0)).getBatchId());
  }

  @Test
  void markPromoBatchAsDownloaded_shouldDelegateToInPort() {
    UUID batchId = UUID.randomUUID();
    promoBatchController.markPromoBatchAsDownloaded(batchId);
    verify(promoBatchInPort).markAsDownloaded(batchId);
  }

  @Test
  void validatePromoKind_shouldReturnDto() {
    PromoKindRequestDto requestDto = createPromoKindRequestDto();
    PromoKindRequest request = createPromoKindRequest();

    PromoKindResponse domainResponse = PromoKindResponse.builder()
            .promoKind(PromoKind.UNIQUE)
            .operaPromoCode("PROMO123")
            .uniquePromoCodeStatus(PromoCodeStatus.ISSUED)
            .build();

    PromoKindResponseDto dtoResponse = new PromoKindResponseDto();
    dtoResponse.setPromoKind(PromoKind.UNIQUE);
    dtoResponse.setOperaPromoCode("PROMO123");
    dtoResponse.setUniquePromoCodeStatus(PromoCodeStatus.ISSUED);

    when(promoKindRequestDtoMapper.toModel(requestDto)).thenReturn(request);
    when(promoBatchInPort.validatePromoKind(request)).thenReturn(domainResponse);
    when(promoBatchDtoMapper.toPromoKindDto(domainResponse)).thenReturn(dtoResponse);

    PromoKindResponseDto result = promoBatchController.validatePromoKind(requestDto);

    assertNotNull(result);
    assertEquals(PromoKind.UNIQUE, result.getPromoKind());
    assertEquals("PROMO123", result.getOperaPromoCode());
    assertEquals(PromoCodeStatus.ISSUED, result.getUniquePromoCodeStatus());

    verify(promoKindRequestDtoMapper).toModel(requestDto);
    verify(promoBatchInPort).validatePromoKind(request);
    verify(promoBatchDtoMapper).toPromoKindDto(domainResponse);
  }

  // Helper methods
  private PromoBatchRequestDto createPromoBatchRequestDto() {
    return PromoBatchRequestDto.builder()
        .prefix(TEST_PREFIX)
        .batchCount(TEST_BATCH_COUNT)
        .codeLength(TEST_CODE_LENGTH)
        .expiryDate(TEST_EXPIRY_DATE)
        .operaPromoCode(TEST_OPERA_CODE)
        .build();
  }

  private PromoBatchRequest createPromoBatchRequest() {
    return PromoBatchRequest.builder()
        .prefix(TEST_PREFIX)
        .batchCount(TEST_BATCH_COUNT)
        .codeLength(TEST_CODE_LENGTH)
        .expiryDate(TEST_EXPIRY_DATE)
        .operaPromoCode(TEST_OPERA_CODE)
        .build();
  }

  private PromoBatchResponse createPromoBatchResponse() {
    return PromoBatchResponse.builder()
        .batchId(UUID.randomUUID())
        .status(PromoBatchStatus.PENDING)
        .build();
  }

  private PromoBatchResponseDto createPromoBatchResponseDto() {
    return PromoBatchResponseDto.builder()
        .batchId(UUID.randomUUID())
        .status(PromoBatchStatus.PENDING)
        .build();
  }

  private PromoKindRequestDto createPromoKindRequestDto() {
    return PromoKindRequestDto.builder()
            .promoCode("PROMO123")
            .country("GB")
            .channel("PI")
            .subChannel("WEB")
            .build();
  }

  private PromoKindRequest createPromoKindRequest() {
    return PromoKindRequest.builder()
            .promoCode("PROMO123")
            .country("gb")
            .channel("pi")
            .subChannel("WEB")
            .build();
  }
}
