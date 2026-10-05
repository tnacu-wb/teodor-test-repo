package com.whitbread.premierinn.api.request.login;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class LoginBody {

    @SerializedName("username")
    public abstract String email();
    @SerializedName("password")
    public abstract String password();

    public static LoginBody create(@NonNull String email, @NonNull String password) {
        return new AutoValue_LoginBody(email, password);
    }

    @NonNull
    public static TypeAdapter<LoginBody> typeAdapter(Gson gson) {
        return new AutoValue_LoginBody.GsonTypeAdapter(gson);
    }
}
