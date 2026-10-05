package uk.co.whitbread.piba.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoicesService {

  private final WorldLineService worldLineService;

  public Pair<LocalDate, LocalDate> getDateRangeForInvoice(Optional<LocalDate> invoiceDateOptional,
                                                           String clientIp, Scheme scheme, String tetheredUserGuid) {
    if (invoiceDateOptional.isEmpty()) {
      return Pair.of(null, null);
    }
    AccountInfoResponse accountInfoResponse = worldLineService.getAccountInfo(scheme, clientIp, tetheredUserGuid);

    return getLocalDateLocalDatePair(accountInfoResponse, invoiceDateOptional.get());
  }

  private Pair<LocalDate, LocalDate> getLocalDateLocalDatePair(AccountInfoResponse accountInfoResponse, LocalDate invoiceDate) {
    final LocalDate startDate;
    final LocalDate endDate;
    if (accountInfoResponse == null) {
      return Pair.of(null, null);
    }
    switch(accountInfoResponse.getData().getBillingFrequency()) {
      case "Weekly":
        // Weekly - invoiced every Monday
        endDate = invoiceDate.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));
        startDate = endDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
        break;
      case "Monthly":
        // Monthly - invoiced 2nd of each month
        startDate = invoiceDate.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
        endDate = invoiceDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        break;
      default:
        // Fortnight - invoiced 2nd or 15th
        if (invoiceDate.getDayOfMonth() < 15) {
          // Set range from 15th to the end of the previous month
          startDate = invoiceDate.minusMonths(1).withDayOfMonth(15);
          endDate = invoiceDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        } else {
          // Set range from 1st to 14th of the same month
          startDate = invoiceDate.withDayOfMonth(1);
          endDate = invoiceDate.withDayOfMonth(14);
        }
    }
    return Pair.of(startDate, endDate);
  }

}
