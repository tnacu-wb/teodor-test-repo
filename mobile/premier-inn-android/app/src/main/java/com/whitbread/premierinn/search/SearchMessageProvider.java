package com.whitbread.premierinn.search;

import android.content.Context;
import androidx.annotation.NonNull;
import com.whitbread.premierinn.R;
import javax.inject.Inject;

public class SearchMessageProvider {

    private final Context context;

    @Inject
    public SearchMessageProvider(@NonNull Context context) {
        this.context = context;
    }

    String getMyLocationSearchText() {
        return context.getString(R.string.search_results_my_location_title);
    }

    String getNoSearchResultsMessage(String query) {
        return context.getString(R.string.search_no_results_text, query);
    }

    String getSearchResultsErrorMessage() {
        return context.getString(R.string.search_results_error);
    }
}
