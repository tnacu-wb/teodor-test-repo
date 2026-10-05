package uk.co.whitbread.piba.account.converter;

import java.time.LocalDate;
import uk.co.whitbread.piba.account.mapper.CreditProposeMapper;
import uk.co.whitbread.piba.account.mapper.CustomerAccountMapper;
import uk.co.whitbread.piba.account.mapper.MemorableWordMapper;
import uk.co.whitbread.piba.account.mapper.TetheredMapper;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByDateCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.api.converter.WorldlineRequestTransformer;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimit;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordResponse;

public abstract class AbstractWorldlineAccountTransformer extends WorldlineRequestTransformer
    implements WorldlineTransformer {

  protected final CustomerAccountMapper customerAccountMapper;
  protected final MemorableWordMapper memorableWordMapper;
  protected final TetheredMapper tetheredMapper;
  protected final CreditProposeMapper creditProposeMapper;

  protected AbstractWorldlineAccountTransformer(WorldLineProperties worldLineProperties,
      CustomerAccountMapper customerAccountMapper,
      MemorableWordMapper memorableWordMapper,
      TetheredMapper tetheredMapper,
      CreditProposeMapper creditProposeMapper) {
    super(worldLineProperties);
    this.customerAccountMapper = customerAccountMapper;
    this.memorableWordMapper = memorableWordMapper;
    this.tetheredMapper = tetheredMapper;
    this.creditProposeMapper = creditProposeMapper;
  }

  @Override
  public CreditProposeNewLimit toCreditProposeNewLimitRequest(CreditProposeLimitRequest creditProposeLimitRequest) {
    CreditProposeNewLimit creditProposeNewLimit =
        creditProposeMapper.toCreditProposeNewLimit(creditProposeLimitRequest);
    CreditProposeNewLimitRequestType request = creditProposeNewLimit.getRequest();
    request.setHeader(getHeader());
    request.setTrustedPartnerCredentials(getCredentials());
    return creditProposeNewLimit;
  }

  @Override
  public CreditProposeLimitResponse toCreditProposeNewLimitResponse(
      CreditProposeNewLimitResponse creditProposeNewLimitResponse) {
    return creditProposeMapper.toCreditProposeLimitResponse(creditProposeNewLimitResponse);
  }

  @Override
  public CustomerAccountTransactionsResponse toCustomerAccountTransactionsResponse(
      CustomerAccountViewTransactionsResponse customerAccountViewTransactionsResponse) {
    return customerAccountMapper.toCustomerAccountTransaction(customerAccountViewTransactionsResponse);
  }

  @Override
  public CustomerAccountCurrentBalances toCustomerAccountCurrentBalancesResponse(
      CustomerAccountViewCurrentBalancesResponse customerAccountViewCurrentBalancesResponse) {
    return customerAccountMapper.toCustomerAccountBalances(
        customerAccountViewCurrentBalancesResponse.getResponse().getCustomerAccountBalancesDashboardPattern1());
  }

  @Override
  public TetheredUserDetailsResponse toTetheredUserDetailsResponse(
      TetheredUserDetailsGetResponse tetheredUserDetailsGetResponse) {
    return tetheredMapper.toTetheredUserDetailsResponse(tetheredUserDetailsGetResponse);
  }

  @Override
  public CustomerAccountInvoiceResponse toCustomerAccountInvoiceResponse(
      CustomerAccountInvoiceListResponse customerAccountInvoiceListResponse) {
    return customerAccountMapper.toCustomerAccountInvoiceResponse(customerAccountInvoiceListResponse);
  }

  @Override
  public CustomerAccountInvoiceListDownloadResponse toCustomerAccountInvoiceDownloadResponse(
      CustomerAccountInvoiceDownloadResponse customerAccountInvoiceDownloadResponse) {
    return customerAccountMapper.toCustomerAccInvoiceListDownloadResponse(customerAccountInvoiceDownloadResponse);
  }

  @Override
  public ResetMemorableWordResponse toResetMemorableWordResponse(UpdateMemorableWordResponse response) {
    return memorableWordMapper.toResetMemorableWordResponse(response);
  }

  protected void updateDateCriteriaToDefaultIfNull(
      CustomerAccountTransactionsRequest customerAccountTransactionsRequest) {
    if (customerAccountTransactionsRequest.getSearchCriteria() == null) {
      CustomerAccountTransactionsCriteria searchCriteria = new CustomerAccountTransactionsCriteria();
      searchCriteria.setDateSearch(getDefaultSearchCriteria(new CustomerAccountTransactionsByDateCriteria()));
      customerAccountTransactionsRequest.setSearchCriteria(searchCriteria);
    }
  }

  protected CustomerAccountTransactionsByDateCriteria getDefaultSearchCriteria(
      CustomerAccountTransactionsByDateCriteria dateCriteria) {
    dateCriteria.setDateFrom(
        dateCriteria.getDateFrom() == null ? LocalDate.now().minusMonths(2) : dateCriteria.getDateFrom());
    dateCriteria.setDateTo(dateCriteria.getDateTo() == null ? LocalDate.now() : dateCriteria.getDateTo());
    dateCriteria.setTransactionTypes(
        dateCriteria.getTransactionTypes() == null || dateCriteria.getTransactionTypes().isEmpty() ? "Uninvoiced"
            : dateCriteria.getTransactionTypes());
    return dateCriteria;
  }
}
