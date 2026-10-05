package com.whitbread.premierinn.api.response.login;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class LoginResponse implements Parcelable {

    public abstract boolean loginSuccessful();

    @Nullable
    public abstract String sessionId();

    @NonNull
    public static TypeAdapter<LoginResponse> typeAdapter(Gson gson) {
        return new AutoValue_LoginResponse.GsonTypeAdapter(gson);
    }
}
