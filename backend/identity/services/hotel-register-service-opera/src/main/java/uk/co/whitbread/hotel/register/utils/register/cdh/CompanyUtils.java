package uk.co.whitbread.hotel.register.utils.register.cdh;

import static uk.co.whitbread.shared.cdh.model.Currency.EUR;
import static uk.co.whitbread.shared.cdh.model.Currency.GBP;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.hotel.register.model.CompanyType;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;
import uk.co.whitbread.shared.cdh.model.Price;
import uk.co.whitbread.shared.cdh.model.company.BookingAlerts;
import uk.co.whitbread.shared.cdh.model.company.BookingAllowances;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.CompanyCellCode;
import uk.co.whitbread.shared.cdh.model.company.CompanyManagementDetails;
import uk.co.whitbread.shared.cdh.model.company.CompanyQuestion;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.RateCaps;

@UtilityClass
public class CompanyUtils {

  private static final CompanyCellCode defaultCompanyCellCode = CompanyCellCode.builder()
      .cellCode("BFLEX")
      .id("1")
      .build();
  private static final RateCaps defaultRateCaps = RateCaps.builder()
      .greaterLondon(Price.builder().amount(BigDecimal.ZERO).currency(GBP).build())
      .ireland(Price.builder().amount(BigDecimal.ZERO).currency(EUR).build())
      .ukWide(Price.builder().amount(BigDecimal.ZERO).currency(GBP).build())
      .build();
  private static final BookingAlerts defaultBookingAlerts = BookingAlerts.builder()
      .rateCaps(defaultRateCaps)
      .bookingAlertHotels(Collections.emptyList())
      .recipientEmailAddresses(Collections.emptyList())
      .build();
  private static final BookingAllowances defaultBookingAllowances = BookingAllowances.builder()
      .allowIndividualCards(true)
      .allowPremierSaverRates(true)
      .maxNumberOfNights(14)
      .maxDinnerBudgets(defaultRateCaps)
      .extrasCodes(List.of("1", "2", "3", "4", "5"))
      .upsellItemsAllowed(List.of("11", "15", "12", "17", "18", "135", "136", "137"))
      .build();
  private static final CompanyManagementDetails defaultCompanyManagementDetails = CompanyManagementDetails.builder()
      .customerReferenceManagement(new CompanyQuestion())
      .purchaseOrderManagement(new CompanyQuestion())
      .build();

  public static void setDefaultCompanyValues(CompanyAccountRequest newCompany) {
    newCompany.setAllowCentralCreditCard(true);
    newCompany.setNumberOfEmployees(1);
    newCompany.setCellCodes(List.of(defaultCompanyCellCode));
    newCompany.setBookingAlerts(defaultBookingAlerts);
    newCompany.setBookingAllowances(defaultBookingAllowances);
    newCompany.setCompanyManagementDetails(defaultCompanyManagementDetails);
    SecureRandom secureRandom = new SecureRandom();
    var bartId = secureRandom.nextInt(0, Integer.MAX_VALUE);
    newCompany.setGlobalCompanyId(bartId);
    newCompany.getMainContact().setGlobalCompanyId(String.valueOf(bartId));
    newCompany.getMainContact().setBartEmployeeId(String.valueOf(bartId));
    newCompany.getMainContact().setBartGuestHistoryNumber(UUID.randomUUID().toString());
    newCompany.setCompanyType(CompanyType.BB.getValue());
  }

  public static GetCompaniesQueryParams buildGetCompaniesQueryParams(Customer newCustomer) {
    return GetCompaniesQueryParams.builder()
        .companyName(newCustomer.getCompanyName())
        .addressLine1(newCustomer.getContactDetail().getAddress().getLine1())
        .addressLine2(newCustomer.getContactDetail().getAddress().getLine2())
        .addressLine3(newCustomer.getContactDetail().getAddress().getLine3())
        .addressLine4(newCustomer.getContactDetail().getAddress().getLine4())
        .addressLine5(newCustomer.getContactDetail().getAddress().getLine5())
        .countryCode(newCustomer.getContactDetail().getAddress().getCountryCode())
        .postCode(newCustomer.getContactDetail().getAddress().getPostCode())
        .build();
  }

  public static GetCompaniesQueryParams buildGetCompaniesQueryParams(String companyName, InnBCompanyAddress address) {
    return GetCompaniesQueryParams.builder()
        .companyName(companyName)
        .addressLine1(address.getLine1())
        .addressLine2(address.getLine2())
        .addressLine3(address.getLine3())
        .addressLine4(address.getLine4())
        .addressLine5(address.getLine5())
        .countryCode(address.getCountryCode())
        .postCode(address.getPostCode())
        .build();
  }
}
