package uk.co.whitbread.basket.infrastructure.rest.client.ohip;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ohip.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.generated.models.ohip.BillingAddressCaptRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.UpdateCustomReferenceNumberRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper.BillingAddressRequestOhipMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper.CustomReferenceNumberRequestOhipMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.BasketOhipOutPortImpl;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@ExtendWith(MockitoExtension.class)
class BasketOhipOutPortImplTest {

  private BasketOhipOutPortImpl basketOhipOutPort;
  @Mock
  private OhipAdapterClient ohipAdapterClient;
  @Mock
  private BillingAddressRequestOhipMapper billingAddressRequestOhipMapper;
  @Mock
  private CustomReferenceNumberRequestOhipMapper customReferenceNumberRequestOhipMapper;

  @BeforeEach
  public void before() {
    basketOhipOutPort = new BasketOhipOutPortImpl(
        ohipAdapterClient,
        billingAddressRequestOhipMapper,
        customReferenceNumberRequestOhipMapper);
  }

  @Test
  void testUpdateReservationBillingAddress() {
    List<String> reservationIds = Arrays.asList("123");

    when(billingAddressRequestOhipMapper.toDto(any(PaymentRequest.class), any(), anyBoolean(), anyBoolean(), anyBoolean()))
        .thenReturn(new BillingAddressCaptRequestDto());

    basketOhipOutPort
        .updateReservationBillingAddress(PaymentRequest.builder().build(), reservationIds, true, true, true);

    verify(ohipAdapterClient).sendReservationBillingAddress(any());
    verifyNoMoreInteractions(ohipAdapterClient);
  }

  @Test
  void testUpdateReservationBillingAddressCcui() {
    List<String> reservationIds = Arrays.asList("123");

    when(billingAddressRequestOhipMapper.toDto(any(CcuiPaymentRequest.class), any()))
        .thenReturn(new BillingAddressCaptRequestDto());

    basketOhipOutPort
        .updateReservationBillingAddressCcui(CcuiPaymentRequest.builder().build(), reservationIds);

    verify(ohipAdapterClient).sendReservationBillingAddress(any());
    verifyNoMoreInteractions(ohipAdapterClient);
  }

  @Test
  void testUpdateCustomReferenceNumber() {
    when(customReferenceNumberRequestOhipMapper.toDto(any()))
        .thenReturn(new UpdateCustomReferenceNumberRequestDto());

    basketOhipOutPort
        .updateCustomReferenceNumber(new UpdateCustomReferenceNumberRequest());

    verify(ohipAdapterClient).sendUpdateCustomReferenceNumber(any());
    verifyNoMoreInteractions(ohipAdapterClient);
  }
}