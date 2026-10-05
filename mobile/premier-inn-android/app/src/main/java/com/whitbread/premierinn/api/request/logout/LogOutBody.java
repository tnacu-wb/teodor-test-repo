package com.whitbread.premierinn.api.request.logout;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class LogOutBody {

    @SerializedName("sessionId")
    public abstract String sessionId();

    @NonNull
    public static TypeAdapter<LogOutBody> typeAdapter(Gson gson) {
        return new AutoValue_LogOutBody.GsonTypeAdapter(gson);
    }

    public static LogOutBody create(@NonNull String customerSessionId) {
        return new AutoValue_LogOutBody(customerSessionId);
    }
}
