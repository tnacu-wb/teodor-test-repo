package uk.co.whitbread.account.infrastructure.rest.controller.account;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequestV2;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.domain.ports.primary.AccountRegistrationPort;
import uk.co.whitbread.account.domain.ports.primary.MarketingPreferencesPort;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper.MarketingPreferencesMapper;
import uk.co.whitbread.account.infrastructure.rest.controller.account.mapper.AccountRegistrationMapper;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.AccountRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.ContactDetailDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.CustomerDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.SourceDetailsDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestV2Dto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.out.AccountRegistrationResponseDto;

@ExtendWith(MockitoExtension.class)
public class AccountRegistrationControllerTest {

  private static final String COUNTRY = "GB";
  private static final String LANGUAGE = "EN";
  public static final String CAPTCHA = "CAPTCHA";
  public static final String PASSWORD = "PASSWORD";

  @Mock
  private AccountRegistrationMapper accountRegistrationMapper;
  @Mock
  private AccountRegistrationPort accountRegistrationPort;
  @Mock
  private MarketingPreferencesMapper marketingPreferencesMapper;
  @Mock
  private MarketingPreferencesPort marketingPreferencesPort;

  @InjectMocks
  private AccountRegistrationController underTest;

  @Test
  void validRequestCreatesAccountAndReturnsCreatedStatus() {
    // Arrange
    AccountRegistrationRequestDto requestDto = new AccountRegistrationRequestDto();

    // Act
    ResponseEntity<AccountRegistrationResponseDto> responseEntity = underTest.createAccount(
        requestDto);

    // Assert
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  void validRequestWithMarketingPreferences_callsMarketingPreferencesService() {
    // Arrange
    var requestDto = new AccountRegistrationRequestDto();
    requestDto.setCountry(COUNTRY);
    requestDto.setLanguage(LANGUAGE);

    var contact = new ContactDetailDto();
    contact.setEmail("big.boi@example.com");
    requestDto.setContactDetail(contact);

    requestDto.setUpdatePreferencesRequest(new UpdatePreferencesRequestV2Dto(
        null,
        true,
        true,
        false,
        false,
        new String[]{"PINN"},
        new CustomerDto("Mr", "Big", "Boi", "DE", "big.boi", "DE", "big.boi", "DE"),
        new SourceDetailsDto("WEB", "JOURNEY", "en_GB"),
        null
    ));

    when(accountRegistrationMapper.toModel(any(AccountRegistrationRequestDto.class)))
        .thenReturn(AccountRegistrationRequest.builder()
            .captcha(CAPTCHA)
            .password(PASSWORD)
            .contactDetail(validDomainContactDetail("random@example.com"))
            .basketReference(null)
            .marketingPreferencesRequest(null)
            .build());

    when(accountRegistrationPort.registerAccount(any(AccountRegistrationRequest.class), any(),
        any()))
        .thenReturn(new AccountRegistrationResponse(true, "SESSION", "CUSTOMER", false, false));

    when(accountRegistrationMapper.toDto(any(AccountRegistrationResponse.class)))
        .thenReturn(new AccountRegistrationResponseDto(true, "SESSION", "CUSTOMER", false, false));

    when(marketingPreferencesMapper.toMarketingPreferencesRequestModel(
        any(UpdatePreferencesRequestV2Dto.class)))
        .thenReturn(MarketingPreferencesRequestV2.builder().build());

    // Act
    ResponseEntity<AccountRegistrationResponseDto> responseEntity = underTest.createAccount(
        requestDto);

    // Assert
    assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
    verify(marketingPreferencesPort).updateMarketingPreferences(
        any(MarketingPreferencesRequestV2.class));
  }

  private uk.co.whitbread.account.domain.model.in.ContactDetail validDomainContactDetail(
      String email) {
    var cd = new uk.co.whitbread.account.domain.model.in.ContactDetail();
    cd.setEmail(email);
    cd.setAddress(new uk.co.whitbread.account.domain.model.in.Address());
    return cd;
  }
}
