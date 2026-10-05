package uk.co.whitbread.ocd.infrastructure.rest.controller.tax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.ocd.domain.ports.primary.TaxInfoInPort;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper.TaxRequestMapper;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper.TaxResponseMapper;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.in.TaxRequestDto;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out.PriceInfoDto;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out.TaxResponseDto;

@ExtendWith(MockitoExtension.class)
public class TaxControllerTest {

  @Mock
  private TaxRequestMapper taxRequestMapper;

  @Mock
  private TaxResponseMapper taxResponseMapper;

  @Mock
  private TaxInfoInPort taxInfoInPort;

  @InjectMocks
  private TaxController taxController;

  @Test
  void getTaxDetails__ShouldReturnOk() {
    //Arrange
    var request = mockTaxRequest();
    TaxRequest taxRequest = TaxRequest.builder().build();
    TaxResponseDto taxResponseDto = TaxResponseDto.builder()
        .total(PriceInfoDto.builder().amountAfterTax(new BigDecimal(120)).build())
        .build();
    when(taxInfoInPort.getTaxDetails(any())).thenReturn(createTaxResponse());
    when(taxRequestMapper.toModel(any(), any())).thenReturn(taxRequest);
    when(taxResponseMapper.toDto(any())).thenReturn(taxResponseDto);

    //Act
    var response = taxController.getTaxDetails("BRECIT", request);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(new BigDecimal("120"), response.getBody().getTotal().getAmountAfterTax());
  }

  private TaxRequestDto mockTaxRequest() {
    TaxRequestDto taxRequestDto = new TaxRequestDto();
    taxRequestDto.setAdults(1);
    taxRequestDto.setRoomType("FLEXRATE");
    taxRequestDto.setRoomType("DOUBLE");
    taxRequestDto.setArrivalDate("2025-10-10");
    taxRequestDto.setDepartureDate("2025-10-11");
    return taxRequestDto;
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
