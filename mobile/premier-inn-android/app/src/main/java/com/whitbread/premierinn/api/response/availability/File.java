package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@Deprecated // Use com.whitbread.premierinn.data.remote.File
@AutoValue
public abstract class File implements Parcelable {

    public static final String ALLERGY_INFO_LABEL = "Allergy info";

    @SerializedName("path")
    @Nullable
    public abstract String path();

    @SerializedName("label")
    @Nullable
    public abstract String label();

    @NonNull
    public static TypeAdapter<File> typeAdapter(Gson gson) {
        return new AutoValue_File.GsonTypeAdapter(gson);
    }

    public static File create(String path, String label) {
        return new AutoValue_File(path, label);
    }
}
