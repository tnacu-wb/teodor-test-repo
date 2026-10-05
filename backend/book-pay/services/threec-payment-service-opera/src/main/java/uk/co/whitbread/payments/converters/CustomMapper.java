package uk.co.whitbread.payments.converters;

import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionRequest;

public interface CustomMapper {
    void mapForNoCardRead(ProviderAccount account, NoCardReadTransactionRequest.Params params, PaymentRequest paymentRequest);
    void mapForInitialise(ProviderAccount account, InitialiseRequest fraudCheckData, PaymentRequest paymentRequest);
    void mapForPaypal(ProviderAccount account, PaypalForwardAPITransactionRequest params, PaymentRequest paymentRequest);
}
