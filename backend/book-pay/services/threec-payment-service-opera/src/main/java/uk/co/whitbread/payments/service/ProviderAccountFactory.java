package uk.co.whitbread.payments.service;

import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.SaveCardRequest;
import uk.co.whitbread.payments.properties.ProviderAccount;

public interface ProviderAccountFactory {
    ProviderAccount getAccount(PaymentRequest paymentRequest);

    ProviderAccount getAccountForSite(String siteIdentifier);

    ProviderAccount getSaveCardAccount(SaveCardRequest saveCardRequest);

    ProviderAccount getAuthorizeScaAccount();
}
