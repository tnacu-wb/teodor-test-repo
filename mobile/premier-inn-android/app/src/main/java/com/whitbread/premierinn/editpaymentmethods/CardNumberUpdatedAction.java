package com.whitbread.premierinn.editpaymentmethods;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.common.forms.action.Action;

@AutoValue
public abstract class CardNumberUpdatedAction implements Action {

    public abstract String cardNumberEntered();

    public static CardNumberUpdatedAction create(String cardNumber) {
        return new AutoValue_CardNumberUpdatedAction(cardNumber);
    }
}