package uk.co.whitbread.piba.account.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionTypes {
    INVOICED("Invoiced"),
    UNINVOICED("Uninvoiced"),
    BOTH("Both");

    private final String transactionType;

}
