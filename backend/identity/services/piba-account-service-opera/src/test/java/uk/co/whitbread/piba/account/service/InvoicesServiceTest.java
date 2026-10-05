package uk.co.whitbread.piba.account.service;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.model.AccountInfo;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicesServiceTest {

  @InjectMocks
  private InvoicesService invoicesService;

  @Mock
  private WorldLineService mockWorldLineService;

  @Test
  void getDateRangeForInvoice_ShouldReturnNullPair_WhenInvoiceDateIsEmpty() {
    Pair<LocalDate, LocalDate> result = invoicesService.getDateRangeForInvoice(Optional.empty(), "127.0.0.1", Scheme.GB, "test-guid");
    assertEquals(Pair.of(null, null), result);
  }

  @Test
  void getDateRangeForInvoice_ShouldReturnNullPair_WhenAccountInfoResponseIsNull() {
    when(mockWorldLineService.getAccountInfo(Scheme.GB, "127.0.0.1", "test-guid")).thenReturn(null);

    Pair<LocalDate, LocalDate> result = invoicesService.getDateRangeForInvoice(Optional.of(LocalDate.of(2023, 10, 15)), "127.0.0.1", Scheme.GB, "test-guid");
    assertEquals(Pair.of(null, null), result);
  }

  @Test
  void getDateRangeForInvoice_ShouldReturnWeeklyRange() {
    AccountInfoResponse mockResponse = new AccountInfoResponse();
    mockResponse.setData(new AccountInfo("Weekly", 7, "Current", null, null));
    when(mockWorldLineService.getAccountInfo(Scheme.GB, "127.0.0.1", "test-guid")).thenReturn(mockResponse);

    Pair<LocalDate, LocalDate> result = invoicesService.getDateRangeForInvoice(Optional.of(LocalDate.of(2025, 8, 18)), "127.0.0.1", Scheme.GB, "test-guid");

    assertEquals(LocalDate.of(2025, 8, 10), result.getLeft());
    assertEquals(LocalDate.of(2025, 8, 16), result.getRight());
  }

  @Test
  void getDateRangeForInvoice_ShouldReturnMonthlyRange() {
    AccountInfoResponse mockResponse = new AccountInfoResponse();
    mockResponse.setData(new AccountInfo("Monthly", 30, "Current", null, null));
    when(mockWorldLineService.getAccountInfo(Scheme.GB, "127.0.0.1", "test-guid")).thenReturn(mockResponse);

    Pair<LocalDate, LocalDate> result = invoicesService.getDateRangeForInvoice(Optional.of(LocalDate.of(2025, 8, 2)), "127.0.0.1", Scheme.GB, "test-guid");

    assertEquals(LocalDate.of(2025, 7, 1), result.getLeft());
    assertEquals(LocalDate.of(2025, 7, 31), result.getRight());
  }

  @Test
  void getDateRangeForInvoice_ShouldReturnFortnightRange_InvoicesOnThe2nd() {
    AccountInfoResponse mockResponse = new AccountInfoResponse();
    mockResponse.setData(new AccountInfo("Fortnight", 14, "Current", null, null));
    when(mockWorldLineService.getAccountInfo(Scheme.GB, "127.0.0.1", "test-guid")).thenReturn(mockResponse);

    Pair<LocalDate, LocalDate> result = invoicesService.getDateRangeForInvoice(Optional.of(LocalDate.of(2025, 8, 2)), "127.0.0.1", Scheme.GB, "test-guid");

    assertEquals(LocalDate.of(2025, 7, 15), result.getLeft());
    assertEquals(LocalDate.of(2025, 7, 31), result.getRight());
  }

  @Test
  void getDateRangeForInvoice_ShouldReturnFortnightRange_InvoiceOnThe15th() {
    AccountInfoResponse mockResponse = new AccountInfoResponse();
    mockResponse.setData(new AccountInfo("Fortnight", 14, "Current", null, null));
    when(mockWorldLineService.getAccountInfo(Scheme.GB, "127.0.0.1", "test-guid")).thenReturn(mockResponse);

    Pair<LocalDate, LocalDate> result = invoicesService.getDateRangeForInvoice(Optional.of(LocalDate.of(2025, 8, 15)), "127.0.0.1", Scheme.GB, "test-guid");

    assertEquals(LocalDate.of(2025, 8, 1), result.getLeft());
    assertEquals(LocalDate.of(2025, 8, 14), result.getRight());
  }
}
