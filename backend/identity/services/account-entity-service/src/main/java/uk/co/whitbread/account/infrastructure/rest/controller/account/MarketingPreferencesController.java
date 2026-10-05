package uk.co.whitbread.account.infrastructure.rest.controller.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.ports.primary.MarketingPreferencesPort;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper.MarketingPreferencesMapper;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestDto;

@RequestMapping("/v1/account")
@RestController
@Slf4j
@RequiredArgsConstructor
public class MarketingPreferencesController implements MarketingPreferencesApi {
  private final MarketingPreferencesMapper marketingPreferencesMapper;
  private final MarketingPreferencesPort marketingPreferencesPort;

  @PutMapping(value = "preferences",
      produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> updateMarketingPreferences(
      @Valid @RequestBody UpdatePreferencesRequestDto updatePreferencesRequestDto) {
    MarketingPreferencesRequest marketingPreferencesRequest
        = marketingPreferencesMapper.toMarketingPreferencesRequestModel(updatePreferencesRequestDto);
    marketingPreferencesPort.updateMarketingPreferences(marketingPreferencesRequest);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
