package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.ports.secondary.PaypalRuleRepositoryOutPort;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class PaypalRuleInPortImplTest {

  @Mock
  private PaypalRuleRepositoryOutPort paypalRuleRepositoryOutPort;


  @InjectMocks
  private PaypalRuleInPortImpl paypalRuleInPort;

  @Test
  void getPaypalRule__shouldThrowException() {
    //Arrange
    when(paypalRuleRepositoryOutPort.getPaypalHasAccess("PI", "NL", "hotel2")).thenReturn(false);

    //Act
    var response = paypalRuleInPort.getPaypalHasAccess("PI", "NL", "hotel2");

    //Assert

    assertThat(response.getIsPayPalPaymentEnabled(), is(Boolean.FALSE));
  }

  @Test
  void getAllPaypalRule__shouldRetrunOk() {
    //Arrange

    when(paypalRuleRepositoryOutPort.getPaypalHasAccess("PI", "GB", "hotel1")).thenReturn(true);

    //Act
    var response = paypalRuleInPort.getPaypalHasAccess("PI", "GB", "hotel1");

    //Assert

    assertThat(response.getIsPayPalPaymentEnabled(), is(Boolean.TRUE));
  }
}