package uk.co.whitbread.booking.infrastructure.rest.client.reservation.invoice;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceBillingContactDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceBookingDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceChargeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceContainerDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDetailsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceGuestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceHotelDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTransactionDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTransactionsDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceTseDataDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.InvoiceVatDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.WbCompanyDto;

@Component
public class InvoiceTemplateDataMapper {

  private static final DateTimeFormatter INVOICE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final String FOLIO_TYPE = "Invoice";
  private static final String DEFAULT_BRAND = "PI";
  private static final Map<String, String> BRAND_LOGO_FILES = Map.of(
      "PI", "static/images/pi.jpg",
      "HUB", "static/images/hub.jpg",
      "ZIP", "static/images/zip.jpg"
  );

  private final Map<String, String> logoCache;

  public InvoiceTemplateDataMapper() {
    this.logoCache = BRAND_LOGO_FILES.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(
            Map.Entry::getKey,
            entry -> toDataUri(entry.getValue(), "image/jpeg")
        ));
  }

  public InvoiceTemplateData toTemplateData(InvoiceContainerDto container, String hotelBrand) {
    InvoiceSourceData source = extractSourceData(container);
    List<InvoiceTemplateData.TransactionView> transactionViews = toTransactionViews(source.transactions());
    List<InvoiceTemplateData.ChargeView> chargeViews = toChargeViews(source.invoice());
    List<InvoiceTemplateData.VatView> vatViews = toVatViews(source.invoice());

    return InvoiceTemplateData.builder()
        .hotel(toHotelView(source.hotel()))
        .billing(toBillingView(source.billing()))
        .booking(toBookingView(source.booking(), source.billing()))
        .invoice(toInvoiceView(source.invoice()))
        .transactions(transactionViews)
        .charges(chargeViews)
        .vatBreakdown(vatViews)
        .vatSummary(sumVat(vatViews))
        .deposits(Collections.emptyList())
        .tse(toTseView(source.tse()))
        .legalOwnerText(legalOwnerText(source.company()))
        .logoImage(getLogoImageForBrand(hotelBrand))
        .build();
  }

  private InvoiceSourceData extractSourceData(InvoiceContainerDto container) {
    InvoiceDetailsDto details = container.getInvoiceDetails();
    InvoiceHotelDto hotel = details != null ? details.getHotel() : null;
    InvoiceDto invoice = details != null ? details.getInvoice() : null;
    InvoiceBookingDto booking = invoice != null ? invoice.getBooking() : null;
    InvoiceBillingContactDto billing = booking != null ? booking.getBillingContact() : null;
    InvoiceTransactionsDto transactions = invoice != null ? invoice.getTransactions() : null;
    InvoiceTseDataDto tse = transactions != null ? transactions.getTseData() : null;
    WbCompanyDto company = hotel != null ? hotel.getWbCompany() : null;
    return new InvoiceSourceData(hotel, invoice, booking, billing, transactions, tse, company);
  }

  private InvoiceTemplateData.HotelView toHotelView(InvoiceHotelDto hotel) {
    return new InvoiceTemplateData.HotelView(
        value(hotel != null ? hotel.getName() : null),
        value(hotel != null ? hotel.getAddressLine1() : null),
        value(hotel != null ? hotel.getAddressLine2() : null),
        value(hotel != null ? hotel.getAddressLine3() : null),
        value(hotel != null ? hotel.getAddressLine4() : null),
        value(hotel != null ? hotel.getCity() : null),
        value(hotel != null ? hotel.getPostalCode() : null),
        value(hotel != null ? hotel.getHotelId() : null)
    );
  }

  private InvoiceTemplateData.BillingView toBillingView(InvoiceBillingContactDto billing) {
    return new InvoiceTemplateData.BillingView(
        value(billing != null ? billing.getCompanyName() : null),
        fullName(billing),
        value(billing != null ? billing.getAddressLine1() : null),
        value(billing != null ? billing.getAddressLine2() : null),
        value(billing != null ? billing.getAddressLine3() : null),
        value(billing != null ? billing.getAddressLine4() : null),
        cityPostalLine(billing),
        value(billing != null ? billing.getGroupName() : null)
    );
  }

  private InvoiceTemplateData.BookingView toBookingView(InvoiceBookingDto booking,
      InvoiceBillingContactDto billing) {
    return new InvoiceTemplateData.BookingView(
        primaryGuestName(booking != null ? booking.getGuest() : null, billing),
        leadGuestLastName(booking != null ? booking.getGuest() : null, billing),
        value(booking != null ? booking.getRoomNumber() : null),
        formatDate(booking != null ? booking.getArrivalDate() : null),
        formatDate(booking != null ? booking.getDepartureDate() : null),
        value(booking != null ? booking.getConfirmationNumber() : null),
        value(billing != null ? billing.getGroupName() : null),
        value(booking != null ? booking.getBookingReference() : null)
    );
  }

  private InvoiceTemplateData.InvoiceView toInvoiceView(InvoiceDto invoice) {
    String currency = value(invoice != null ? invoice.getCurrency() : null);
    return new InvoiceTemplateData.InvoiceView(
        formatDate(invoice != null ? invoice.getIssuedDate() : null),
        value(invoice != null ? invoice.getInvoiceNumber() : null),
        value(invoice != null ? invoice.getCustomersReference() : null),
        value(invoice != null ? invoice.getCustomersPurchaseOrder() : null),
        value(invoice != null ? invoice.getArNumber() : null),
        FOLIO_TYPE,
        currency,
        toCurrencySymbol(currency),
        value(invoice != null && invoice.getTotals() != null ? invoice.getTotals().getDebitAmount() : null),
        value(invoice != null && invoice.getTotals() != null ? invoice.getTotals().getCreditAmount() : null),
        value(invoice != null && invoice.getTotals() != null ? invoice.getTotals().getBalanceAmount() : null)
    );
  }

  private String toCurrencySymbol(String currency) {
    if ("GBP".equalsIgnoreCase(currency)) {
      return "£";
    } else if ("EUR".equalsIgnoreCase(currency)) {
      return "€";
    }
    return currency;
  }

  private InvoiceTemplateData.TseView toTseView(InvoiceTseDataDto tse) {
    return new InvoiceTemplateData.TseView(
        hasTseData(tse),
        value(tse != null ? tse.getFn() : null),
        value(tse != null ? tse.getStartDateTime() : null),
        value(tse != null ? tse.getEndDateTime() : null),
        value(tse != null ? tse.getSerial() : null),
        value(tse != null ? tse.getSign() : null),
        value(tse != null ? tse.getSignCnt() : null),
        value(tse != null ? tse.getCode() : null),
        value(tse != null ? tse.getSq() : null),
        value(tse != null ? tse.getTn() : null)
    );
  }

  private List<InvoiceTemplateData.TransactionView> toTransactionViews(InvoiceTransactionsDto transactions) {
    return transactions != null && transactions.getTransaction() != null
        ? transactions.getTransaction().stream().filter(Objects::nonNull).map(this::toTransactionView).toList()
        : Collections.emptyList();
  }

  private List<InvoiceTemplateData.ChargeView> toChargeViews(InvoiceDto invoice) {
    return invoice != null && invoice.getCharges() != null && invoice.getCharges().getCharge() != null
        ? invoice.getCharges().getCharge().stream().filter(Objects::nonNull).map(this::toChargeView).toList()
        : Collections.emptyList();
  }

  private List<InvoiceTemplateData.VatView> toVatViews(InvoiceDto invoice) {
    return invoice != null && invoice.getVatBreakdown() != null && invoice.getVatBreakdown().getVat() != null
        ? invoice.getVatBreakdown().getVat().stream().filter(Objects::nonNull).map(this::toVatView).toList()
        : Collections.emptyList();
  }

  private record InvoiceSourceData(
      InvoiceHotelDto hotel,
      InvoiceDto invoice,
      InvoiceBookingDto booking,
      InvoiceBillingContactDto billing,
      InvoiceTransactionsDto transactions,
      InvoiceTseDataDto tse,
      WbCompanyDto company
  ) {}

  private InvoiceTemplateData.TransactionView toTransactionView(InvoiceTransactionDto transaction) {
    return new InvoiceTemplateData.TransactionView(
        formatDate(transaction.getDateTime()),
        value(transaction.getType()),
        value(transaction.getAmount())
    );
  }

  private InvoiceTemplateData.ChargeView toChargeView(InvoiceChargeDto charge) {
    return new InvoiceTemplateData.ChargeView(
        formatDate(charge.getDateTime()),
        value(charge.getItemDescription()),
        value(charge.getNetAmount()),
        value(charge.getVatRate()),
        value(charge.getVatAmount()),
        firstNonBlank(charge.getDebitAmount(), charge.getCreditAmount())
    );
  }

  private InvoiceTemplateData.VatView toVatView(InvoiceVatDto vat) {
    return new InvoiceTemplateData.VatView(
        value(vat.getRateDesc()),
        value(vat.getTotalExclVat()),
        value(vat.getTotalVat()),
        value(vat.getTotalIncVat())
    );
  }

  private InvoiceTemplateData.VatSummaryView sumVat(List<InvoiceTemplateData.VatView> vatViews) {
    if (vatViews.isEmpty()) {
      return null;
    }
    InvoiceTemplateData.VatView total = vatViews.get(vatViews.size() - 1);
    return new InvoiceTemplateData.VatSummaryView(
        total.totalExclVat(),
        total.totalVat(),
        total.totalIncVat()
    );
  }

  private String legalOwnerText(WbCompanyDto company) {
    if (company == null || company.getLegalOwnerText() == null) {
      return "";
    }
    return company.getLegalOwnerText().stream()
        .filter(Objects::nonNull)
        .reduce("",
            (left, right) -> left.isBlank() ? right : left + System.lineSeparator() + right);
  }

  private boolean hasTseData(InvoiceTseDataDto tse) {
    return tse != null && Stream.of(tse.getFn(), tse.getSerial(), tse.getSign(), tse.getTn())
        .anyMatch(value -> value != null && !value.isBlank());
  }

  private String primaryGuestName(List<InvoiceGuestDto> guests, InvoiceBillingContactDto billing) {
    if (guests != null && !guests.isEmpty() && guests.get(0) != null
        && guests.get(0).getName() != null) {
      return guests.get(0).getName();
    }
    return fullName(billing);
  }

  private String leadGuestLastName(List<InvoiceGuestDto> guests, InvoiceBillingContactDto billing) {
    if (guests != null) {
      Optional<String> leadGuestLastName = guests.stream()
          .filter(Objects::nonNull)
          .filter(guest -> Boolean.TRUE.equals(guest.getIsLeadGuest()))
          .map(InvoiceGuestDto::getName)
          .map(this::extractLastName)
          .filter(value -> !value.isBlank())
          .findFirst();
      if (leadGuestLastName.isPresent()) {
        return leadGuestLastName.get();
      }
    }
    return extractLastName(fullName(billing));
  }

  private String extractLastName(String fullName) {
    if (fullName == null || fullName.isBlank()) {
      return "";
    }
    String[] nameParts = fullName.trim().split("\\s+");
    return nameParts.length == 0 ? "" : nameParts[nameParts.length - 1];
  }

  private String cityPostalLine(InvoiceBillingContactDto billing) {
    if (billing == null) {
      return "";
    }
    String city = value(billing.getCity());
    String postal = value(billing.getPostalCode());
    return firstNonBlank((city + " " + postal).trim(), postal, city);
  }

  private String fullName(InvoiceBillingContactDto billing) {
    if (billing == null) {
      return "";
    }
    return (value(billing.getFirstName()) + " " + value(billing.getLastName())).trim();
  }

  private String formatDate(String raw) {
    if (raw == null || raw.isBlank()) {
      return "";
    }
    try {
      return raw.contains("T") ? formatDateTime(raw) : formatDateOnly(raw);
    } catch (Exception ignored) {
      return raw;
    }
  }

  private String formatDateTime(String raw) {
    try {
      return OffsetDateTime.parse(raw).toLocalDate().format(INVOICE_DATE_FORMATTER);
    } catch (Exception ignored) {
      return LocalDateTime.parse(raw).toLocalDate().format(INVOICE_DATE_FORMATTER);
    }
  }

  private String formatDateOnly(String raw) {
    return LocalDate.parse(raw).format(INVOICE_DATE_FORMATTER);
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank() && !"0".equals(value) && !"0.00".equals(value)) {
        return value;
      }
    }
    return "";
  }

  private String value(String text) {
    return text == null ? "" : text;
  }

  private String getLogoImageForBrand(String hotelBrand) {
    String key = (hotelBrand == null || hotelBrand.isBlank())
        ? DEFAULT_BRAND
        : hotelBrand.toUpperCase();
    return logoCache.getOrDefault(key, logoCache.get(DEFAULT_BRAND));
  }

  private String toDataUri(String classpathLocation, String mimeType) {
    try (InputStream is = new ClassPathResource(classpathLocation).getInputStream()) {
      byte[] bytes = is.readAllBytes();
      return String.format("data:%s;base64,%s", mimeType,
          Base64.getEncoder().encodeToString(bytes));
    } catch (Exception ex) {
      return "";
    }
  }
}
