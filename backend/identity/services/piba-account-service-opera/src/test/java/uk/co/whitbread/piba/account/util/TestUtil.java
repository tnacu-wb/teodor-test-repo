package uk.co.whitbread.piba.account.util;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.core.io.ClassPathResource;
import uk.co.whitbread.piba.account.model.Currency;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransaction;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransactionDetail;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoice;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponseType;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByDateCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByInvoiceNumberCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponseType;
import uk.co.whitbread.piba.account.model.PagingRequestWithoutSort;
import uk.co.whitbread.piba.account.model.PagingResult;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.TetheredUserAccountOverview;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsOverview;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.enums.RegistrationRoles;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CurrencyType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountBalancesDashboardPattern1Type;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceItemType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.FileDownloadResultType;
import worldline.mst.bsm.api.b2b.pi.data.PagingResultType;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestUtil {

    public static final int SCHEME_CUSTOMER_ID = 778653;
    public static final String ACCOUNT_HOLDER = "AccountHolder";
    public static final String TETHERED_USER_GUID = "327f7a0c-9a33-41c2-808d-74f15f24797c";
    public static final String ACCOUNT_NAME = "Primary Account";
    public static final String ACCOUNT_NUMBER = "3089503200019372";

    public static final String TETHERED_USER_GUID2 = "a8c30dab-ba44-4b5b-ada6-b241ef65f8ac";
    public static final String ACCOUNT_NAME2 = "Finance Account";
    public static final String ACCOUNT_NUMBER2 = "3089503200019373";
    public static final int SCHEME_CUSTOMER_ID2 = 620367;
    public static final String FINANCE_USER = "ReportsAndInvoices";

    public static final String TETHERED_USER_GUID3 = "a8c30dab-ba44-4b5b-ada6-b241ef65f8fc";
    public static final String ACCOUNT_NAME3 = "Finance Account Card Holder";
    public static final String ACCOUNT_NUMBER3 = "3089503200019372";
    public static final int SCHEME_CUSTOMER_ID3 = 620366;
    public static final String FINANCE_USER_CARD_HOLDER = "AccountCardHolderWithReportsAndInvoices";
    public static final String CARD_HOLDER = "AccountCardHolder";
    public static final byte[] INVOICES_BINARY_DATA = new byte[]{};
    public static final int INVOICE_FILE_ID = 22094;
    public static final String BALANCE_ERROR_CODE = "InternalServerError";
    
    public static final String SESSION_ID = "Mjk5MDg2MEYtQTFEMC00MjkzLUFDMkItQ0I4ODgxMTBDMzQy";
    public static final String HASH = "Rehsk5MDg2MEYtQTFEMC00MjkzLUFDMkItQ0I4ODgxMTBDMzQy";
    public static final String MEMORABLE_WORD = "Remember12345";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String SHARED_SECRET_ID = "worldLineSharedSecret";
    public static final String SAMPLE_NONCE = "sampleNonce";
    public static final String COST_CENTRE_HOLDER = "CostCentreHolder";
    public static final List<String> TETHERED_GUIDS = Arrays.asList(TETHERED_USER_GUID, TETHERED_USER_GUID2, TETHERED_USER_GUID3);

    public static final String COMPANY_ID = "55";
    public static final String EMPLOYEE_ID = "22";
    public static final String USER_EMAIL = "InnBusiness@whitbread.com";

    public List<CustomerAccountCurrentBalancesRequest> populateCurrentBalancesRequest() {
        List<CustomerAccountCurrentBalancesRequest> customerAccountCurrentBalancesRequest = new ArrayList<>();
        CustomerAccountCurrentBalancesRequest request = new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID, SCHEME_CUSTOMER_ID, Scheme.GB);
        customerAccountCurrentBalancesRequest.add(request);
        request = new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID2, SCHEME_CUSTOMER_ID2, Scheme.GB);
        customerAccountCurrentBalancesRequest.add(request);
        request = new CustomerAccountCurrentBalancesRequest(TETHERED_USER_GUID3, SCHEME_CUSTOMER_ID3, Scheme.GB);
        customerAccountCurrentBalancesRequest.add(request);
        return customerAccountCurrentBalancesRequest;
    }

    public CustomerAccountCurrentBalances populateCurrentBalancesResponse() {
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse = new CustomerAccountCurrentBalances();
        customerAccountCurrentBalancesResponse.setRegistrationRoles(Collections.singletonList(RegistrationRoles.ACCOUNT_CARD_HOLDER));
        customerAccountCurrentBalancesResponse.setAccountName(ACCOUNT_NAME);
        customerAccountCurrentBalancesResponse.setAccountNumber(ACCOUNT_NUMBER);
        customerAccountCurrentBalancesResponse.setTetheredGuid(TETHERED_USER_GUID);
        customerAccountCurrentBalancesResponse.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        customerAccountCurrentBalancesResponse.setAvailable(new Currency(BigDecimal.valueOf(101.20), "GBP", "£"));
        customerAccountCurrentBalancesResponse.setCreditLimit(new Currency(BigDecimal.valueOf(660), "GBP", "£"));
        customerAccountCurrentBalancesResponse.setCurrentBalance(new Currency(BigDecimal.valueOf(240.00), "GBP", "£"));
        customerAccountCurrentBalancesResponse.setInterimPayments(new Currency(BigDecimal.valueOf(0.00), "GBP", "£"));
        customerAccountCurrentBalancesResponse.setNewTransactions(new Currency(BigDecimal.valueOf(0.00), "GBP", "£"));
        customerAccountCurrentBalancesResponse.setOutstanding(new Currency(BigDecimal.valueOf(100.00), "GBP", "£"));
        return customerAccountCurrentBalancesResponse;
    }

    public TetheredUserDetailsResponse populateTetheredUserResponse(int schemeCustomerId) {

        if (schemeCustomerId == SCHEME_CUSTOMER_ID)
            return new TetheredUserDetailsResponse(new TetheredUserDetailsOverview(TETHERED_USER_GUID, TETHERED_USER_GUID, ACCOUNT_HOLDER, 1),
                    new TetheredUserAccountOverview(SCHEME_CUSTOMER_ID, SCHEME_CUSTOMER_ID, ACCOUNT_NAME, ACCOUNT_NUMBER));
        else if (schemeCustomerId == SCHEME_CUSTOMER_ID2)
            return new TetheredUserDetailsResponse(new TetheredUserDetailsOverview(TETHERED_USER_GUID2, TETHERED_USER_GUID2, FINANCE_USER, 0),
                    new TetheredUserAccountOverview(SCHEME_CUSTOMER_ID2, SCHEME_CUSTOMER_ID2, ACCOUNT_NAME2, ACCOUNT_NUMBER2));
        else if (schemeCustomerId == SCHEME_CUSTOMER_ID3)
            return new TetheredUserDetailsResponse(new TetheredUserDetailsOverview(TETHERED_USER_GUID3, TETHERED_USER_GUID3, FINANCE_USER_CARD_HOLDER, 0),
                    new TetheredUserAccountOverview(SCHEME_CUSTOMER_ID3, SCHEME_CUSTOMER_ID3, ACCOUNT_NAME3, ACCOUNT_NUMBER3));
        else return null;
    }

    public TetheredUserDetailsResponse populateCostCenterTetheredUserResponse() {
            return new TetheredUserDetailsResponse(new TetheredUserDetailsOverview(TETHERED_USER_GUID3, TETHERED_USER_GUID3, COST_CENTRE_HOLDER, 0),
                    new TetheredUserAccountOverview(SCHEME_CUSTOMER_ID3, SCHEME_CUSTOMER_ID3, ACCOUNT_NAME3, ACCOUNT_NUMBER3));
    }
    public TetheredUserDetailsResponse createTetheredUserDetailsResponse() {
        return new TetheredUserDetailsResponse(new TetheredUserDetailsOverview(TETHERED_USER_GUID, TETHERED_USER_GUID, ACCOUNT_HOLDER, 3),
                new TetheredUserAccountOverview(SCHEME_CUSTOMER_ID, SCHEME_CUSTOMER_ID, ACCOUNT_NAME, ACCOUNT_NUMBER));
    }

    public CustomerAccountViewCurrentBalancesResponse populateViewCurrentBalancesResponse() {
        CurrencyType currencyValue = new CurrencyType();
        currencyValue.setAmount(BigDecimal.valueOf(100));
        currencyValue.setCurrencyCode("826");
        CustomerAccountViewCurrentBalancesResponse response = new CustomerAccountViewCurrentBalancesResponse();
        CustomerAccountViewCurrentBalancesResponseType responseType = new CustomerAccountViewCurrentBalancesResponseType();
        CustomerAccountBalancesDashboardPattern1Type balancePattern1 = new CustomerAccountBalancesDashboardPattern1Type();
        responseType.setCustomerAccountBalancesDashboardPattern1(balancePattern1);
        response.setResponse(responseType);
        balancePattern1.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        balancePattern1.setAvailable(currencyValue);
        balancePattern1.setCreditLimit(currencyValue);
        balancePattern1.setCurrentBalance(currencyValue);
        balancePattern1.setInterimPayments(currencyValue);
        balancePattern1.setNewTransactions(currencyValue);
        balancePattern1.setOutstanding(currencyValue);
        return response;
    }

    public CustomerAccountTransactionsRequest populateTransactionRequest(boolean setInvoiceNumberCriteria, boolean setDateCriteria) {
        CustomerAccountTransactionsRequest customerAccountTransactionsRequest = new CustomerAccountTransactionsRequest();
        customerAccountTransactionsRequest.setSearchCriteria(populateSearchCriteria(setInvoiceNumberCriteria, setDateCriteria));
        customerAccountTransactionsRequest.setPagingRequest(populatePagingRequestWithoutSort());
        customerAccountTransactionsRequest.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        customerAccountTransactionsRequest.setTetheredUserGuid(TETHERED_USER_GUID);
        return customerAccountTransactionsRequest;
    }

    public CustomerAccountTransactionsResponse populateViewTransactionsResponse(int lastPage) {
        CustomerAccountTransactionsResponse customerAccountTransactionsResponse = new CustomerAccountTransactionsResponse();
        CustomerAccountTransactionsResponseType customerAccountTransactionsResponseType = new CustomerAccountTransactionsResponseType();
        customerAccountTransactionsResponseType.setTransactions(populateCustomerAccountCardTransaction());
        customerAccountTransactionsResponse.setResponse(customerAccountTransactionsResponseType);
        customerAccountTransactionsResponse.setPagingResult(buildPagingResult(lastPage));

        return customerAccountTransactionsResponse;
    }

    public CreditProposeNewLimitResponse populateCreditProposeNewLimitResponse() {
        CreditProposeNewLimitResponse creditProposeNewLimitResponse = new CreditProposeNewLimitResponse();
        CreditProposeNewLimitResponseType creditProposeNewLimitResponseType = new CreditProposeNewLimitResponseType();
        creditProposeNewLimitResponseType.setCLIRequestId("888991ff-d0ee-435d-ac78-57e91556deca");
        creditProposeNewLimitResponse.setResponse(creditProposeNewLimitResponseType);
        return creditProposeNewLimitResponse;
    }


    public CustomerAccountViewTransactionsResponse populateTransactionsResponse() {
        File file = null;
        try {
            file = new ClassPathResource("data/viewtransactionsresponse.xml").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(CustomerAccountViewTransactionsResponse.class);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        Unmarshaller um = null;
        try {
            um = context.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        try {
            return (CustomerAccountViewTransactionsResponse) um.unmarshal(new FileReader(file));
        } catch (JAXBException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<CustomerAccountCardTransaction> populateCustomerAccountCardTransaction() {
        CustomerAccountCardTransaction customerAccountCardTransaction = new CustomerAccountCardTransaction();
        customerAccountCardTransaction.setCustomerOwnRef("customerRef");
        customerAccountCardTransaction.setPan("1232132");
        customerAccountCardTransaction.setLocation("Oxford");
        customerAccountCardTransaction.setCardName("Name Surname");
        customerAccountCardTransaction.setGrossAmount(new Currency(BigDecimal.valueOf(755.88), "826", "£"));
        customerAccountCardTransaction.setInvoiceDate(LocalDate.of(2021, 12, 1));
        customerAccountCardTransaction.setInvoiceNo("1");
        customerAccountCardTransaction.setLineItems(populateLineItems());
        customerAccountCardTransaction.setNetAmount(new Currency(BigDecimal.valueOf(719.89), "826", "£"));
        customerAccountCardTransaction.setSalesOrderNumber("1");
        customerAccountCardTransaction.setTaxAmount(new Currency(BigDecimal.valueOf(35.99), "826", "£"));
        customerAccountCardTransaction.setPurchaseOrderReference("orderRef");
        customerAccountCardTransaction.setTransactionDate(LocalDateTime.of(2021, 12, 1, 13, 30));
        return Collections.singletonList(customerAccountCardTransaction);

    }

    private List<CustomerAccountCardTransactionDetail> populateLineItems() {
        CustomerAccountCardTransactionDetail customerAccountCardTransactionDetail = new CustomerAccountCardTransactionDetail();
        customerAccountCardTransactionDetail.setDescription("Accommodation");
        customerAccountCardTransactionDetail.setGrossAmount(new Currency(BigDecimal.valueOf(755.88), "826", "£"));
        customerAccountCardTransactionDetail.setGuestName("Name Surname");
        customerAccountCardTransactionDetail.setInvoiceLineItem(5);
        customerAccountCardTransactionDetail.setNetAmount(new Currency(BigDecimal.valueOf(719.89), "826", "£"));
        customerAccountCardTransactionDetail.setQuantity(1);
        customerAccountCardTransactionDetail.setTaxAmount(new Currency(BigDecimal.valueOf(35.99), "826", "£"));
        return Collections.singletonList(customerAccountCardTransactionDetail);
    }

    private PagingResult buildPagingResult(int lastPage) {
        PagingResult pagingResult = new PagingResult();
        pagingResult.setFromRecord(41);
        pagingResult.setLastPage(lastPage);
        pagingResult.setToRecord(50);
        pagingResult.setTotalRecordCount(594);
        return pagingResult;
    }

    private PagingRequestWithoutSort populatePagingRequestWithoutSort() {
        PagingRequestWithoutSort pagingRequestWithoutSort = new PagingRequestWithoutSort();
        pagingRequestWithoutSort.setMaximumDisplayRows(10);
        pagingRequestWithoutSort.setPage(5);
        return pagingRequestWithoutSort;
    }

    private CustomerAccountTransactionsCriteria populateSearchCriteria(boolean setInvoiceNumberCriteria, boolean setDateCriteria) {
        CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria = new CustomerAccountTransactionsCriteria();
        if (setInvoiceNumberCriteria) {
            CustomerAccountTransactionsByInvoiceNumberCriteria invoiceNumberCriteria = new CustomerAccountTransactionsByInvoiceNumberCriteria();
            invoiceNumberCriteria.setInvoiceNumber(5);
            customerAccountTransactionsCriteria.setInvoiceNumberSearch(invoiceNumberCriteria);
        }
        if (setDateCriteria) {
            CustomerAccountTransactionsByDateCriteria dateCriteria = new CustomerAccountTransactionsByDateCriteria();
            dateCriteria.setDateFrom(LocalDate.of(2020, 12, 1));
            dateCriteria.setDateTo(LocalDate.of(2020, 12, 1));
            dateCriteria.setPan("12345678");
            dateCriteria.setTransactionTypes("Both");
            customerAccountTransactionsCriteria.setDateSearch(dateCriteria);
        }
        return customerAccountTransactionsCriteria;

    }


    public CustomerAccountInvoiceRequest createCustomerAccountInvoiceRequest(String tetheredUserGuid, int schemeCustomerId) {
        CustomerAccountInvoiceCriteria customerAccountInvoiceCriteria = new CustomerAccountInvoiceCriteria(LocalDate.of(2021, 10, 12), LocalDate.of(2021, 12, 20));
        CustomerAccountInvoiceRequest customerAccountInvoiceRequest = new CustomerAccountInvoiceRequest();
        customerAccountInvoiceRequest.setSchemeCustomerId(schemeCustomerId);
        customerAccountInvoiceRequest.setTetheredUserGuid(tetheredUserGuid);
        customerAccountInvoiceRequest.setSearchCriteria(customerAccountInvoiceCriteria);
        customerAccountInvoiceRequest.setPagingRequest(new PagingRequestWithoutSort(1, 10));
        return customerAccountInvoiceRequest;
    }

    public CustomerAccountInvoiceListResponse createCustomerAccountInvoiceResponse() {
        CustomerAccountInvoiceListResponse customerAccountInvoiceListResponse = new CustomerAccountInvoiceListResponse();
        List<CustomerAccountInvoiceItemType> customerAccountInvoiceItemTypeList = new ArrayList<>();
        customerAccountInvoiceItemTypeList.add(createCustomerAccountInvoiceItemType("12345"));
        customerAccountInvoiceItemTypeList.add(createCustomerAccountInvoiceItemType("234"));
        customerAccountInvoiceItemTypeList.add(createCustomerAccountInvoiceItemType("456"));
        PagingResultType pagingResult = new PagingResultType();
        pagingResult.setFromRecord(1);
        pagingResult.setToRecord(3);
        pagingResult.setTotalRecordCount(100);
        pagingResult.setLastPage(34);
        CustomerAccountInvoiceListResponseType respType = new CustomerAccountInvoiceListResponseType();
        respType.setResultCode("OK");
        respType.getCustomerAccountInvoiceItem().addAll(customerAccountInvoiceItemTypeList);
        respType.setPagingResult(pagingResult);
        customerAccountInvoiceListResponse.setResponse(respType);
        return customerAccountInvoiceListResponse;
    }

    private CustomerAccountInvoiceItemType createCustomerAccountInvoiceItemType(String invoiceNumber) {
        CustomerAccountInvoiceItemType itemType = new CustomerAccountInvoiceItemType();
        itemType.setInvoiceNo(invoiceNumber);
        itemType.setFileAutoID(54321);
        itemType.setInvoiceValue(createCurrencyType(BigDecimal.valueOf(234.78), "826"));
        itemType.setBroughtForward(createCurrencyType(BigDecimal.valueOf(134.78), "826"));
        itemType.setOverdueBalance(createCurrencyType(BigDecimal.valueOf(34.80), "826"));
        itemType.setStatementBalance(createCurrencyType(BigDecimal.valueOf(55.50), "826"));
        itemType.setPaymentsReceived(createCurrencyType(BigDecimal.valueOf(99.99), "826"));
        return itemType;
    }

    private CurrencyType createCurrencyType(BigDecimal amount, String currencyCode) {
        CurrencyType ct = new CurrencyType();
        ct.setAmount(amount);
        ct.setCurrencyCode(currencyCode);
        return ct;
    }

    public CustomerAccountInvoiceResponse createCustomerAccountInvoiceResponse(int noOfInvoices) {
        CustomerAccountInvoiceResponse customerAccountInvoiceResponse = new CustomerAccountInvoiceResponse();
        CustomerAccountInvoiceResponseType responseType = new CustomerAccountInvoiceResponseType();
        List<CustomerAccountInvoice> invoices = new ArrayList<>();

        StringBuilder invoiceNumberBuilder = new StringBuilder("123");
        for (int i = 0; i < noOfInvoices; i++) {
            invoiceNumberBuilder.append("0");
            invoices.add(createCustomerAccountInvoice(invoiceNumberBuilder.toString()));
        }
        responseType.setInvoices(invoices);

        PagingResult pagingResult = new PagingResult();
        pagingResult.setFromRecord(1);
        pagingResult.setToRecord(noOfInvoices);
        pagingResult.setLastPage(10);
        pagingResult.setTotalRecordCount(10 * noOfInvoices);
        customerAccountInvoiceResponse.setResponse(responseType);
        customerAccountInvoiceResponse.setPagingResult(pagingResult);
        return customerAccountInvoiceResponse;
    }

    private CustomerAccountInvoice createCustomerAccountInvoice(String invoiceNumber) {
        Currency amount = new Currency(BigDecimal.valueOf(23.67), "GBP", "£");
        return (new CustomerAccountInvoice("2021-03-05", invoiceNumber, amount, amount, amount,
                amount, amount, Integer.valueOf(23457)));
    }

    public CustomerAccountInvoiceDownloadResponse createInvoiceListDownloadResponse() {
        FileDownloadResultType fileDownloadResult = new FileDownloadResultType();
        fileDownloadResult.setFileName("filename.pdf");
        fileDownloadResult.setBinaryData(INVOICES_BINARY_DATA);
        fileDownloadResult.setFileExtension(".pdf");
        CustomerAccountInvoiceDownloadResponseType responseType = new CustomerAccountInvoiceDownloadResponseType();
        responseType.setResultCode("OK");
        responseType.setFileDownloadResult(fileDownloadResult);
        CustomerAccountInvoiceDownloadResponse response = new CustomerAccountInvoiceDownloadResponse();
        response.setResponse(responseType);
        return response;
    }
    
    
    public PibaTetheredGuidResponse createPibaTetheredGuidResponse(String companyId, String employeeId) {
        return new PibaTetheredGuidResponse(companyId, employeeId, Arrays.asList(TETHERED_USER_GUID, TETHERED_USER_GUID2, TETHERED_USER_GUID3), Scheme.GB);
    }

    public PibaTetheredGuidResponse createSinglePibaTetheredGuidResponse(String companyId, String employeeId) {
        return new PibaTetheredGuidResponse(companyId, employeeId, Arrays.asList(TETHERED_USER_GUID), Scheme.DE);
    }

    public List<PibaTetheredGuidResponse> createPibaTetheredGuidResponseMultiple(String companyId, String employeeId) {
        return List.of(new PibaTetheredGuidResponse(companyId, employeeId, Arrays.asList(TETHERED_USER_GUID, TETHERED_USER_GUID2), Scheme.GB), new PibaTetheredGuidResponse(companyId, employeeId, List.of(TETHERED_USER_GUID3), Scheme.DE));
    }

    public List<PibaTetheredGuidResponse> createPibaTetheredGuidResponseMultipleEmptyGuids(String companyId, String employeeId) {
        return List.of(new PibaTetheredGuidResponse(companyId, employeeId, null, Scheme.GB), new PibaTetheredGuidResponse(companyId, employeeId, List.of(TETHERED_USER_GUID3), Scheme.DE));
    }

    public EmployeeDetails createEmployeeDetails(){
        return new EmployeeDetails(COMPANY_ID, EMPLOYEE_ID);
    }

    public CdhEmployeeDetails createCdhEmployeeDetails(){
        return CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID)
            .build();
    }

    public CdhEmployeeDetails createCdhEmployeeDetailsWithEmail() {
        return CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID)
            .userEmail(USER_EMAIL)
            .build();
    }
}
