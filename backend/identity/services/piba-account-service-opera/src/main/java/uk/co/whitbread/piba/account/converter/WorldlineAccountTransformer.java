package uk.co.whitbread.piba.account.converter;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWord;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordRequestType;

@Slf4j
@Component
@ConditionalOnProperty(name = "worldline.tetheringPlus.enabled", havingValue = "true")
public class WorldlineAccountTransformer extends AbstractWorldlineAccountTransformer {

    public WorldlineAccountTransformer(WorldLineProperties worldLineProperties,
            CustomerAccountMapper customerAccountMapper,
            MemorableWordMapper memorableWordMapper,
            TetheredMapper tetheredMapper,
            CreditProposeMapper creditProposeMapper) {
        super(worldLineProperties, customerAccountMapper, memorableWordMapper, tetheredMapper, creditProposeMapper);
    }

    @Override
    public CustomerAccountViewCurrentBalances toCustomerAccountCurrentBalancesRequest(CustomerAccountCurrentBalancesRequest balancesRequest) {
        CustomerAccountViewCurrentBalances viewBalanceRequest = customerAccountMapper.toCustomerAccountBalancesView(balancesRequest);
        CustomerAccountViewCurrentBalancesRequestType request = viewBalanceRequest.getRequest();
        request.setHeader(getWorldLineRequestHeader(balancesRequest.getScheme()));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(balancesRequest.getScheme()));
        return viewBalanceRequest;
    }

    @Override
    public CustomerAccountViewTransactions toCustomerAccountTransactionsRequest(CustomerAccountTransactionsRequest customerAccountTransactionsRequest) {
        updateDateCriteriaToDefaultIfNull(customerAccountTransactionsRequest);
        CustomerAccountViewTransactions customerAccountViewTransactions = customerAccountMapper.toCustomerAccountViewTransaction(customerAccountTransactionsRequest);
        CustomerAccountViewTransactionsRequestType request = customerAccountViewTransactions.getRequest();
        request.setHeader(getWorldLineRequestHeader(customerAccountTransactionsRequest.getScheme()));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(customerAccountTransactionsRequest.getScheme()));
        return customerAccountViewTransactions;
    }

    @Override
    public TetheredUserDetailsGet toTetheredUserDetailsRequest(String tetheredUserGuid, Scheme scheme) {
        TetheredUserDetailsGetRequestType requestType = new TetheredUserDetailsGetRequestType();
        requestType.setHeader(getWorldLineRequestHeader(scheme));
        requestType.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        requestType.setTetheredUserGuid("{" + tetheredUserGuid + "}");
        TetheredUserDetailsGet request = new TetheredUserDetailsGet();
        request.setRequest(requestType);
        return request;
    }

    @Override
    public CustomerAccountInvoiceList toCustomerAccountInvoiceRequest(CustomerAccountInvoiceRequest customerAccountInvoiceRequest) {
        CustomerAccountInvoiceList customerAccountInvoiceList = customerAccountMapper.toCustomerAccountInvoiceList(customerAccountInvoiceRequest);
        CustomerAccountInvoiceListRequestType requestType = customerAccountInvoiceList.getRequest();
        requestType.setHeader(getWorldLineRequestHeader(customerAccountInvoiceRequest.getScheme()));
        requestType.setTrustedPartnerCredentials(getWorldLineCredentialsType(customerAccountInvoiceRequest.getScheme()));
        customerAccountInvoiceList.setRequest(requestType);
        return customerAccountInvoiceList;
    }

    @Override
    public CustomerAccountInvoiceDownload toCustomerAccountInvoiceDownloadRequest(int schemeCustomerId,
        String tetheredUserGuid, int fileId, Scheme scheme) {
        CustomerAccountInvoiceDownloadRequestType request = new CustomerAccountInvoiceDownloadRequestType();
        request.setHeader(getWorldLineRequestHeader(scheme));
        request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
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
        requestType.setHeader(getWorldLineRequestHeader(scheme));
        requestType.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
        requestType.setTetheredUserGuid("{" + guid + "}");
        LoginTetheredUser login = new LoginTetheredUser();
        login.setRequest(requestType);
        return login;
    }

    @Override
    public UpdateMemorableWord toUpdateMemorableWordRequest(UpdateMemorableWordRequest request) {
        UpdateMemorableWord updateMemorableWord = memorableWordMapper.toUpdateMemorableWord(request);
        UpdateMemorableWordRequestType requestType = updateMemorableWord.getRequest();
        requestType.setHeader(getWorldLineRequestHeader(request.getScheme()));
        updateMemorableWord.setRequest(requestType);
        return updateMemorableWord;
    }

    private HeaderType getWorldLineRequestHeader(Scheme scheme) {
        HeaderType header = Scheme.DE.equals(scheme) ? getDeHeader() : getGbHeader();
        header.setClientMessageId(String.valueOf(UUID.randomUUID()));
        log.debug("Adding ClientMessageId={}", header.getClientMessageId());
        return header;
    }

    private TrustedPartnerCredentialsType getWorldLineCredentialsType(Scheme scheme) {
        return Scheme.DE.equals(scheme) ? getDeCredentials() : getGbCredentials();
    }
}

