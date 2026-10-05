package uk.co.whitbread.payments.model;

public enum CardType {
    VI("Visa Credit"), MD("Mastercard Debit"), AC("Mastercard Credit"), AM("American Express"), DI("Diners Club"), DL("Visa Debit"), EL("Electron"), MA("Maestro");

    CardType(String s) {
    }
}
