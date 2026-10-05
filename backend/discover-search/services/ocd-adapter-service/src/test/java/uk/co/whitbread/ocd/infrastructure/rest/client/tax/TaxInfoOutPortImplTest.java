package uk.co.whitbread.ocd.infrastructure.rest.client.tax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Offer;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsResponse;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.domain.model.tax.out.PriceInfo;
import uk.co.whitbread.ocd.domain.model.tax.out.Tax;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxDetails;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.OcdClient;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper.OfferDetailsResponseMapper;

@ExtendWith(MockitoExtension.class)
public class TaxInfoOutPortImplTest {

  @InjectMocks
  private TaxInfoOutPortImpl taxInfoOutPortImpl;

  @Mock
  private OcdClient ocdClient;

  @Mock
  private OfferDetailsResponseMapper offerDetailsResponseMapper;

  @Test
  void getTaxDetails__ShouldReturnOK() {

    //Arrange
    var request = mockTaxRequest();
    when(ocdClient.getTaxDetails(any())).thenReturn(mockOfferDetailsResponse());
    when(offerDetailsResponseMapper.toModel(any())).thenReturn(createTaxResponse());

    //Act
    var response = taxInfoOutPortImpl.getTaxDetails(request);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(new BigDecimal("120"), response.getTotal().getAmountAfterTax());
    assertEquals(new BigDecimal("100"), response.getTotal().getAmountBeforeTax());
  }

  @Test
  void getTaxDetails__ShouldReturnOK_with_EmptyObject() {

    //Arrange
    var request = mockTaxRequest();
    when(ocdClient.getTaxDetails(any())).thenReturn(null);

    //Act
    var response = taxInfoOutPortImpl.getTaxDetails(request);

    //Assert
    assertThat(response, notNullValue());
    assertNull(response.getTotal());
  }

  @Test
  void getTaxDetails__ShouldReturnOK_with_EmptyObject_1() {

    //Arrange
    var request = mockTaxRequest();
    when(ocdClient.getTaxDetails(any())).thenReturn(new OfferDetailsResponse());

    //Act
    var response = taxInfoOutPortImpl.getTaxDetails(request);

    //Assert
    assertThat(response, notNullValue());
    assertNull(response.getTotal());
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

  private OfferDetailsResponse mockOfferDetailsResponse() {
    Offer offer = new Offer();
    offer.setRatePlanCode("FLEXRATE");
    OfferDetailsResponse offerDetailsResponse = new OfferDetailsResponse();
    offerDetailsResponse.setOffer(offer);
    return offerDetailsResponse;
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
