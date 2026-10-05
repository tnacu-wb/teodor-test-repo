package uk.co.whitbread.spending.domain.logic;

import static uk.co.whitbread.spending.domain.utils.AppConstants.CSV_EXTENSION;
import static uk.co.whitbread.spending.domain.utils.AppConstants.CSV_QUOTE;
import static uk.co.whitbread.spending.domain.utils.AppConstants.CSV_SEPARATOR;
import static uk.co.whitbread.spending.domain.utils.AppConstants.EURO_DECIMAL_SEPARATOR;
import static uk.co.whitbread.spending.domain.utils.AppConstants.EURO_GROUPING_SEPARATOR;
import static uk.co.whitbread.spending.domain.utils.AppConstants.EURO_SYMBOL;
import static uk.co.whitbread.spending.domain.utils.AppConstants.POUND_DECIMAL_SEPARATOR;
import static uk.co.whitbread.spending.domain.utils.AppConstants.POUND_GROUPING_SEPARATOR;
import static uk.co.whitbread.spending.domain.utils.AppConstants.POUND_SYMBOL;
import static uk.co.whitbread.spending.domain.utils.AppConstants.UTF8_BOM;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.spending.domain.model.in.MonthName;
import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.domain.model.in.SpendingReportInfo;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.SpendingReportFile;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;
import uk.co.whitbread.spending.domain.ports.primary.ReportingInPort;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportingInPortImpl implements ReportingInPort {

  @Override
  public SpendingReportFile generateAccountSpendingCsv(AccountSpendingResponse accountSpending,
                                                       String language, String pibaAccountId, Scheme scheme) {
    log.info("Generating Account spending report for pibaAccountId {} in {} language", pibaAccountId, language);

    SpendingReportInfo spendingReportInfo = SpendingReportInfo.fromLanguage(language);
    StringBuilder csvBuilder = new StringBuilder(UTF8_BOM);

    appendHeaders(csvBuilder, spendingReportInfo, scheme);
    appendDataRows(csvBuilder, accountSpending, scheme, language);

    String filename = spendingReportInfo.getFilename() + pibaAccountId + CSV_EXTENSION;
    log.info("Report generation completed for language: {}, filename: {}", language, filename);

    return new SpendingReportFile(filename, csvBuilder.toString());
  }

  private void appendHeaders(StringBuilder csvBuilder, SpendingReportInfo reportInfo, Scheme scheme) {
    csvBuilder.append(reportInfo.getYearHeader()).append(CSV_SEPARATOR)
        .append(reportInfo.getMonthHeader()).append(CSV_SEPARATOR)
        .append(reportInfo.getBookingValueHeader()).append(getBookingValueCurrency(scheme))
        .append("\n");
  }

  private String getBookingValueCurrency(Scheme scheme) {
    return " (" + (Scheme.DE.equals(scheme) ? EURO_SYMBOL : POUND_SYMBOL) + ")";
  }

  private void appendDataRows(StringBuilder csvBuilder, AccountSpendingResponse accountSpending, Scheme scheme,
                              String language) {
    accountSpending.getAccountSpendingList()
        .stream()
        .sorted(Comparator.comparingInt(AccountSpending::getYear)
            .thenComparingInt(AccountSpending::getMonth).reversed())
        .forEach(spending -> csvBuilder.append(spending.getYear()).append(CSV_SEPARATOR)
            .append(MonthName.getMonthName(spending.getMonth(), language)).append(CSV_SEPARATOR)
            .append(quoteField(formatBookingValue(spending.getBookingValue(), scheme)))
            .append("\n"));
  }

  private String formatBookingValue(BigDecimal bookingValue, Scheme scheme) {
    DecimalFormatSymbols poundSymbols = new DecimalFormatSymbols();
    poundSymbols.setGroupingSeparator(POUND_GROUPING_SEPARATOR);
    poundSymbols.setDecimalSeparator(POUND_DECIMAL_SEPARATOR);
    DecimalFormat poundFormat = new DecimalFormat("#,##0.##", poundSymbols);

    DecimalFormatSymbols euroSymbols = new DecimalFormatSymbols();
    euroSymbols.setGroupingSeparator(EURO_GROUPING_SEPARATOR);
    euroSymbols.setDecimalSeparator(EURO_DECIMAL_SEPARATOR);
    DecimalFormat euroFormat = new DecimalFormat("#,##0.##", euroSymbols);

    return Scheme.DE.equals(scheme) ? euroFormat.format(bookingValue) : poundFormat.format(bookingValue);
  }

  private String quoteField(String field) {
    var escapedField = field.replace(String.valueOf(CSV_QUOTE),
        CSV_QUOTE + String.valueOf(CSV_QUOTE));

    return CSV_QUOTE + escapedField + CSV_QUOTE;
  }

}
