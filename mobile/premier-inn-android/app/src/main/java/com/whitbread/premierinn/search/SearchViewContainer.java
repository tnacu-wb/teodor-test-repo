package com.whitbread.premierinn.search;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxrelay2.ReplayRelay;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.whitbread.premierinn.api.response.search.SearchItemInput;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.databinding.ActivitySearchBinding;
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem;
import com.whitbread.premierinn.search.adapter.SearchAdapter;
import com.whitbread.premierinn.search.adapter.adapteritem.SearchAdapterItem;

import java.util.List;

import io.reactivex.Observable;

public class SearchViewContainer implements SearchPresenter.View {

    private SearchAdapter searchAdapter;
    private final RxPermissions rxPermissions;
    private ReplayRelay<String> searchInputProxy = ReplayRelay.create();
    private ActivitySearchBinding binding;
    private  BaseActivity activity;


    public SearchViewContainer(@NonNull BaseActivity activity, ActivitySearchBinding binding) {
        this.activity = activity;
        this.binding = binding;

        activity.setKeyboardVisibilityListener(binding.llSearchRoot);

        searchAdapter = new SearchAdapter();
        rxPermissions = new RxPermissions(activity);
        binding.searchSearchList.setLayoutManager(new LinearLayoutManager(activity));
        binding.searchSearchList.setAdapter(searchAdapter);

        RxTextView.textChanges(binding.etLayoutSearch)
                .map(CharSequence::toString)
                .subscribe(searchInputProxy);

        binding.searchClear.setOnClickListener(__ -> binding.etLayoutSearch.setText(null));
        binding.ivSearchBackArrow.setOnClickListener(__ ->  activity.onBackPressed());
    }

    @Override
    public void displayResults(@NonNull List<SearchAdapterItem> searchAdapterItems) {
        searchAdapter.submitList(searchAdapterItems);
        binding.searchSearchList.setVisibility(View.VISIBLE);
        binding.llSearchNoResults.setVisibility(View.GONE);
    }

    @Override
    public void showNoResultsFoundMessage(@NonNull String message) {
        binding.llSearchNoResults.setVisibility(View.VISIBLE);
        binding.noSearchResultsLine1.setText(message);
    }

    @Override
    public void showErrorOccurred(@NonNull String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Observable<String> onSearchInput() {
        return searchInputProxy;
    }

    @Override
    public Observable<Object> onRecentSearchClearClick() {
        return searchAdapter.onClearClicked();
    }

    @Override
    public Observable<Object> onSearchLocationClickPermissionsAccepted() {
        return searchAdapter.onSearchLocationClick()
                .flatMap(__ -> rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                .filter(granted -> granted)
                .map(__ -> new Object());
    }

    @Override
    public Observable<SearchSuggetionItem> onSearchItemClick() {
        return searchAdapter.onSearchItemClick();
    }

    @Override
    public void showLoading(boolean show) {
        binding.searchProgressLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showClearSearch(boolean show) {
        if (show) {
            binding.searchClear.setVisibility(View.VISIBLE);
            binding.searchClear.setEnabled(true);
        } else {
            binding.searchClear.setVisibility(View.INVISIBLE);
            binding.searchClear.setEnabled(false);
        }
    }

    @Override
    public void returnResult(@NonNull SearchItemInput searchItem) {
        Intent intent = new Intent();
        intent.putExtra(SearchActivity.SELECTED_SEARCH_ITEM, searchItem);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}