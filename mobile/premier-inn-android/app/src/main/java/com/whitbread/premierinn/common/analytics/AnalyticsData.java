package com.whitbread.premierinn.common.analytics;

import com.contentsquare.android.api.model.CustomVar;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public interface AnalyticsData {
    Map<String, String> contextData();

    default List<CustomVar> customCSQVars() {
        return Collections.emptyList();
    }
}
