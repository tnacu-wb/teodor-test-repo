package com.whitbread.premierinn.login;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LoginDataInput {

    public abstract boolean emailErrorEnabled();
    public abstract String email();
    public abstract String password();
    public abstract Boolean businessBooker();

    public static LoginDataInput create(boolean emailErrorEnabled, @NonNull String email,
                                        @NonNull String password, boolean businessBooker) {
        return new AutoValue_LoginDataInput(emailErrorEnabled, email, password, businessBooker);
    }
}
