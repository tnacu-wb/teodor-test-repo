package com.whitbread.premierinn.api.response;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.whitbread.premierinn.common.retrofitConfiguration.GsonAdapterFactory;
import com.whitbread.premierinn.common.utils.FileUtils;
import com.whitbread.premierinn.data.GsonFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InstanceFactory {

    public static final Gson GSON = GsonFactory.create(GsonAdapterFactory.create());

    @SuppressWarnings("unchecked")
    public static <T> T create(Class clazz, String filePath) {

        String jsonStringSource = FileUtils.loadFileFromResource(filePath);
        try {
            Method autoValueTypeAdapterMethod = clazz.getMethod("typeAdapter", Gson.class);
            TypeAdapter<T> typeAdapter = (TypeAdapter) autoValueTypeAdapterMethod.invoke(null, GSON);
            return typeAdapter.fromJson(jsonStringSource);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
            return (T) GSON.fromJson(jsonStringSource, clazz);
        }
    }
}
