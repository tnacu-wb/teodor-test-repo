package uk.co.whitbread.piba.account.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.piba.account.converter.Converters;
import uk.co.whitbread.piba.account.model.*;
import worldline.mst.bsm.api.b2b.pi.data.*;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CustomerAccountMapper {

    @Mapping(source = "response.pagingResult.fromRecord", target = "pagingResult.fromRecord")
    @Mapping(source = "response.pagingResult.totalRecordCount", target = "pagingResult.totalRecordCount")
    @Mapping(source = "response.pagingResult.toRecord", target = "pagingResult.toRecord")
    @Mapping(source = "response.pagingResult.lastPage", target = "pagingResult.lastPage")
    CustomerAccountTransactionsResponse toCustomerAccountTransaction(CustomerAccountViewTransactionsResponse customerResponse);

    @Mapping(source = "invoiceDate", target = "invoiceDate", qualifiedByName = "toLocalDateConverter")
    @Mapping(source = "transactionDate", target = "transactionDate", qualifiedByName = "toLocalDateTimeConverter")
    @Mapping(source = "netAmount", target = "netAmount", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "taxAmount", target = "taxAmount", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "grossAmount", target = "grossAmount", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "PAN", target = "pan")
    CustomerAccountCardTransaction toCustomerAccountCardTransaction(CustomerAccountCardTransactionType cardTransaction);


    @Mapping(source = "netAmount", target = "netAmount", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "taxAmount", target = "taxAmount", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "grossAmount", target = "grossAmount", qualifiedByName = "toCurrencyConverter")
    CustomerAccountCardTransactionDetail toCustomerAccountCardDetailTransaction(CustomerAccountCardTransactionDetailType cardTransactionDetail);

    @Mapping(source = "outstanding", target = "outstanding", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "newTransactions", target = "newTransactions", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "available", target = "available", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "creditLimit", target = "creditLimit", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "currentBalance", target = "currentBalance", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "interimPayments", target = "interimPayments", qualifiedByName = "toCurrencyConverter")
    CustomerAccountCurrentBalances toCustomerAccountBalances(CustomerAccountBalancesDashboardPattern1Type balancesDashboardPattern);

    @Mapping(source = "tetheredUserGuid", target = "request.tetheredUserGuid", qualifiedByName = "convertFromTetheredGuid")
    @Mapping(source = "schemeCustomerId", target = "request.schemeCustomerId")
    CustomerAccountViewCurrentBalances toCustomerAccountBalancesView(CustomerAccountCurrentBalancesRequest currentBalancesRequest);

    @Mapping(source = "tetheredUserGuid", target = "request.tetheredUserGuid", qualifiedByName = "convertFromTetheredGuid")
    @Mapping(source = "schemeCustomerId", target = "request.schemeCustomerId")
    @Mapping(source = "searchCriteria", target = "request.searchCriteria")
    @Mapping(source = "searchCriteria.dateSearch.pan", target = "request.searchCriteria.dateSearch.PAN")
    @Mapping(source = "searchCriteria.dateSearch.dateFrom", target = "request.searchCriteria.dateSearch.dateFrom", qualifiedByName = "toXmlGregorianCalendarConverter")
    @Mapping(source = "searchCriteria.dateSearch.dateTo", target = "request.searchCriteria.dateSearch.dateTo", qualifiedByName = "toXmlGregorianCalendarConverter")
    @Mapping(source = "pagingRequest", target = "request.pagingRequest")
    CustomerAccountViewTransactions toCustomerAccountViewTransaction(CustomerAccountTransactionsRequest customerTransactionRequest);

    @Mapping(source = "pan", target = "PAN")
    @Mapping(source = "dateFrom", target = "dateFrom", qualifiedByName = "toXmlGregorianCalendarConverter")
    @Mapping(source = "dateTo", target = "dateTo", qualifiedByName = "toXmlGregorianCalendarConverter")
    CustomerAccountViewTransactionsByDateCriteriaType toCustomerAccountViewByDate(CustomerAccountTransactionsByDateCriteria customerTransactionByDate);

    @Mapping(source = "tetheredUserGuid", target = "request.tetheredUserGuid", qualifiedByName = "convertFromTetheredGuid")
    @Mapping(source = "schemeCustomerId", target = "request.schemeCustomerId")
    @Mapping(source = "searchCriteria", target = "request.searchCriteria")
    @Mapping(source = "searchCriteria.dateFrom", target = "request.searchCriteria.dateFrom", qualifiedByName = "toXmlGregorianCalendarConverter")
    @Mapping(source = "searchCriteria.dateTo", target = "request.searchCriteria.dateTo", qualifiedByName = "toXmlGregorianCalendarConverter")
    @Mapping(source = "pagingRequest", target = "request.pagingRequestDetails")
    CustomerAccountInvoiceList toCustomerAccountInvoiceList(CustomerAccountInvoiceRequest customerInvoiceRequest);

    @Mapping(source = "response.pagingResult.fromRecord", target = "pagingResult.fromRecord")
    @Mapping(source = "response.pagingResult.totalRecordCount", target = "pagingResult.totalRecordCount")
    @Mapping(source = "response.pagingResult.toRecord", target = "pagingResult.toRecord")
    @Mapping(source = "response.pagingResult.lastPage", target = "pagingResult.lastPage")
    @Mapping(source = "response", target = "response")
    @Mapping(source = "response.customerAccountInvoiceItem", target = "response.invoices")
    CustomerAccountInvoiceResponse toCustomerAccountInvoiceResponse(CustomerAccountInvoiceListResponse customerInvoiceListResponse);

    @Mapping(source = "statementDate", target = "statementDate")
    @Mapping(source = "invoiceNo", target = "invoiceNo")
    @Mapping(source = "broughtForward", target = "broughtForward", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "paymentsReceived", target = "paymentsReceived", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "overdueBalance", target = "overdueBalance", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "invoiceValue", target = "invoiceValue", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "statementBalance", target = "statementBalance", qualifiedByName = "toCurrencyConverter")
    @Mapping(source = "fileAutoID", target = "fileAutoID")
    CustomerAccountInvoice toCustomerAccountInvoice(CustomerAccountInvoiceItemType accountInvoiceItemType);

    @Mapping(source = "response.fileDownloadResult.fileName", target = "fileName")
    @Mapping(source = "response.fileDownloadResult.binaryData", target = "binaryData")
    @Mapping(source = "response.fileDownloadResult.fileExtension", target = "fileExtension")
    CustomerAccountInvoiceListDownloadResponse toCustomerAccInvoiceListDownloadResponse(CustomerAccountInvoiceDownloadResponse accountInvoiceDownloadResponse);


    @Named("toLocalDateConverter")
    default LocalDate toLocalDateConverter(XMLGregorianCalendar date) {
        return Converters.convertToLocalDate(date);
    }

    @Named("toXmlGregorianCalendarConverter")
    default XMLGregorianCalendar toXmlGregorianCalendarConverter(LocalDate date) {
        return Converters.localDateToXmlGregorianCalendar(date);
    }

    @Named("toLocalDateTimeConverter")
    default LocalDateTime toLocalDateTimeConverter(XMLGregorianCalendar dateTime) {
        return Converters.convertToLocalDateTime(dateTime);
    }

    @Named("toCurrencyConverter")
    default Currency toCurrencyConverter(CurrencyType currencyType) {
        return Converters.convertToCurrency(currencyType);
    }

    @Named("convertFromTetheredGuid")
    default String convertFromTetheredGuid(String tetheredGuid) {
        return Converters.convertFromTetheredGuid(tetheredGuid);
    }
}
