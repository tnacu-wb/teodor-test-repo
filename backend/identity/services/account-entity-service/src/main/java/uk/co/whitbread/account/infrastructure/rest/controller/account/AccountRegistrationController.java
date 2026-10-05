package uk.co.whitbread.account.infrastructure.rest.controller.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequestV2;
import uk.co.whitbread.account.domain.ports.primary.AccountRegistrationPort;
import uk.co.whitbread.account.domain.ports.primary.MarketingPreferencesPort;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper.MarketingPreferencesMapper;
import uk.co.whitbread.account.infrastructure.rest.controller.account.mapper.AccountRegistrationMapper;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.AccountRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.out.AccountRegistrationResponseDto;

@RequestMapping("/v1/account")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AccountRegistrationController implements AccountRegistrationApi {
  private final AccountRegistrationMapper accountRegistrationMapper;
  private final AccountRegistrationPort accountRegistrationPort;
  private final MarketingPreferencesMapper marketingPreferencesMapper;
  private final MarketingPreferencesPort marketingPreferencesPort;

  @Override
  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountRegistrationResponseDto> createAccount(
      @Valid @RequestBody AccountRegistrationRequestDto accountRegistrationRequestDto) {

    AccountRegistrationRequest accountRegistrationRequest
        = accountRegistrationMapper.toModel(accountRegistrationRequestDto);

    AccountRegistrationResponseDto status = accountRegistrationMapper.toDto(
        accountRegistrationPort.registerAccount(
            accountRegistrationRequest,
                accountRegistrationRequestDto.getCountry(),
                accountRegistrationRequestDto.getLanguage()));

    if (accountRegistrationRequestDto.getUpdatePreferencesRequest() == null) {
      log.debug("Skipping marketing opt-in update because request is null");
    } else {
      accountRegistrationRequestDto.getUpdatePreferencesRequest()
          .setContactValue(accountRegistrationRequestDto.getContactDetail().getEmail());
      MarketingPreferencesRequestV2 marketingPreferencesRequest
          = marketingPreferencesMapper.toMarketingPreferencesRequestModel(
          accountRegistrationRequestDto.getUpdatePreferencesRequest());
      try {
        marketingPreferencesPort.updateMarketingPreferences(marketingPreferencesRequest);
      } catch (Exception ex) {
        log.warn("Marketing opt-in call failed", ex);
      }
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(status);
  }
}
