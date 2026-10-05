package uk.co.whitbread.payments.model;

public enum PaymentType {

    CARD("ccdc"),
    PIBA("PIBAGB"),
    PIBA_EU("PIBADE"),
    PLANET_BASE_WALLETS_TEST("PLANET_BASE_WALLETS_TEST"),
    WALLET_APPLE("WALLET_APPLE"),
    WALLET_GOOGLE("WALLET_GOOGLE"),
    PAYPAL("PAYPAL"),
    WB_WALLETS_TEST("WB_WALLETS_TEST");


    final String eckohMapping;

    PaymentType(String eckohMapping) {
        this.eckohMapping = eckohMapping;
    }

    public String getEckohMapping() {
        return eckohMapping;
    }
}
