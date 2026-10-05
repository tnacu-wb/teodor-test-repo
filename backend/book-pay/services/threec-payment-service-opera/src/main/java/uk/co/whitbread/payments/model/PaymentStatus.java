package uk.co.whitbread.payments.model;

public enum PaymentStatus {

    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    PENDING("PENDING"),
    NO_PAYMENT_ATTEMPT("NO_PAYMENT_ATTEMPT");

    final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
