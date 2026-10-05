package com.whitbread.premierinn.shortcutlocationtrampoline;

import static com.whitbread.premierinn.searchresults.SearchResultsActivityKt.createSearchResultIntent;

import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.searchresults.SearchResultsInput;

import io.reactivex.Observable;

public class ShortcutLocationTrampolineViewContainer extends ViewContainer implements ShortcutLocationTrampolinePresenter.View {

    private final RxPermissions rxPermissions;

    public ShortcutLocationTrampolineViewContainer(@NonNull BaseActivity activity) {
        super(activity);
        this.rxPermissions = new RxPermissions(activity);
    }

    @Override
    public Observable<Boolean> onLocationPermissionRequested() {
        return rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void startSearchResultsActivity(@NonNull SearchResultsInput searchResultsInput,
                                           boolean useGraphQlAvailabilities,
                                           String country,
                                           String language) {
        getActivity().startActivity(createSearchResultIntent(getActivity(), searchResultsInput,
                        null, null, false, null, country, language)
                        );
        getActivity().finish();
    }

    @Override
    public void closeScreenWithError() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.shortcut_error_location_permission), Toast.LENGTH_LONG)
                .show();
        getActivity().finish();
    }
}
