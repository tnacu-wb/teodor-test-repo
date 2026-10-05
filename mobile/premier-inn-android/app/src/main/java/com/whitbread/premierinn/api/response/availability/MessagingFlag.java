package com.whitbread.premierinn.api.response.availability;

import android.graphics.Color;
import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@AutoValue
public abstract class MessagingFlag implements Parcelable {

    private static final String FLAG_TEXT = "flagText";
    private static final String FLAG_COLOR = "flagColor";

    @NonNull
    public static TypeAdapter<MessagingFlag> typeAdapter(Gson gson) {
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, MessagingFlag value) {
                // TODO ?
            }

            @Override
            public MessagingFlag read(JsonReader jsonReader) throws IOException {
                jsonReader.beginObject();
                String flagText = null;
                int flagColor = 0;
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    if (jsonReader.peek() == JsonToken.NULL) {
                        jsonReader.skipValue();
                        continue;
                    }
                    switch (name) {
                        case FLAG_TEXT: {
                            flagText = gson.getAdapter(String.class).read(jsonReader);
                            if (flagText.isEmpty()) {
                                flagText = null;
                            }
                            break;
                        }
                        case FLAG_COLOR: {
                            String code = gson.getAdapter(String.class).read(jsonReader);
                            if (code.length() > 0) {
                                flagColor = Color.parseColor("#" + code);
                            }
                            break;
                        }
                        default: {
                            jsonReader.skipValue();
                        }
                    }
                }
                jsonReader.endObject();
                return new AutoValue_MessagingFlag(flagText, flagColor);
            }
        };
    }

    @SerializedName(FLAG_TEXT)
    @Nullable
    public abstract String flagText();

    @SerializedName(FLAG_COLOR)
    @ColorInt
    public abstract int flagColor();
}
