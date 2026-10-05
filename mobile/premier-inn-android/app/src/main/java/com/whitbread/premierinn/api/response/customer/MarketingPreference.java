package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@AutoValue
public abstract class MarketingPreference implements Parcelable {

    public static final String DEFAULT_VALUE = "1";

    /**
     *
     *  {
            “regions”: [
                {
                     “id”: “1”,
                     “description”: “UK & Ireland”,
                     “active”: true
                },
                {
                     “id”: “2",
                     “description”: “Dubai”,
                     “active”: true
                },
                {
                     “id”: “3”,
                     “description”: “India”,
                     “active”: true
                },
                {
                     “id”: “4",
                     “description”: “hub by Premier Inn”,
                     “active”: true
                },
                {
                     “id”: “5”,
                     “description”: “Germany”,
                     “active”: true
                }
            ]
        }
     */


    @SerializedName("wantRestaurantNews")
    public abstract boolean wantRestaurantNews();

    @SerializedName("regionPreferences")
    public abstract List<String> regionPreferences();

    public static Builder builder() {
        return new AutoValue_MarketingPreference.Builder()
                .wantRestaurantNews(false);
    }

    public static TypeAdapter<MarketingPreference> typeAdapter(Gson gson) {
        return new AutoValue_MarketingPreference.GsonTypeAdapter(gson);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder wantRestaurantNews(boolean wantRestaurantNews);

        public abstract Builder regionPreferences(List<String> regionPreferences);

        public abstract MarketingPreference build();
    }
}
