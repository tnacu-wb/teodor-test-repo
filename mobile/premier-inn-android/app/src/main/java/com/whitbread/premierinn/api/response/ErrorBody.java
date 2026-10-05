package com.whitbread.premierinn.api.response;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

/**
 * @Deprecated - Use the ApiError in ApiCommon instead
 */
@AutoValue
@Deprecated
public abstract class ErrorBody implements Parcelable {

    @SerializedName("code")
    public abstract int code();

    @Nullable
    @SerializedName("details")
    public abstract List<String> messagesList();

    @NonNull
    public static TypeAdapter<ErrorBody> typeAdapter(Gson gson) {
        return new AutoValue_ErrorBody.GsonTypeAdapter(gson);
    }

    public static ErrorBody create(int code, @NonNull String message) {
        return new AutoValue_ErrorBody(code, Collections.singletonList(message));
    }

    @Nullable
    public String errorMessage() {
        if (messagesList() != null && !messagesList().isEmpty()) {
            return messagesList().get(0);
        } else {
            return null;
        }
    }

    public static ErrorBody fromResponse(@NonNull Response response, @NonNull Gson gson) {
        if (response.errorBody() == null) {
            return new AutoValue_ErrorBody(0, null);
        }
        try {
            String errorBodyString = response.errorBody().string();
            return gson.fromJson(errorBodyString, AutoValue_ErrorBody.class);
        } catch (IOException e) {
            return new AutoValue_ErrorBody(0, null);
        }
    }
}