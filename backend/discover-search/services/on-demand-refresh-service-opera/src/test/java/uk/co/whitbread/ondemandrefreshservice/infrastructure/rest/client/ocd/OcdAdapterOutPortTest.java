package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ocd.adapter.service.generated.models.PriceInfoDto;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxResponseDto;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service.OcdAdapterClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcdAdapterOutPortTest {

  public static final BigDecimal AMOUNT_AFTER_TAX = BigDecimal.TEN;
  @Mock
  private OcdAdapterClient ocdAdapterClient;
  @InjectMocks
  private OcdAdapterOutPortImpl ocdAdapterOutPort;

  @Test
  void getHotelsWithCityTax_success() {
    //Arrange
    var taxResponseDto = new TaxResponseDto();
    taxResponseDto.setTotal(new PriceInfoDto().amountAfterTax(AMOUNT_AFTER_TAX));
    when(ocdAdapterClient.getTax(any(), any(), any(), any(), any(), any())).thenReturn(taxResponseDto);

    //Act
    var response = ocdAdapterOutPort.getAmountAfterTax("hotelId", "2023-10-10", "2023-10-12", 2, "ratePlanCode",
        "roomType");

    //Assert
    assertNotNull(response);
    assertEquals(AMOUNT_AFTER_TAX, response);
  }

}
