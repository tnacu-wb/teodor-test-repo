package com.whitbread.premierinn.editpaymentmethods;

import android.content.Context;
import com.whitbread.premierinn.R;
import javax.inject.Inject;

public class EditPaymentMethodsMessageProvider {

    private final Context context;
    @Inject
    public EditPaymentMethodsMessageProvider(Context context) {
        this.context = context;
    }

    public String addCardButtonText() {
        return context.getResources().getString(R.string.save_changes_label);
    }

    public String replaceCardButtonText() {
        return context.getResources().getString(R.string.edit_payment_methods_replace_card);
    }

    public String replaceCardTitle() {
        return context.getResources().getString(R.string.edit_payment_methods_replace_card_title);
    }

    public String addCardTitle() {
        return context.getResources().getString(R.string.payment_methods_label);
    }
}
