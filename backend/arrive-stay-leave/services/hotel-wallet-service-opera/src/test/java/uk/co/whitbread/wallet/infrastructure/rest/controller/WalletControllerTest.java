package uk.co.whitbread.wallet.infrastructure.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.ports.primary.WalletGeneratorInPort;
import uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.WalletController;
import uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.mapper.WalletRequestMapper;
import uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.model.in.WalletRequestDto;

@ExtendWith({MockitoExtension.class})
class WalletControllerTest {

  @InjectMocks
  private WalletController walletController;

  @Mock
  private WalletGeneratorInPort walletGeneratorInPort;

  @Mock
  private WalletRequestMapper walletRequestMapper;

  @Test
  void getHotelWalletTest() {
    byte[] walletPassBytes = {21, 121, 101, 45, 62, 118, 101, 114, 61, 101, 98};

    when(walletGeneratorInPort.generateWalletPass(any())).thenReturn(walletPassBytes);
    when(walletRequestMapper.toModel(any())).thenReturn(new WalletRequest());

    var response = walletController.getHotelWallet(new WalletRequestDto());
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), Arrays.toString(response.getBody()),
        Arrays.toString(walletPassBytes));

  }
}
