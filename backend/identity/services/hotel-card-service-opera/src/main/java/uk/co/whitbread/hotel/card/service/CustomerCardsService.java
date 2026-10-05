package uk.co.whitbread.hotel.card.service;

import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.co.whitbread.hotel.card.client.account.model.AccessLevel;
import uk.co.whitbread.hotel.card.client.payment.Payment3CP;
import uk.co.whitbread.hotel.card.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.card.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;
import uk.co.whitbread.hotel.card.exceptions.UnauthorizedSaveCentralCardException;
import uk.co.whitbread.hotel.card.exceptions.UnknownStrategyException;
import uk.co.whitbread.hotel.card.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.ProviderResponseDto;
import uk.co.whitbread.hotel.card.generated.models.payments.SaveCardRequestDto;
import uk.co.whitbread.hotel.card.mapper.AuthorizeScaMapper;
import uk.co.whitbread.hotel.card.mapper.SaveCardMapper;
import uk.co.whitbread.hotel.card.model.AuthorizeScaRequest;
import uk.co.whitbread.hotel.card.model.CardType;
import uk.co.whitbread.hotel.card.model.PaymentCardCommon;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentRequiredDetails;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.SaveCardRequest;
import uk.co.whitbread.hotel.card.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.card.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.card.service.cards.BBCentralCardService;
import uk.co.whitbread.hotel.card.service.cards.PaymentCardContext;
import uk.co.whitbread.hotel.card.service.cards.PaymentCardStrategy;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class CustomerCardsService {

  private final Payment3CP payment3CP;
  private final TokenService authTokenService;
  private final SaveCardMapper saveCardMapper;
  private final PaymentCardContext paymentCardContext;
  private final BBCentralCardService bbCentralCardService;
  private final AuthorizeScaMapper authorizeScaMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final UnleashContextProvider unleashContextProvider;
  private final AuthenticatedUserService authenticatedUserService;

  public PaymentRequiredDetails initiateSave(SaveCardRequest saveCardRequestDto, String authorization) {
    var saveCardRequest = saveCardMapper.toSaveCardRequest(saveCardRequestDto);
    if (saveCardRequestDto.getCardDetails().isBusiness()) {
      var accessLevel = authenticatedUserService.getAuthenticatedUser().getAccount()
          .getAccessLevel();
      if (!AccessLevel.SUPER.name().equalsIgnoreCase(accessLevel)
          && !saveCardRequestDto.getCardDetails().isPersonalCard()) {
        throw new UnauthorizedSaveCentralCardException("You are not authorized to add a company card");
      }
      var cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      saveCardRequest
          .employeeAccountId(Optional.ofNullable(cdhEmployeeDetails.getEmployeeAccountId()).orElseThrow(() -> new EmployeeNotFoundException("Employee account id is missing")))
          .companyAccountId(Optional.ofNullable(cdhEmployeeDetails.getCompanyAccountId()).orElseThrow(() -> new EmployeeNotFoundException("Company account id is missing")))
          .email(cdhEmployeeDetails.getUserEmail());
    } else {
      var customerAccountId = authTokenService.retrieveCustomerAccountIdAndVerifyToken(authorization);
      var customerEmail = authTokenService.retrieveEmailAndVerifyToken(authorization);
      saveCardRequest
          .accountId(customerAccountId.orElseThrow(() -> new AccountNotFoundException("Customer account id is missing")))
          .email(customerEmail.orElse(""));
    }
    if (isNoneCardTypeEdit(saveCardRequest)) {
      bbCentralCardService.updatePaymentCard(saveCardMapper.toPaymentCardBBCentral(saveCardRequest));
      return PaymentRequiredDetails.builder().build();
    } else {
      var paymentResponseDto = payment3CP.initiateSaveCard(saveCardRequest);
      var threeCResponse = Optional.ofNullable(paymentResponseDto)
          .map(PaymentResponseDto::getProviderResponse)
          .map(ProviderResponseDto::getThreecResponse)
          .orElseThrow(() -> new ThreeCPClientException("There is no 3CP response"));
      return PaymentRequiredDetails.builder()
          .paymentRedirect(threeCResponse.getiPageHtml())
          .sessionId(threeCResponse.getSessionId())
          .template(threeCResponse.getTemplate())
          .providerUrl(threeCResponse.getProviderUrl())
          .build();
    }
  }

  private boolean isNoneCardTypeEdit(SaveCardRequestDto saveCardRequest) {
    return StringUtils.isNoneBlank(saveCardRequest.getCardId()) && CardType.NONE.name().equalsIgnoreCase(saveCardRequest.getCardType());
  }

  public void updatePaymentCard(SaveCardPurpose saveCardPurpose, @Valid PaymentCardCommon paymentCard) {
    PaymentCardStrategy strategy = paymentCardContext.getStrategy(saveCardPurpose);
    if (strategy == null) {
      throw new UnknownStrategyException("Unknown strategy for save card purpose: " + saveCardPurpose);
    }

    // CDH call delay for specific users for testing purposes, controlled by Unleash feature toggle
    introduceCDHDelayForTestingPurposes(paymentCard);

    // applying temporary workaround for card type "PI"
    applyTemporaryCardTypeWorkaround(paymentCard);
    strategy.updatePaymentCard(paymentCard);
  }

  private void introduceCDHDelayForTestingPurposes(PaymentCardCommon paymentCard) {
    var context = new UnleashContext.Builder(unleashContextProvider.getContext())
        .userId(paymentCard.getUserEmail())
        .build();
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhDelay(), context)) {
      var variant = unleashWrapper.unleash()
          .getVariant(unleashWrapper.featureFlag().getCdhDelay().getKey(), context);
      variant.getPayload().ifPresent(payload -> {
        try {
          var delay = Integer.valueOf(payload.getValue());
          ThreadUtils.sleep(Duration.ofMillis(delay));
        } catch (NumberFormatException e) {
          log.info("Invalid delay CDH variant value set on Unleash toggle");
        } catch (InterruptedException e) {
          log.info("Could not sleep thread");
          Thread.currentThread().interrupt();
        }
      });
    }
  }

  public PaymentCardCommon mapPaymentCard(SaveCardPurpose saveCardPurpose, PaymentCardDTO paymentCard) {
    PaymentCardStrategy strategy = paymentCardContext.getStrategy(saveCardPurpose);
    if (strategy == null) {
      throw new UnknownStrategyException("Unknown strategy for save card purpose: " + saveCardPurpose);
    }
    return strategy.mapPaymentCard(paymentCard);
  }

  public PaymentRequiredDetails initiateAuthorizeSca(AuthorizeScaRequest authorizeScaRequest) {
    var authorizeScaRequestDto = authorizeScaMapper.toDto(authorizeScaRequest);
    var paymentResponseDto = payment3CP.initiateAuthorizeSca(authorizeScaRequestDto);
    var threeCResponse = Optional.ofNullable(paymentResponseDto)
            .map(PaymentResponseDto::getProviderResponse)
            .map(ProviderResponseDto::getThreecResponse)
            .orElseThrow(() -> new ThreeCPClientException("There is no 3CP response"));
    return PaymentRequiredDetails.builder()
            .paymentRedirect(threeCResponse.getiPageHtml())
            .sessionId(threeCResponse.getSessionId())
            .template(threeCResponse.getTemplate())
            .providerUrl(threeCResponse.getProviderUrl())
            .build();
  }

  /**
   * This method applies a temporary workaround for the card type "PI" due to a bug raised by the apps team.
   * This workaround will be removed once the long-term solution is in place.
   * <p>
   * ticket: <a href="https://whitbreadis.atlassian.net/browse/DNRQ-79116">DNRQ-79116</a>
   */
  private void applyTemporaryCardTypeWorkaround(PaymentCardCommon paymentCard) {
    if ("PI".equalsIgnoreCase(paymentCard.getCardType())) {
      paymentCard.setCardType("AT");
    }
  }
}
