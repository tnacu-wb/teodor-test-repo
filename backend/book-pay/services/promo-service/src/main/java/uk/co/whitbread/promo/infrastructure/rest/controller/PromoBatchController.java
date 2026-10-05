package uk.co.whitbread.promo.infrastructure.rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummaryPage;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;
import uk.co.whitbread.promo.infrastructure.rest.controller.documentation.PromoBatchApiDocumentation;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoBatchDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoBatchRequestDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoBatchSummaryDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.PromoKindRequestDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.RedeemPromoCodeRequestDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.mapper.RedeemPromoCodeResponseDtoMapper;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoBatchRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.PromoKindRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.in.RedeemPromoCodeRequestDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PagedResponse;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchSummaryDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoKindResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.RedeemPromoCodeResponseDto;
import uk.co.whitbread.promo.infrastructure.rest.utils.PageMapper;

@RestController
@RequestMapping("/v1/promo/batches")
@RequiredArgsConstructor
public class PromoBatchController implements PromoBatchApiDocumentation {

  private final PromoBatchRequestDtoMapper promoBatchRequestDtoMapper;
  private final PromoBatchInPort promoBatchInPort;
  private final PromoBatchDtoMapper promoBatchDtoMapper;
  private final PromoBatchSummaryDtoMapper promoBatchSummaryDtoMapper;
  private final RedeemPromoCodeRequestDtoMapper redeemPromoCodeRequestDtoMapper;
  private final RedeemPromoCodeResponseDtoMapper redeemPromoCodeResponseDtoMapper;
  private final PromoKindRequestDtoMapper promoKindRequestDtoMapper;

  @Override
  @PostMapping()
  public PromoBatchResponseDto createPromoBatch(
      @Valid @RequestBody PromoBatchRequestDto promoBatchRequestDto) {

    var promoBatchRequest = promoBatchRequestDtoMapper.toModel(promoBatchRequestDto);
    var promoBatchResponse = promoBatchInPort.createPromoBatch(promoBatchRequest);
    return promoBatchDtoMapper.toDto(promoBatchResponse);
  }

  @Override
  @GetMapping("/{batchId}")
  public PromoBatchSummaryDto getPromoBatchById(@PathVariable("batchId") UUID batchId) {
    var batchSummary = promoBatchInPort.getPromoBatchById(batchId);
    return promoBatchSummaryDtoMapper.toDto(batchSummary);
  }

  @Override
  @GetMapping()
  public PagedResponse<PromoBatchSummaryDto> getPromoBatchSummary(
      @PageableDefault(size = 20)
      @SortDefault.SortDefaults({
          @SortDefault(sort = "updatedAt", direction = Sort.Direction.DESC)
      }) Pageable pageable) {

    PromoBatchSummaryPage summaryPage = promoBatchInPort.getPromoBatchSummary(pageable);
    List<PromoBatchSummaryDto> summary = summaryPage.getPromoBatchSummary().stream()
        .map(promoBatchSummaryDtoMapper::toDto)
        .toList();

    Page<PromoBatchSummaryDto> dtoPage =
        new PageImpl<>(summary, summaryPage.getPageable(), summaryPage.getTotalElements());

    return PageMapper.toPagedResponse(dtoPage);
  }

  @Override
  @PatchMapping("/{batchId}/mark-downloaded")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markPromoBatchAsDownloaded(@Valid @PathVariable("batchId") UUID batchId) {
    promoBatchInPort.markAsDownloaded(batchId);
  }

  @Override
  @GetMapping("/promo-kind")
  public PromoKindResponseDto validatePromoKind(
          @Valid @ModelAttribute PromoKindRequestDto requestDto) {
    var request = promoKindRequestDtoMapper.toModel(requestDto);
    return promoBatchDtoMapper.toPromoKindDto(promoBatchInPort.validatePromoKind(request));
  }

  @Override
  @PostMapping("/redeem")
  public RedeemPromoCodeResponseDto redeemPromoCode(
      @RequestBody @Valid RedeemPromoCodeRequestDto requestDto) {
    var request = redeemPromoCodeRequestDtoMapper.toModel(requestDto);
    return redeemPromoCodeResponseDtoMapper.toModel(
        promoBatchInPort.redeemPromoCode(request)
    );
  }
}
