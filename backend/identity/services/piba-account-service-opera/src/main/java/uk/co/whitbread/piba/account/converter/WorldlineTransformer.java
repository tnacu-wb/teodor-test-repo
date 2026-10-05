package uk.co.whitbread.piba.account.converter;

import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimit;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownload;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalances;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactions;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWord;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordResponse;

public interface WorldlineTransformer {

  UpdateMemorableWord toUpdateMemorableWordRequest(UpdateMemorableWordRequest updateMemorableWordRequest);

  ResetMemorableWordResponse toResetMemorableWordResponse(UpdateMemorableWordResponse updateMemorableWordResponse);

  CustomerAccountViewCurrentBalances toCustomerAccountCurrentBalancesRequest(CustomerAccountCurrentBalancesRequest customerAccountCurrentBalancesRequest);

  CustomerAccountCurrentBalances toCustomerAccountCurrentBalancesResponse(CustomerAccountViewCurrentBalancesResponse response);

  TetheredUserDetailsGet toTetheredUserDetailsRequest(String tetheredUserGuid, Scheme scheme);

  TetheredUserDetailsResponse toTetheredUserDetailsResponse(TetheredUserDetailsGetResponse response);

  CustomerAccountViewTransactions toCustomerAccountTransactionsRequest(CustomerAccountTransactionsRequest customerAccountTransactionsRequest);

  CustomerAccountTransactionsResponse toCustomerAccountTransactionsResponse(CustomerAccountViewTransactionsResponse response);

  CreditProposeNewLimit toCreditProposeNewLimitRequest(CreditProposeLimitRequest creditProposeLimitRequest);

  CreditProposeLimitResponse toCreditProposeNewLimitResponse(CreditProposeNewLimitResponse response);

  CustomerAccountInvoiceList toCustomerAccountInvoiceRequest(CustomerAccountInvoiceRequest customerAccountInvoiceRequest);

  CustomerAccountInvoiceResponse toCustomerAccountInvoiceResponse(CustomerAccountInvoiceListResponse response);

  CustomerAccountInvoiceDownload toCustomerAccountInvoiceDownloadRequest(int schemeCustomerId, String tetheredUserGuid, int fileId, Scheme scheme);

  CustomerAccountInvoiceListDownloadResponse toCustomerAccountInvoiceDownloadResponse(CustomerAccountInvoiceDownloadResponse response);

  LoginTetheredUser toLoginTetheredUserRequest(String guid, Scheme scheme);
}
