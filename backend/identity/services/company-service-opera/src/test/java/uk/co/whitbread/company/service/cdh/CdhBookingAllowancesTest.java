package uk.co.whitbread.company.service.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.mapper.CompanyMapper;
import uk.co.whitbread.company.model.BookingAllowances;
import uk.co.whitbread.company.model.Price;
import uk.co.whitbread.company.model.PriceCapLocations;
import uk.co.whitbread.shared.cdh.model.Currency;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@ExtendWith(MockitoExtension.class)
class CdhBookingAllowancesTest {

  private static final String COMPANY_ID = "125";
  private static final String EMAIL = "user@mail.com";

  @Mock
  private CdhService mockCdhService;

  @Mock
  private CompanyMapper mockCompanyMapper;

  @InjectMocks
  private CdhBookingAllowancesService mockCdhBookingAllowancesService;

  @Test
  void updateBookingAllowances_success() {
    var getCompanyResponse = GetCompanyResponse.builder().build();
    var companyAccountRequest = CompanyAccountRequest.builder().build();
    var bookingAllowances = new BookingAllowances();

    when(mockCdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(getCompanyResponse);
    when(mockCompanyMapper.toCompanyAccountRequest(getCompanyResponse)).thenReturn(companyAccountRequest);
    when(mockCompanyMapper.toUpdatedCompanyAccountRequest(companyAccountRequest, bookingAllowances)).thenReturn(
        companyAccountRequest);

    mockCdhBookingAllowancesService.updateBookingAllowances(COMPANY_ID, bookingAllowances, EMAIL);

    verify(mockCdhService).updateCompanyDetails(COMPANY_ID, companyAccountRequest, EMAIL);
  }


  @Test
  void updateBookingAllowancesRightCurrency() {
    var companyAccountRequest = CompanyAccountRequest.builder().build();
    var companyMapper = Mappers.getMapper(CompanyMapper.class);
    var updatedCompanyAccountRequest = companyMapper.toUpdatedCompanyAccountRequest(companyAccountRequest, bookingAllowancesTest());

    Currency irelandCurrency = updatedCompanyAccountRequest.getBookingAllowances().getMaxDinnerBudgets().getIreland().getCurrency();
    assertEquals(Currency.EUR, irelandCurrency);
  }


  private static BookingAllowances bookingAllowancesTest() {
    PriceCapLocations priceCapLocations = new PriceCapLocations();
    Price price = new Price();
    price.setAmount(10);
    price.setCurrency("GBP");
    priceCapLocations.setIreland(price);
    priceCapLocations.setGreaterLondon(price);
    priceCapLocations.setuKWide(price);
    BookingAllowances bookingAllowances = new BookingAllowances();
    bookingAllowances.setMaxDinnerBudgets(priceCapLocations);
    bookingAllowances.setAllowAlcohol(true);
    bookingAllowances.setAllowIndividualCards(true);
    bookingAllowances.setAllowCarParking(true);
    bookingAllowances.setAllowAdditionalCosts(true);
    bookingAllowances.setAllowPremierSaverRates(true);
    bookingAllowances.setAllowIndividualCards(true);
    bookingAllowances.setMaxNumberOfNights(4);
    return bookingAllowances;
  }
}
