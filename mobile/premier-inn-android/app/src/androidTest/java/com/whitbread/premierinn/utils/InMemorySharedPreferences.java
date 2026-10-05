package com.whitbread.premierinn.utils;

import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class InMemorySharedPreferences implements SharedPreferences, SharedPreferences.Editor {

    private HashMap<String, Object> values = new HashMap<>();
    private HashMap<String, Object> tempValues = new HashMap<>();

    @Override
    public Editor edit() {
        return this;
    }

    @Override
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    @Override
    public Map<String, ?> getAll() {
        return new HashMap<>(values);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (values.containsKey(key)) {
            return ((Boolean) values.get(key)).booleanValue();
        }
        return defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        if (values.containsKey(key)) {
            return ((Float) values.get(key)).floatValue();
        }
        return defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (values.containsKey(key)) {
            return ((Integer) values.get(key)).intValue();
        }
        return defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        if (values.containsKey(key)) {
            return ((Long) values.get(key)).longValue();
        }
        return defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        if (values.containsKey(key)) {
            return (String) values.get(key);
        }
        return defValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (values.containsKey(key)) {
            return (Set<String>) values.get(key);
        }
        return defValues;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Editor putBoolean(String key, boolean value) {
        tempValues.put(key, Boolean.valueOf(value));
        return this;
    }

    @Override
    public Editor putFloat(String key, float value) {
        tempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putInt(String key, int value) {
        tempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putLong(String key, long value) {
        tempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putString(String key, String value) {
        tempValues.put(key, value);
        return this;
    }

    @Override
    public Editor putStringSet(String key, Set<String> values) {
        tempValues.put(key, values);
        return this;
    }

    @Override
    public Editor remove(String key) {
        tempValues.remove(key);
        return this;
    }

    @Override
    public Editor clear() {
        tempValues.clear();
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean commit() {
        values = (HashMap<String, Object>) tempValues.clone();
        return true;
    }

    @Override
    public void apply() {
        commit();
    }
}
