package uk.co.whitbread.payments.infrastructure.rest.client.customers;

import static uk.co.whitbread.payments.domain.model.in.UserType.BUSINESS;
import static uk.co.whitbread.payments.domain.model.in.UserType.LEISURE;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_PERSONAL_STORED_CARD;
import static uk.co.whitbread.payments.domain.model.out.StoredCardType.LEISURE_STORED_CARD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.model.in.UserType;
import uk.co.whitbread.payments.domain.model.out.Card;
import uk.co.whitbread.payments.domain.model.out.CompanyDetailsResponse;
import uk.co.whitbread.payments.domain.model.out.CustomerAccount;
import uk.co.whitbread.payments.domain.ports.secondary.CustomerAccountPort;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper.AccountMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper.CompanyMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.PaymentCardDto;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.CompanyServiceClient;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.HotelAccountClient;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.HotelCardClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerAccountPortImpl implements CustomerAccountPort {

  private final HotelCardClient hotelCardClient;
  private final HotelAccountClient hotelAccountClient;
  private final CompanyServiceClient companyServiceClient;
  private final AccountMapper accountMapper;
  private final CompanyMapper companyMapper;

  @Override
  public List<Card> findCustomerSavedCards(String userId, UserType userType, String channel,
                                           CustomerAccount account, String authToken) {
    log.info("Entered find customers saved cards for userId = {}", userId);
    var savedCards = new ArrayList<Card>();
    if (BUSINESS.equals(userType)) {
      extractSavedCard(account, userType).ifPresent(savedCards::add);
      extractCentralSavedCard(account, channel, authToken).ifPresent(savedCards::add);
      return savedCards;
    } else if (LEISURE.equals(userType)) {
      extractSavedCard(account, userType).ifPresent(savedCards::add);
      return savedCards;
    }
    log.info("Skipped retrieving saved cards for anonymous customer");
    return Collections.emptyList();
  }


  @Override
  public CustomerAccount findCustomerAccount(String userId, UserType userType, String channel, String authToken) {
    return accountMapper.toCustomerAccountModel(hotelAccountClient.findCustomerAccount(userId,
          BUSINESS.equals(userType), channel, authToken));
  }

  @Override
  public Optional<CompanyDetailsResponse> findCompany(CustomerAccount account, String authToken) {
    var companyResponse = companyServiceClient.findCompany(account.getCompanyId(),
        account.getSessionId(),
        account.getBusiness().getEmployeeId(), authToken);
    return Optional.ofNullable(companyMapper.toCompanyModel(companyResponse));
  }


  private Optional<Card> extractSavedCard(CustomerAccount account, UserType userType) {
    log.info("Entered extract saved card for account");
    if (isPaymentCardPresent(account)) {
      var card = accountMapper.toCardModel(account.getPaymentPreference().getPaymentCard());
      card.setCardType(BUSINESS.toString().equals(userType.name())
          ? BUSINESS_PERSONAL_STORED_CARD.name() : LEISURE_STORED_CARD.name());
      return Optional.ofNullable(card);
    }
    return Optional.empty();
  }

  private Optional<Card> extractCentralSavedCard(CustomerAccount account, String channel,
      String authToken) {
    log.info("Entered extract central saved card for account");
    Optional<PaymentCardDto> centralCard = Optional.empty();
    if (account.getCompanyId() != null) {
      try {
        centralCard = hotelCardClient.findCentralStoredCard(account.getCompanyId(),
                    channel, account.getSessionId(), authToken).stream()
              .filter(
                    card -> card.getCardId().toString().equals(account.getBusiness().getCentralCard()))
              .findFirst();
      } catch (PaymentMethodsException e) {
        log.error("Unable to retrieve central stored cards for company={}", account.getCompanyId(), e);
      }
      if (centralCard.isPresent()) {
        var centralStoredCard = accountMapper.toCentralCardModel(centralCard.get());
        return Optional.ofNullable(centralStoredCard);
      }
    }
    return Optional.empty();
  }

  private boolean isPaymentCardPresent(CustomerAccount account) {
    return account.getPaymentPreference() != null
        && account.getPaymentPreference().getPaymentCard() != null
        && account.getPaymentPreference().getPaymentCard().getCardNumber() != null;
  }
}
