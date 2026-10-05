package uk.co.whitbread.ocd.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.domain.model.tax.out.PriceInfo;
import uk.co.whitbread.ocd.domain.model.tax.out.Tax;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxDetails;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;
import uk.co.whitbread.ocd.domain.ports.secondary.TaxInfoOutPort;

@ExtendWith(MockitoExtension.class)
public class TaxInfoInPortImplTest {

  @InjectMocks
  private TaxInfoInPortImpl taxInfoInPort;

  @Mock
  private TaxInfoOutPort taxInfoOutPort;

  @Test
  void getTaxDetails__ShouldReturnOK() {

    //Arrange
    var request = mockTaxRequest();
    when(taxInfoOutPort.getTaxDetails(any())).thenReturn(createTaxResponse());

    //Act
    var response = taxInfoInPort.getTaxDetails(request);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(new BigDecimal("120"), response.getTotal().getAmountAfterTax());
    assertEquals(new BigDecimal("100"), response.getTotal().getAmountBeforeTax());
    verifyNoMoreInteractions(taxInfoOutPort);
  }

  private TaxRequest mockTaxRequest() {
    return TaxRequest.builder()
        .hotelId("BRECIT")
        .adults(1)
        .roomType("FLEXRATE")
        .roomType("DOUBLE")
        .arrivalDate("2025-10-10")
        .departureDate("2025-10-11")
        .build();
  }

  private TaxResponse createTaxResponse() {
    Tax tax = Tax.builder()
        .code("CITYTAX")
        .description("City Tax")
        .amount(new BigDecimal(20))
        .currencyCode("GBP")
        .build();
    TaxDetails taxDetails = TaxDetails.builder()
        .amount(new BigDecimal(20))
        .currencyCode("GBP")
        .tax(Collections.singletonList(tax))
        .build();
    PriceInfo total = PriceInfo.builder()
        .amountBeforeTax(new BigDecimal("100"))
        .amountAfterTax(new BigDecimal("120"))
        .currencyCode("GBP")
        .taxes(taxDetails)
        .build();
    return TaxResponse.builder()
        .roomType("DOUBLE")
        .ratePlanCode("FLEXRATE")
        .total(total)
    .build();
  }

}
