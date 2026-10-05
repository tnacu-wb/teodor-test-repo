package com.whitbread.premierinn.api.response.availability;


import androidx.annotation.Nullable;

import com.whitbread.premierinn.api.response.InstanceFactory;

import java.util.List;

public class HotelImageFactory extends InstanceFactory {
    public static HotelImage create(@Nullable String path, @Nullable List<String> tags) {
        return new AutoValue_HotelImage(path, tags);
    }
}