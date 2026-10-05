package uk.co.whitbread.piba.account.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.co.whitbread.piba.account.util.TestUtil.HASH;
import static uk.co.whitbread.piba.account.util.TestUtil.INVOICES_BINARY_DATA;
import static uk.co.whitbread.piba.account.util.TestUtil.INVOICE_FILE_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.MEMORABLE_WORD;
import static uk.co.whitbread.piba.account.util.TestUtil.SAMPLE_NONCE;
import static uk.co.whitbread.piba.account.util.TestUtil.SCHEME_CUSTOMER_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.SESSION_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.TETHERED_USER_GUID;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.model.Currency;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoice;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.PagingResult;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.util.TestUtil;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimit;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownload;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountOverviewType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalances;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactions;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserOverviewType;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWord;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = "worldline.tetheringPlus.enabled=true")
@MockitoSettings(strictness = Strictness.LENIENT)
class WorldlineAccountTransformerTest {
    
    @Autowired
    private WorldlineTransformer worldlineTransformer;
    @MockitoBean
    private CacheManager cacheManager;
    private final TestUtil testUtil = new TestUtil();

    @Test
    void transformViewTransactionsSoapRequestSuccessfully() {

        CustomerAccountTransactionsRequest customerAccountTransactionsRequest = testUtil.populateTransactionRequest(true, true);

        CustomerAccountViewTransactions customerAccountViewTransactions = worldlineTransformer.toCustomerAccountTransactionsRequest(customerAccountTransactionsRequest);
        assertNotNull(customerAccountViewTransactions);
        assertEquals("{"+TETHERED_USER_GUID+"}", customerAccountViewTransactions.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, customerAccountViewTransactions.getRequest().getSchemeCustomerId());
        assertEquals(10, customerAccountViewTransactions.getRequest().getPagingRequest().getMaximumDisplayRows());
        XMLGregorianCalendar dateFrom = customerAccountViewTransactions.getRequest().getSearchCriteria().getDateSearch().getDateFrom();
        LocalDate localDate = LocalDate.of(2020, 12, 1);
        assertThat(localDate.getMonthValue()).isEqualTo(dateFrom.getMonth());
        assertThat(localDate.getDayOfMonth()).isEqualTo(dateFrom.getDay());
        assertEquals(5, customerAccountViewTransactions.getRequest().getSearchCriteria().getInvoiceNumberSearch().getInvoiceNumber());
    }


    @Test
    void transformViewTransactionsSoapRequestSuccessfullyWithDateSearch() {

        CustomerAccountTransactionsRequest customerAccountTransactionsRequest = testUtil.populateTransactionRequest(false, true);

        CustomerAccountViewTransactions customerAccountViewTransactions = worldlineTransformer.toCustomerAccountTransactionsRequest(customerAccountTransactionsRequest);
        assertNotNull(customerAccountViewTransactions);
        assertEquals("{"+TETHERED_USER_GUID+"}", customerAccountViewTransactions.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, customerAccountViewTransactions.getRequest().getSchemeCustomerId());
        assertEquals(10, customerAccountViewTransactions.getRequest().getPagingRequest().getMaximumDisplayRows());
        XMLGregorianCalendar dateFrom = customerAccountViewTransactions.getRequest().getSearchCriteria().getDateSearch().getDateFrom();
        LocalDate localDate = LocalDate.of(2020, 12, 1);
        assertThat(localDate.getYear()).isEqualTo(dateFrom.getYear());
        assertThat(localDate.getMonthValue()).isEqualTo(dateFrom.getMonth());
        assertThat(localDate.getDayOfMonth()).isEqualTo(dateFrom.getDay());
        assertNull(customerAccountViewTransactions.getRequest().getSearchCriteria().getInvoiceNumberSearch());
    }
    @Test
    void transformViewTransactionsSoapRequestSuccessfullyWithInvoiceSearch() {

        CustomerAccountTransactionsRequest customerAccountTransactionsRequest = testUtil.populateTransactionRequest(true, false);

        CustomerAccountViewTransactions customerAccountViewTransactions = worldlineTransformer.toCustomerAccountTransactionsRequest(customerAccountTransactionsRequest);
        assertNotNull(customerAccountViewTransactions);
        assertEquals("{"+TETHERED_USER_GUID+"}", customerAccountViewTransactions.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, customerAccountViewTransactions.getRequest().getSchemeCustomerId());
        assertEquals(10, customerAccountViewTransactions.getRequest().getPagingRequest().getMaximumDisplayRows());
        assertNull(customerAccountViewTransactions.getRequest().getSearchCriteria().getDateSearch());
        assertEquals(5, customerAccountViewTransactions.getRequest().getSearchCriteria().getInvoiceNumberSearch().getInvoiceNumber());
    }

    @Test
    void transformViewTransactionsResponseSuccessfully() {

        CustomerAccountViewTransactionsResponse viewTransactionsResponse = testUtil.populateTransactionsResponse();
        CustomerAccountTransactionsResponse response = worldlineTransformer.toCustomerAccountTransactionsResponse(viewTransactionsResponse);
        assertNotNull(response);
        assertEquals(10, response.getResponse().getTransactions().size());
        assertEquals(594, response.getPagingResult().getTotalRecordCount());
        assertEquals("30895001*******0018", response.getResponse().getTransactions().get(0).getPan());
        assertEquals("Jersey St Helier", response.getResponse().getTransactions().get(1).getLocation());
        assertEquals("Accommodation", response.getResponse().getTransactions().get(0).getLineItems().get(0).getDescription());
        LocalDateTime transactionDate = LocalDateTime.of(2020, 2, 13, 0, 30);
        assertEquals(transactionDate, response.getResponse().getTransactions().get(0).getTransactionDate());
        Currency netAmount = response.getResponse().getTransactions().get(0).getNetAmount();
        assertEquals(BigDecimal.valueOf(719.89), netAmount.getAmount());
        assertEquals("GBP", netAmount.getCurrencyCode());
        Currency netAmount2 = response.getResponse().getTransactions().get(1).getNetAmount();
        assertEquals(BigDecimal.valueOf(721.89), netAmount2.getAmount());
        assertEquals("GBP", netAmount2.getCurrencyCode());
        Currency lineItemsGrossAmount = response.getResponse().getTransactions().get(0).getLineItems().get(0).getGrossAmount();
        assertEquals(BigDecimal.valueOf(755.88), lineItemsGrossAmount.getAmount());
        assertEquals("GBP", lineItemsGrossAmount.getCurrencyCode());
        Currency lineItemsGrossAmount1 = response.getResponse().getTransactions().get(0).getLineItems().get(1).getGrossAmount();
        assertEquals(BigDecimal.valueOf(756.88),lineItemsGrossAmount1.getAmount());
        assertEquals("GBP",lineItemsGrossAmount1.getCurrencyCode());
    }

    @Test
    void transformViewCurrentBalanceResponseSuccessfully()  {
        Currency currencyValue=new Currency();
        currencyValue.setAmount(BigDecimal.valueOf(100));
        currencyValue.setCurrencyCode("GBP");
        currencyValue.setCurrencySymbol("£");

        CustomerAccountViewCurrentBalancesResponse customerAccountViewCurrentBalancesResponse=testUtil.populateViewCurrentBalancesResponse();
        CustomerAccountCurrentBalances response = worldlineTransformer.toCustomerAccountCurrentBalancesResponse(customerAccountViewCurrentBalancesResponse);
        assertNotNull(response);
        assertEquals(SCHEME_CUSTOMER_ID,response.getSchemeCustomerId());
        assertEquals(currencyValue,response.getAvailable());

    }

    @Test
    void transformViewCurrentBalanceSoapRequestSuccessfully() {

        CustomerAccountCurrentBalancesRequest request= new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID,SCHEME_CUSTOMER_ID, Scheme.GB);
        CustomerAccountViewCurrentBalances balancesRequest = worldlineTransformer.toCustomerAccountCurrentBalancesRequest(request);
        assertNotNull(balancesRequest);
        assertEquals("{"+TETHERED_USER_GUID+"}", balancesRequest.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, balancesRequest.getRequest().getSchemeCustomerId());
    }

    @Test
    void transformTetheredUserDetailsGetSuccessfully() {
        TetheredUserDetailsGet tetheredUserDetailsGet = worldlineTransformer.toTetheredUserDetailsRequest(TETHERED_USER_GUID, Scheme.GB);
        assertNotNull(tetheredUserDetailsGet);
        assertEquals("{"+ TETHERED_USER_GUID +"}", tetheredUserDetailsGet.getRequest().getTetheredUserGuid());
    }

    @Test
    void transformTetheredUserDetailsGetResponseSuccessfully() {
        TetheredUserDetailsGetResponse tetheredUserDetailsGetResponse = new TetheredUserDetailsGetResponse();
        TetheredUserDetailsGetResponseType respType=new TetheredUserDetailsGetResponseType();
        tetheredUserDetailsGetResponse.setResponse(respType);
        TetheredUserOverviewType tetheredUserOverview = new TetheredUserOverviewType();
        tetheredUserOverview.setTetheredUserGuid(TETHERED_USER_GUID);
        tetheredUserOverview.setAPIUserGuid(TETHERED_USER_GUID);
        tetheredUserOverview.setUserRole("AccountHolder");
        tetheredUserOverview.setCountMyCards(1);
        CustomerAccountOverviewType customerAccountOverview = new CustomerAccountOverviewType();
        customerAccountOverview.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        customerAccountOverview.setPrimarySchemeCustomerId(SCHEME_CUSTOMER_ID);
        customerAccountOverview.setAccountName("Primary Account");
        customerAccountOverview.setAccountNumber("785324");
        TetheredUserDetailsType tetheredUserDetails = new TetheredUserDetailsType();
        tetheredUserDetails.setTetheredUserOverview(tetheredUserOverview);
        tetheredUserDetails.setCustomerAccountOverview(customerAccountOverview);
        respType.setTetheredUserDetails(tetheredUserDetails);

        TetheredUserDetailsResponse response = worldlineTransformer.toTetheredUserDetailsResponse(tetheredUserDetailsGetResponse);
        assertNotNull(response);
        assertEquals(TETHERED_USER_GUID, response.getTetheredUserOverview().getTetheredUserGuid());
        assertEquals(TETHERED_USER_GUID, response.getTetheredUserOverview().getApiUserGuid());
        assertEquals("AccountHolder", response.getTetheredUserOverview().getUserRole());
        assertEquals(1, response.getTetheredUserOverview().getMyCards());
        assertEquals(SCHEME_CUSTOMER_ID, response.getCustomerAccountOverview().getSchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, response.getCustomerAccountOverview().getPrimarySchemeCustomerId());
        assertEquals("Primary Account", response.getCustomerAccountOverview().getAccountName());
        assertEquals("785324", response.getCustomerAccountOverview().getAccountNumber());
    }

    @Test
    void transformViewTransactionsSoapRequestSuccessfullyWithNullSearchCriteria_ShouldSetDefaultDateSearchCriteria() {

        CustomerAccountTransactionsRequest request = testUtil.populateTransactionRequest(false, false);
        request.setSearchCriteria(null);

        CustomerAccountViewTransactions customerAccountViewTransactions = worldlineTransformer.toCustomerAccountTransactionsRequest(request);

        assertNotNull(customerAccountViewTransactions);
        assertEquals("{"+TETHERED_USER_GUID+"}", customerAccountViewTransactions.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, customerAccountViewTransactions.getRequest().getSchemeCustomerId());
        assertEquals(10, customerAccountViewTransactions.getRequest().getPagingRequest().getMaximumDisplayRows());
        XMLGregorianCalendar dateFrom = customerAccountViewTransactions.getRequest().getSearchCriteria().getDateSearch().getDateFrom();
        assertThat(LocalDate.now().minusMonths(2).getYear()).isEqualTo(dateFrom.getYear());
        assertThat(LocalDate.now().minusMonths(2).getMonthValue()).isEqualTo(dateFrom.getMonth());
        assertThat(LocalDate.now().minusMonths(2).getDayOfMonth()).isEqualTo(dateFrom.getDay());
        XMLGregorianCalendar dateTo = customerAccountViewTransactions.getRequest().getSearchCriteria().getDateSearch().getDateTo();
        assertThat(LocalDate.now().getYear()).isEqualTo(dateTo.getYear());
        assertThat(LocalDate.now().getMonthValue()).isEqualTo(dateTo.getMonth());
        assertThat(LocalDate.now().getDayOfMonth()).isEqualTo(dateTo.getDay());
        assertThat(customerAccountViewTransactions.getRequest().getSearchCriteria().getDateSearch().getTransactionTypes()).isEqualTo("Uninvoiced");
        assertNull(customerAccountViewTransactions.getRequest().getSearchCriteria().getInvoiceNumberSearch());
    }

    @Test
    void transformGetInvoicesRequestSuccessfully() {
        CustomerAccountInvoiceRequest invoiceRequest = testUtil.createCustomerAccountInvoiceRequest(TETHERED_USER_GUID, SCHEME_CUSTOMER_ID);
        CustomerAccountInvoiceList request = worldlineTransformer.toCustomerAccountInvoiceRequest(invoiceRequest);
        assertNotNull(request);
        assertEquals("{"+TETHERED_USER_GUID+"}", request.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, request.getRequest().getSchemeCustomerId());
    }

    @Test
    void transformGetInvoicesResponseSuccessfully() {
        CustomerAccountInvoiceListResponse invoiceListResponse = testUtil.createCustomerAccountInvoiceResponse();
        CustomerAccountInvoiceResponse response = worldlineTransformer.toCustomerAccountInvoiceResponse(invoiceListResponse);
        assertNotNull(response);
        assertEquals(3, response.getResponse().getInvoices().size());
        CustomerAccountInvoice invoice = response.getResponse().getInvoices().get(0);
        assertEquals("12345", invoice.getInvoiceNo());
        assertEquals(Integer.valueOf( 54321),invoice.getFileAutoID());
        assertEquals(BigDecimal.valueOf(234.78), invoice.getInvoiceValue().getAmount());
        assertEquals(BigDecimal.valueOf(134.78), invoice.getBroughtForward().getAmount());
        assertEquals(BigDecimal.valueOf(34.80), invoice.getOverdueBalance().getAmount());
        assertEquals(BigDecimal.valueOf(55.50), invoice.getStatementBalance().getAmount());
        assertEquals(BigDecimal.valueOf(99.99), invoice.getPaymentsReceived().getAmount());
        PagingResult pagingResult = response.getPagingResult();
        assertEquals(1, pagingResult.getFromRecord());
        assertEquals(3, pagingResult.getToRecord());
        assertEquals(100, pagingResult.getTotalRecordCount());
        assertEquals(34, pagingResult.getLastPage());
    }

    @Test
    void transformGetInvoicesDownloadRequestSuccessfully() {
        CustomerAccountInvoiceDownload request = worldlineTransformer.toCustomerAccountInvoiceDownloadRequest(SCHEME_CUSTOMER_ID, TETHERED_USER_GUID, INVOICE_FILE_ID, Scheme.GB);
        assertNotNull(request);
        assertEquals("{"+TETHERED_USER_GUID+"}", request.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, request.getRequest().getSchemeCustomerId());
        assertEquals(INVOICE_FILE_ID, request.getRequest().getFileID());
    }

    @Test
    void transformGetInvoicesDownloadResponseSuccessfully() {
        CustomerAccountInvoiceDownloadResponse customerAccountInvoiceDownloadResponse = testUtil.createInvoiceListDownloadResponse();
        CustomerAccountInvoiceListDownloadResponse response = worldlineTransformer.toCustomerAccountInvoiceDownloadResponse(customerAccountInvoiceDownloadResponse);
        assertNotNull(response);
        assertEquals("filename.pdf", response.getFileName());
        assertEquals(".pdf", response.getFileExtension());
        assertEquals(INVOICES_BINARY_DATA.length, response.getBinaryData().length);
    }

    @Test
    void transformPostProposeCreditLimitRequestSuccessfully() {
        CreditProposeLimitRequest creditProposeLimitRequest = new CreditProposeLimitRequest(testUtil.TETHERED_USER_GUID, testUtil.SCHEME_CUSTOMER_ID, 200);
        CreditProposeNewLimit request = worldlineTransformer.toCreditProposeNewLimitRequest(creditProposeLimitRequest);
        assertNotNull(request);
        assertEquals("{"+TETHERED_USER_GUID+"}", request.getRequest().getTetheredUserGuid());
        assertEquals(SCHEME_CUSTOMER_ID, request.getRequest().getSchemeCustomerId());
        assertEquals(200, request.getRequest().getProposedCreditLimit());
    }

    @Test
    void transformPostProposeCreditLimitResponseSuccessfully() {
        CreditProposeLimitResponse response = worldlineTransformer.toCreditProposeNewLimitResponse(testUtil.populateCreditProposeNewLimitResponse());
        assertNotNull(response);
        assertEquals("888991ff-d0ee-435d-ac78-57e91556deca", response.getRequestId());
    }
    
    
    @Test
    void transformResetMemorableWordRequestDESuccessfully() {
        String timestamp = new SimpleDateFormat(testUtil.DATE_FORMAT).format(new Date());
        UpdateMemorableWordRequest
                updateMemorableWordRequest =
                new UpdateMemorableWordRequest(testUtil.SESSION_ID, SAMPLE_NONCE, timestamp, testUtil.HASH,
                        testUtil.MEMORABLE_WORD,testUtil.TETHERED_USER_GUID, Scheme.DE);
        UpdateMemorableWord request = worldlineTransformer.toUpdateMemorableWordRequest(updateMemorableWordRequest);
        assertNotNull(request);
        assertNotNull(request.getRequest());
        assertNotNull(request.getRequest().getSessionToken());
        assertEquals(SESSION_ID, request.getRequest().getSessionToken().getSessionId());
        assertEquals(SAMPLE_NONCE, request.getRequest().getSessionToken().getNonce());
        assertEquals(timestamp, request.getRequest().getSessionToken().getTimestamp());
        assertEquals(HASH, request.getRequest().getSessionToken().getHash());
        assertEquals(MEMORABLE_WORD, request.getRequest().getNewMemorableWord());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getDeHeader().getCultureCode(), request.getRequest().getHeader().getCultureCode());
    }

    @Test
    void transformResetMemorableWordRequestGBSuccessfully() {
        String timestamp = new SimpleDateFormat(testUtil.DATE_FORMAT).format(new Date());
        UpdateMemorableWordRequest
                updateMemorableWordRequest =
                new UpdateMemorableWordRequest(testUtil.SESSION_ID, SAMPLE_NONCE, timestamp, testUtil.HASH,
                        testUtil.MEMORABLE_WORD,testUtil.TETHERED_USER_GUID, Scheme.GB);
        UpdateMemorableWord request = worldlineTransformer.toUpdateMemorableWordRequest(updateMemorableWordRequest);
        assertNotNull(request);
        assertNotNull(request.getRequest());
        assertNotNull(request.getRequest().getSessionToken());
        assertEquals(SESSION_ID, request.getRequest().getSessionToken().getSessionId());
        assertEquals(SAMPLE_NONCE, request.getRequest().getSessionToken().getNonce());
        assertEquals(timestamp, request.getRequest().getSessionToken().getTimestamp());
        assertEquals(HASH, request.getRequest().getSessionToken().getHash());
        assertEquals(MEMORABLE_WORD, request.getRequest().getNewMemorableWord());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getGbHeader().getCultureCode(), request.getRequest().getHeader().getCultureCode());
    }

    @Test
    void transformLoginTetheredUserRequestGBSuccessfully() {
        LoginTetheredUser request = worldlineTransformer.toLoginTetheredUserRequest(TETHERED_USER_GUID, Scheme.GB);
        assertNotNull(request);
        assertNotNull(request.getRequest());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getGbHeader().getCultureCode(), request.getRequest().getHeader().getCultureCode());
        assertEquals("{" +TETHERED_USER_GUID +"}", request.getRequest().getTetheredUserGuid());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getGbCredentials().getUsername(), request.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getGbCredentials().getPassword(), request.getRequest().getTrustedPartnerCredentials().getPassword());
    }

    @Test
    void transformLoginTetheredUserRequestDESuccessfully() {
        LoginTetheredUser request = worldlineTransformer.toLoginTetheredUserRequest(TETHERED_USER_GUID, Scheme.DE);
        assertNotNull(request);
        assertNotNull(request.getRequest());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getDeHeader().getCultureCode(), request.getRequest().getHeader().getCultureCode());
        assertEquals("{" +TETHERED_USER_GUID +"}", request.getRequest().getTetheredUserGuid());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getDeCredentials().getUsername(), request.getRequest().getTrustedPartnerCredentials().getUsername());
        assertEquals(((WorldlineAccountTransformer) worldlineTransformer).getDeCredentials().getPassword(), request.getRequest().getTrustedPartnerCredentials().getPassword());
    }

    @Test
    void whenTransformViewCurrentBalanceSoapRequest_consecutiveClientMessageIdIsUnique() {

        CustomerAccountCurrentBalancesRequest requestGb =
                new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID,SCHEME_CUSTOMER_ID, Scheme.GB);
        CustomerAccountCurrentBalancesRequest requestDe =
                new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID,SCHEME_CUSTOMER_ID, Scheme.DE);

        CustomerAccountViewCurrentBalances balancesGb = worldlineTransformer
                .toCustomerAccountCurrentBalancesRequest(requestGb);
        CustomerAccountViewCurrentBalances balancesDe = worldlineTransformer
                .toCustomerAccountCurrentBalancesRequest(requestDe);

        assertNotNull(balancesGb);
        assertNotNull(balancesDe);
        assertNotEquals(balancesGb.getRequest().getHeader().getClientMessageId(),
                balancesDe.getRequest().getHeader().getClientMessageId());
    }

    @Test
    void whenTransformToTetheredUserDetailsRequest_consecutiveClientMessageIdIsUnique() {
        TetheredUserDetailsGet tetheredUserDetailsGb = worldlineTransformer
                .toTetheredUserDetailsRequest(TETHERED_USER_GUID, Scheme.GB);
        TetheredUserDetailsGet tetheredUserDetailsDe = worldlineTransformer
                .toTetheredUserDetailsRequest(TETHERED_USER_GUID, Scheme.DE);

        assertNotNull(tetheredUserDetailsGb);
        assertNotNull(tetheredUserDetailsDe);
        assertNotEquals(tetheredUserDetailsGb.getRequest().getHeader().getClientMessageId(),
                tetheredUserDetailsDe.getRequest().getHeader().getClientMessageId());
    }
}