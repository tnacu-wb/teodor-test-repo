package com.whitbread.premierinn.api.response.search;


import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@AutoValue
public abstract class SearchItemRoot implements Parcelable {

    @SerializedName("suggestions") public abstract List<SearchItemInput> searchItems();


    public static SearchItemRoot create(List<SearchItemInput> searchItems) {
        return new AutoValue_SearchItemRoot(searchItems);
    }

    @NonNull
    public static TypeAdapter<SearchItemRoot> typeAdapter(Gson gson) {
        return new AutoValue_SearchItemRoot.GsonTypeAdapter(gson);
    }

}
