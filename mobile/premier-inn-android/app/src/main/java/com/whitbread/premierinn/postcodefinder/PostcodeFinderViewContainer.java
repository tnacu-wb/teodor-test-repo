package com.whitbread.premierinn.postcodefinder;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.utils.ViewUtils;
import com.whitbread.premierinn.databinding.ActivityPostcodeFinderBinding;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.common.AddressShort;

import java.util.List;

import io.reactivex.Observable;
import kotlin.Unit;

public class PostcodeFinderViewContainer implements PostcodeFinderPresenter.View {

    private ActivityPostcodeFinderBinding binding;

    private PostcodeFinderActivity activity;
    private final PostcodeFinderAdapter adapter;

    PostcodeFinderViewContainer(
            @NonNull PostcodeFinderActivity activity, ActivityPostcodeFinderBinding binding) {
        this.activity = activity;
        this.binding = binding;

        adapter = new PostcodeFinderAdapter();
        binding.rvPostcodeFinder.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity, R.drawable.line_divider));
        binding.rvPostcodeFinder.addItemDecoration(dividerItemDecoration);

        setPostcodeInputCursorListener();
        setPostcodeInputActionListener();
        setKeyboardVisibilityListener();

        binding.postcodeErrorLabelManualAddress.setOnClickListener(__ -> onClickBackArrow());
        binding.ivPostcodeFinderBackArrow.setOnClickListener(__ -> onClickBackArrow());
    }

    void onClickBackArrow() {
        activity.onBackPressed();
        ViewUtils.hideKeyboard(activity);
    }

    @Override
    public void showResults(@NonNull List<AddressShort> postcodeAddresses) {
        binding.pbAddressLoading.setVisibility(View.GONE);
        binding.postcodeViewErrorWrapper.setVisibility(View.GONE);
        binding.llPostcodeFinderSearchResults.setVisibility(View.VISIBLE);
        adapter.setResults(postcodeAddresses);
        binding.etPostcodeFinderSearchInput.setCursorVisible(false);
    }

    @Override
    public void showNoResults() {
        binding.postcodeViewErrorWrapper.setVisibility(View.VISIBLE);
        binding.pbAddressLoading.setVisibility(View.GONE);
        binding.llPostcodeFinderSearchResults.setVisibility(View.GONE);
        binding.etPostcodeFinderSearchInput.setCursorVisible(false);
    }

    @Override
    public void showDefaultScreen() {
        binding.postcodeViewErrorWrapper.setVisibility(View.GONE);
        binding.pbAddressLoading.setVisibility(View.GONE);
        binding.llPostcodeFinderSearchResults.setVisibility(View.GONE);
        binding.etPostcodeFinderSearchInput.setCursorVisible(true);
        ViewUtils.showKeyboard(activity);
    }

    @Override
    public void showLoading() {
        binding.pbAddressLoading.setVisibility(View.VISIBLE);
        binding.postcodeViewErrorWrapper.setVisibility(View.GONE);
        binding.llPostcodeFinderSearchResults.setVisibility(View.GONE);
    }

    @Override
    public void displayPostcode(@NonNull String text) {
        binding.etPostcodeFinderSearchInput.setText(text);
        binding.etPostcodeFinderSearchInput.setSelection(text.length());
    }

    @Override
    public Observable<AddressShort> onAddressClick() {
        return adapter.onAddressClick();
    }

    @Override
    public Observable<String> onPostCodeEntered() {
        return RxTextView.textChanges(binding.etPostcodeFinderSearchInput)
                .map(CharSequence::toString);
    }

    @Override
    public Observable<Unit> onManualAddressClicked() {
        return RxView.clicks(binding.postcodeLabelManualAddress);
    }

    @Override
    public void selectAddress(@NonNull Address postcodeAddress) {
        Intent intent = new Intent();
        ParcelableAddress parcelableAddress = ParcelableMappersKt.toParcelable(postcodeAddress);
        intent.putExtra(PostcodeFinderActivity.SELECTED_POSTCODE_ADDRESS_KEY, parcelableAddress);
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
        ViewUtils.hideKeyboard(activity);
    }

    @Override
    public void showManualAddressInput() {
        onClickBackArrow();
    }

    @Override
    public void showSelectedAddressLoading() {
        binding.fullAddressLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSelectedAddressLoadingFailed() {
        binding.fullAddressLoading.setVisibility(View.GONE);
        Toast.makeText(
                activity,
                activity.getString(R.string.postcode_finder_loading_address_failed),
                Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPostcodeInputCursorListener() {
        binding.etPostcodeFinderSearchInput.setOnTouchListener((view, motionEvent) -> {
            binding.etPostcodeFinderSearchInput.setCursorVisible(true);
            return false;
        });
    }

    private void setPostcodeInputActionListener() {
        binding.etPostcodeFinderSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                ViewUtils.hideKeyboard(activity);
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setKeyboardVisibilityListener() {
        binding.rvPostcodeFinder.setOnTouchListener((view, motionEvent) -> {
            ViewUtils.hideKeyboard(activity);
            return false;
        });
    }
}
