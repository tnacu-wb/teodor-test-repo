package uk.co.whitbread.avail.business.events.infrastructure.client.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.avail.business.events.infrastructure.client.content.service.ContentClient;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalConfigDto;

@ExtendWith(MockitoExtension.class)
class ContentOutPortTest{

  private static final String HOTEL_1 = "Hotel1";
  private static final String HOTEL_2 = "Hotel2";
  @Mock
  private ContentClient contentClient;
  @InjectMocks
  private ContentOutPortImpl contentOutPort;

  @Test
  void getHotelsWithCityTax_success() {
    //Arrange
    var globalConfigDto = new GlobalConfigDto();
    globalConfigDto.setHotelsWithCityTax(java.util.Arrays.asList(HOTEL_1, HOTEL_2));
    when(contentClient.getGlobalConfig(any(), any())).thenReturn(globalConfigDto);

    //Act
    var response = contentOutPort.getHotelsWithCityTax();

    //Assert
    assertNotNull(response);
    assertEquals(HOTEL_1, response.get(0));
    assertEquals(HOTEL_2, response.get(1));
  }
}