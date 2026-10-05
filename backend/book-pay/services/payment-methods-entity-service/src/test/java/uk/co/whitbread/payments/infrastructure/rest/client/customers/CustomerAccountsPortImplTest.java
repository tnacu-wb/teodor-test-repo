package uk.co.whitbread.payments.infrastructure.rest.client.customers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.co.whitbread.payments.domain.model.in.UserType.BUSINESS;
import static uk.co.whitbread.payments.domain.model.in.UserType.LEISURE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payments.domain.model.out.Card;
import uk.co.whitbread.payments.domain.model.out.CustomerAccount;
import uk.co.whitbread.payments.domain.model.out.PaymentCard;
import uk.co.whitbread.payments.domain.model.out.PaymentPreference;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper.AccountMapper;

@ExtendWith(MockitoExtension.class)
class CustomerAccountsPortImplTest {

  private static final String CUSTOMER_ID = "customerId";
  private static final String WEB = "WEB";
  private static final String JWT = "jwt";
  private static final String CARD_HOLDER_NAME = "John Smith";
  private static final Card CARD = new Card();

  @Mock
  private AccountMapper accountMapper;
  @InjectMocks
  private CustomerAccountPortImpl underTest;

  @Test
  void findCustomerSavedCards__noSavedCard() {

    // Act
    var result = underTest.findCustomerSavedCards(CUSTOMER_ID, BUSINESS, WEB, new CustomerAccount(), JWT);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.isEmpty(), equalTo(true));
    verifyNoInteractions(accountMapper);
  }

  @Test
  void findCustomerSavedCards__savedCard() {
    // Arrange
    var account = customerAccount(CARD_HOLDER_NAME);
    given(accountMapper.toCardModel(any())).willReturn(CARD);

    // Act
    var result = underTest.findCustomerSavedCards(CUSTOMER_ID, BUSINESS, WEB, account, JWT);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.get(0), is(CARD));
    verify(accountMapper).toCardModel(any());
  }

  @Test
  void findLeisureCustomerSavedCards_savedCard() {
    // Arrange
    var account = customerAccount(CARD_HOLDER_NAME);
    given(accountMapper.toCardModel(any())).willReturn(CARD);

    // Act
    var result = underTest.findCustomerSavedCards(CUSTOMER_ID, LEISURE, WEB, account, JWT);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.get(0), is(CARD));
    verify(accountMapper).toCardModel(any());
    verify(accountMapper, times(0)).toCentralCardModel(any());
  }

  private CustomerAccount customerAccount(String cardHolderName) {
    var paymentCard = new PaymentCard();
    paymentCard.setCardHolderName(cardHolderName);
    paymentCard.setCardNumber("12345");

    var paymentPreference = new PaymentPreference();
    paymentPreference.setPaymentCard(paymentCard);

    var customerAccount = new CustomerAccount();
    customerAccount.setPaymentPreference(paymentPreference);

    return customerAccount;
  }

}
