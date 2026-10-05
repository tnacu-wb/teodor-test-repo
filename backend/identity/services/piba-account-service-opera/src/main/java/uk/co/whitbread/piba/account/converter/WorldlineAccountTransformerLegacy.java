package uk.co.whitbread.piba.account.converter;

import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.mapper.CreditProposeMapper;
import uk.co.whitbread.piba.account.mapper.CustomerAccountMapper;
import uk.co.whitbread.piba.account.mapper.MemorableWordMapper;
import uk.co.whitbread.piba.account.mapper.TetheredMapper;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownload;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalances;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactions;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsRequestType;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserRequestType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetRequestType;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWord;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordRequestType;

@Component
@ConditionalOnProperty(name = "worldline.tetheringPlus.enabled", havingValue = "false", matchIfMissing = true)
public class WorldlineAccountTransformerLegacy extends AbstractWorldlineAccountTransformer {

    public WorldlineAccountTransformerLegacy(WorldLineProperties worldLineProperties,
            CustomerAccountMapper customerAccountMapper,
            TetheredMapper tetheredMapper,
            MemorableWordMapper memorableWordMapper,
            CreditProposeMapper creditProposeMapper) {
        super(worldLineProperties, customerAccountMapper, memorableWordMapper, tetheredMapper, creditProposeMapper);
    }

    @Override
    public CustomerAccountViewCurrentBalances toCustomerAccountCurrentBalancesRequest(CustomerAccountCurrentBalancesRequest balancesRequest) {
        CustomerAccountViewCurrentBalances viewBalanceRequest = customerAccountMapper.toCustomerAccountBalancesView(balancesRequest);
        CustomerAccountViewCurrentBalancesRequestType request = viewBalanceRequest.getRequest();
        request.setHeader(getHeader());
        request.setTrustedPartnerCredentials(getCredentials());
        return viewBalanceRequest;
    }

    @Override
    public CustomerAccountViewTransactions toCustomerAccountTransactionsRequest(CustomerAccountTransactionsRequest customerAccountTransactionsRequest) {
        updateDateCriteriaToDefaultIfNull(customerAccountTransactionsRequest);
        CustomerAccountViewTransactions customerAccountViewTransactions = customerAccountMapper.toCustomerAccountViewTransaction(customerAccountTransactionsRequest);
        CustomerAccountViewTransactionsRequestType request = customerAccountViewTransactions.getRequest();
        HeaderType header = Scheme.DE.equals(customerAccountTransactionsRequest.getScheme()) ? getDeHeader() : getGbHeader();
        header.setClientMessageId(String.valueOf(UUID.randomUUID()));
        var credentialsType = Scheme.DE.equals(customerAccountTransactionsRequest.getScheme()) ? getDeCredentials() : getGbCredentials();
        request.setHeader(header);
        request.setTrustedPartnerCredentials(credentialsType);
        return customerAccountViewTransactions;
    }

    @Override
    public TetheredUserDetailsGet toTetheredUserDetailsRequest(String tetheredUserGuid, Scheme scheme) {
        TetheredUserDetailsGetRequestType requestType = new TetheredUserDetailsGetRequestType();
        requestType.setHeader(getHeader());
        requestType.setTrustedPartnerCredentials(getCredentials());
        requestType.setTetheredUserGuid("{" + tetheredUserGuid + "}");
        TetheredUserDetailsGet request = new TetheredUserDetailsGet();
        request.setRequest(requestType);
        return request;
    }

    @Override
    public CustomerAccountInvoiceList toCustomerAccountInvoiceRequest(CustomerAccountInvoiceRequest customerAccountInvoiceRequest) {
        CustomerAccountInvoiceList customerAccountInvoiceList = customerAccountMapper.toCustomerAccountInvoiceList(customerAccountInvoiceRequest);
        CustomerAccountInvoiceListRequestType requestType = customerAccountInvoiceList.getRequest();
        requestType.setHeader(getHeader());
        requestType.setTrustedPartnerCredentials(getCredentials());
        customerAccountInvoiceList.setRequest(requestType);
        return customerAccountInvoiceList;
    }

    @Override
    public CustomerAccountInvoiceDownload toCustomerAccountInvoiceDownloadRequest(int schemeCustomerId,
        String tetheredUserGuid, int fileId, Scheme scheme) {
        HeaderType header = Scheme.DE.equals(scheme) ? getDeHeader() : getGbHeader();
        header.setClientMessageId(String.valueOf(UUID.randomUUID()));
        var credentialsType = Scheme.DE.equals(scheme) ? getDeCredentials() : getGbCredentials();
        CustomerAccountInvoiceDownloadRequestType request = new CustomerAccountInvoiceDownloadRequestType();
        request.setHeader(header);
        request.setTrustedPartnerCredentials(credentialsType);
        request.setTetheredUserGuid("{" + tetheredUserGuid + "}");
        request.setSchemeCustomerId(schemeCustomerId);
        request.setFileID(fileId);
        CustomerAccountInvoiceDownload download = new CustomerAccountInvoiceDownload();
        download.setRequest(request);
        return download;
    }

    @Override
    public LoginTetheredUser toLoginTetheredUserRequest(String guid, Scheme scheme) {
        LoginTetheredUserRequestType requestType = new LoginTetheredUserRequestType();
        requestType.setHeader(getHeader());
        requestType.setTrustedPartnerCredentials(getCredentials());
        requestType.setTetheredUserGuid("{" + guid + "}");
        LoginTetheredUser login = new LoginTetheredUser();
        login.setRequest(requestType);
        return login;
    }

    @Override
    public UpdateMemorableWord toUpdateMemorableWordRequest(UpdateMemorableWordRequest request) {
        UpdateMemorableWord updateMemorableWord = memorableWordMapper.toUpdateMemorableWord(request);
        UpdateMemorableWordRequestType requestType = updateMemorableWord.getRequest();
        requestType.setHeader(getHeader());
        updateMemorableWord.setRequest(requestType);
        return updateMemorableWord;
    }
}

