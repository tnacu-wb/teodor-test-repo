package uk.co.whitbread.hotel.payment.model;

public enum DatacashFormParameters {

    DATACASH_REFERENCE("order.id"),
    DATACASH_TRANSACTION_ID("transaction.id"),
    GATEWAY_RECOMMENDATION("response.gatewayRecommendation"),
    GATEWAY_RESULT("result");

    private String value;

    private DatacashFormParameters(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
