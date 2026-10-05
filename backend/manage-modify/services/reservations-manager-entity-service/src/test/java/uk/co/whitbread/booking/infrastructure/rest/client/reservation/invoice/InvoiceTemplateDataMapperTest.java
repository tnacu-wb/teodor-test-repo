package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceBillingContactDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceBookingDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceChargeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceChargesDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceGuestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceHotelDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTotalsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTransactionDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTransactionsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTseDataDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceVatBreakdownDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceVatDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.WbCompanyDto;

class InvoiceTemplateDataMapperTest {

  private final InvoiceTemplateDataMapper mapper = new InvoiceTemplateDataMapper();

  @Test
  void toTemplateData_mapsExpectedFields() {
    InvoiceTemplateData result = mapper.toTemplateData(invoiceContainer(), "PI");

    assertNotNull(result);
    assertEquals("Freiburg City Süd", result.hotel().name());
    assertEquals("user user", result.booking().guestName());
    assertEquals("user", result.booking().leadGuestLastName());
    assertEquals("22.01.2026", result.booking().arrivalDate());
    assertEquals("23.01.2026", result.booking().departureDate());
    assertEquals("04.09.2025", result.invoice().issuedDate());
    assertEquals("GANI159177", result.invoice().invoiceNumber());
    assertEquals("Invoice", result.invoice().folioType());
    assertEquals("138.6", result.invoice().debitAmount());
    assertEquals("138.6", result.invoice().creditAmount());
    assertEquals("0", result.invoice().balanceAmount());
    assertEquals(1, result.transactions().size());
    assertEquals("04.09.2025", result.transactions().get(0).dateTime());
    assertEquals(1, result.charges().size());
    assertEquals("Prepayment (7% VAT) Accommodation", result.charges().get(0).itemDescription());
    assertEquals("132", result.charges().get(0).grossAmount());
    assertEquals(1, result.vatBreakdown().size());
    assertEquals("129.53", result.vatSummary().totalNet());
    assertEquals("9.07", result.vatSummary().totalAmount());
    assertEquals("138.6", result.vatSummary().totalGross());
    assertTrue(result.tse().present());
    assertTrue(result.legalOwnerText().contains("Whitbread Group PLC"));
    assertFalse(result.logoImage().isBlank());
  }

  @Test
  void toTemplateData_fallsBackToBillingNameWhenGuestMissing() {
    InvoiceContainerDto container = invoiceContainer();
    container.getInvoiceDetails().getInvoice().getBooking().setGuest(null);

    InvoiceTemplateData result = mapper.toTemplateData(container, "PI");

    assertEquals("user user", result.booking().guestName());
    assertEquals("user", result.booking().leadGuestLastName());
  }

  @Test
  void formatDate_returnsRawValueWhenInputCannotBeParsed() throws Exception {
    Method method = InvoiceTemplateDataMapper.class.getDeclaredMethod("formatDate", String.class);
    method.setAccessible(true);

    String result = (String) method.invoke(mapper, "not-a-date");

    assertEquals("not-a-date", result);
  }

  @Test
  void toTemplateData_handlesMissingTopLevelSections() {
    InvoiceContainerDto container = new InvoiceContainerDto();
    container.setInvoiceDetails(new InvoiceDetailsDto());

    InvoiceTemplateData result = mapper.toTemplateData(container, "PI");

    assertNotNull(result);
    assertEquals("", result.hotel().name());
    assertEquals("", result.billing().fullName());
    assertEquals("", result.booking().guestName());
    assertEquals("", result.invoice().invoiceNumber());
    assertEquals(0, result.transactions().size());
    assertEquals(0, result.charges().size());
    assertEquals(0, result.vatBreakdown().size());
    assertEquals("", result.legalOwnerText());
    assertFalse(result.tse().present());
  }

  @Test
  void toTemplateData_handlesNullBillingFields() {
    InvoiceContainerDto container = invoiceContainer();
    InvoiceBillingContactDto billing = container.getInvoiceDetails().getInvoice().getBooking().getBillingContact();
    billing.setFirstName(null);
    billing.setLastName(null);
    billing.setAddressLine1(null);
    billing.setAddressLine2(null);
    billing.setAddressLine3(null);
    billing.setAddressLine4(null);
    billing.setPostalCode(null);
    billing.setCity(null);
    billing.setCompanyName(null);
    billing.setGroupName(null);

    InvoiceTemplateData result = mapper.toTemplateData(container, "PI");

    assertEquals("", result.billing().fullName());
    assertEquals("", result.billing().addressLine1());
    assertEquals("", result.billing().cityPostalLine());
    assertEquals("user user", result.booking().guestName());
    assertEquals("user", result.booking().leadGuestLastName());
  }

  @Test
  void toTemplateData_handlesNullCollectionsAndTseData() {
    InvoiceContainerDto container = invoiceContainer();
    InvoiceDto invoice = container.getInvoiceDetails().getInvoice();
    invoice.setCharges(null);
    invoice.setVatBreakdown(null);
    invoice.setTransactions(null);

    InvoiceTemplateData result = mapper.toTemplateData(container, "PI");

    assertEquals(0, result.transactions().size());
    assertEquals(0, result.charges().size());
    assertEquals(0, result.vatBreakdown().size());
    assertEquals(null, result.vatSummary());
    assertFalse(result.tse().present());
  }

  @Test
  void toTemplateData_handlesNullTotalsAndCompany() {
    InvoiceContainerDto container = invoiceContainer();
    container.getInvoiceDetails().getInvoice().setTotals(null);
    container.getInvoiceDetails().getHotel().setWbCompany(null);

    InvoiceTemplateData result = mapper.toTemplateData(container, "PI");

    assertEquals("", result.invoice().debitAmount());
    assertEquals("", result.invoice().creditAmount());
    assertEquals("", result.invoice().balanceAmount());
    assertEquals("", result.legalOwnerText());
  }

  @Test
  void toTemplateData_prefersCreditAmountWhenDebitAmountIsZero() {
    InvoiceContainerDto container = invoiceContainer();
    InvoiceChargeDto charge = container.getInvoiceDetails().getInvoice().getCharges().getCharge().get(0);
    charge.setDebitAmount("0");
    charge.setCreditAmount("-132");

    InvoiceTemplateData result = mapper.toTemplateData(container, "PI");

    assertEquals("-132", result.charges().get(0).grossAmount());
  }

  private InvoiceContainerDto invoiceContainer() {
    InvoiceContainerDto container = new InvoiceContainerDto();
    InvoiceDetailsDto details = new InvoiceDetailsDto();
    details.setHotel(hotel());
    details.setInvoice(invoice());
    container.setInvoiceDetails(details);
    return container;
  }

  private InvoiceHotelDto hotel() {
    InvoiceHotelDto hotel = new InvoiceHotelDto();
    hotel.setHotelId("FRESUD");
    hotel.setName("Freiburg City Süd");
    hotel.setAddressLine1("Heinrich-Von-Stephan-Str.19");
    hotel.setCity("79100 Freiburg");
    hotel.setPostalCode("");
    hotel.setWbCompany(company());
    return hotel;
  }

  private WbCompanyDto company() {
    WbCompanyDto company = new WbCompanyDto();
    company.setLegalOwnerText(List.of("Premier Inn Hotel GmbH ist ein Mitglied der Whitbread Group PLC"));
    return company;
  }

  private InvoiceDto invoice() {
    InvoiceDto invoice = new InvoiceDto();
    invoice.setInvoiceNumber("GANI159177");
    invoice.setIssuedDate("2025-09-04");
    invoice.setCurrency("EUR");
    invoice.setBooking(booking());
    invoice.setCharges(charges());
    invoice.setTotals(totals());
    invoice.setTransactions(transactions());
    invoice.setVatBreakdown(vatBreakdown());
    return invoice;
  }

  private InvoiceBookingDto booking() {
    InvoiceBookingDto booking = new InvoiceBookingDto();
    booking.setBookingReference("GAN9859956");
    booking.setConfirmationNumber("78300446");
    booking.setRoomNumber("");
    booking.setArrivalDate("2026-01-22T00:00:00");
    booking.setDepartureDate("2026-01-23T00:00:00");
    booking.setBillingContact(billingContact());
    booking.setGuest(List.of(leadGuest()));
    return booking;
  }

  private InvoiceBillingContactDto billingContact() {
    InvoiceBillingContactDto billing = new InvoiceBillingContactDto();
    billing.setFirstName("user");
    billing.setLastName("user");
    billing.setCompanyName("");
    billing.setAddressLine1("Whitbread Court, Porz Avenue");
    billing.setAddressLine2("Houghton Hall Park, Houghton Regis");
    billing.setAddressLine4("DUNSTABLE");
    billing.setPostalCode("LU5 5XE");
    billing.setCity("");
    return billing;
  }

  private InvoiceGuestDto leadGuest() {
    InvoiceGuestDto guest = new InvoiceGuestDto();
    guest.setName("user user");
    guest.setIsLeadGuest(true);
    return guest;
  }

  private InvoiceChargesDto charges() {
    InvoiceChargeDto charge = new InvoiceChargeDto();
    charge.setDateTime("2025-09-04T11:02:10+00:00");
    charge.setItemDescription("Prepayment (7% VAT) Accommodation");
    charge.setNetAmount("123.36");
    charge.setVatRate("7");
    charge.setVatAmount("8.64");
    charge.setDebitAmount("132");
    charge.setCreditAmount("0");

    InvoiceChargesDto charges = new InvoiceChargesDto();
    charges.setCharge(List.of(charge));
    return charges;
  }

  private InvoiceTotalsDto totals() {
    InvoiceTotalsDto totals = new InvoiceTotalsDto();
    totals.setBalanceAmount("0");
    totals.setCreditAmount("138.6");
    totals.setDebitAmount("138.6");
    return totals;
  }

  private InvoiceTransactionsDto transactions() {
    InvoiceTransactionDto transaction = new InvoiceTransactionDto();
    transaction.setDateTime("2025-09-04T11:02:10+00:00");
    transaction.setType("Digital Visa");
    transaction.setAmount("138.6");

    InvoiceTseDataDto tseData = new InvoiceTseDataDto();
    tseData.setFn("fn-1");
    tseData.setTn("tn-1");

    InvoiceTransactionsDto transactions = new InvoiceTransactionsDto();
    transactions.setTransaction(List.of(transaction));
    transactions.setTseData(tseData);
    return transactions;
  }

  private InvoiceVatBreakdownDto vatBreakdown() {
    InvoiceVatDto vat = new InvoiceVatDto();
    vat.setRateDesc("Prepayment VAT SPCL");
    vat.setTotalExclVat("129.53");
    vat.setTotalVat("9.07");
    vat.setTotalIncVat("138.6");

    InvoiceVatBreakdownDto breakdown = new InvoiceVatBreakdownDto();
    breakdown.setVat(List.of(vat));
    return breakdown;
  }
}

