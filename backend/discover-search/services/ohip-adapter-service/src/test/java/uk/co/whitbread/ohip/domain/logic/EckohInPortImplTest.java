package uk.co.whitbread.ohip.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.eckoh.in.CardScheme;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohCardType;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;
import uk.co.whitbread.ohip.domain.ports.secondary.EckohOutPort;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EckohInPortImplTest {

  @InjectMocks
  private EckohInPortImpl eckohInPort;

  @Mock
  private EckohOutPort eckohOutPort;


  @Test
  void eckohWebhook_Success() {
    //Arrange
    var request = createEckohWebhookRequest("1226", CardScheme.VISA, "succes", 100);

    //Act
    eckohInPort.eckohWebhook(request);

    //Assert
    verify(eckohOutPort, times(1)).eckohWebhook(request);

  }

  @Test
  void eckohWebhookChina_UnionPayScheme_Success() {
    //Arrange
    var request = createEckohWebhookRequest("1226", CardScheme.CHINA_UNIONPAY, "succes", 100);

    //Act
    eckohInPort.eckohWebhook(request);

    //Assert
    verify(eckohOutPort, times(1)).eckohWebhook(request);

  }
  @Test
  void eckohWebhook_UnSuccess() {
    //Arrange
    var request = createEckohWebhookRequest("1226", CardScheme.VISA, "failed", 123);

    //Act
    eckohInPort.eckohWebhook(request);

    //Assert
    verify(eckohOutPort, times(0)).eckohWebhook(request);

  }
  private EckohWebhook createEckohWebhookRequest(String expiry, CardScheme scheme, String result, int resultCode) {
    return EckohWebhook.builder()
        .result(result)
        .resultCode(resultCode)
        .maskedPan("444433XXXXXX1111")
        .expiry(expiry)
        .scheme(scheme.name())
        .type(EckohCardType.CREDIT)
        .token("4216333880397891103")
        .reference("15951")
        .build();
  }
}
