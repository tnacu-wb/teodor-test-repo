package com.whitbread.premierinn.createaccount;

import android.content.Context;

import com.whitbread.premierinn.R;

import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class CreateAccountMessageProvider {

    private Context context;

    public CreateAccountMessageProvider(@NonNull Context context) {
        this.context = context;
    }

    public List<String> titlesList() {
        return Arrays.asList(context.getResources().getStringArray(R.array.titles));
    }

    public String getGenericErrorMessage() {
        return context.getString(R.string.create_account_generic_error);
    }

    public String getCustomerRegisteredErrorMessage() {
        return context.getString(R.string.create_account_user_registered_error);
    }

    public String getLoginError() {
        return context.getString(R.string.create_account_login_error);
    }
}
