package com.whitbread.premierinn.gdpr;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import androidx.annotation.NonNull;

import com.jakewharton.rxbinding3.view.RxView;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.common.mvp.ViewContainer;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.databinding.ActivityGdprBinding;

import io.reactivex.Observable;
import kotlin.Unit;

public class GdprViewContainer extends ViewContainer implements GdprPresenter.View {

    private final ActivityGdprBinding binding;

    public GdprViewContainer(@NonNull BaseActivity activity, ActivityGdprBinding binding) {
        super(activity);
        this.binding = binding;
        binding.gdprInterstitialMessage.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public Observable<Unit> changesAccepted() {
        return RxView.clicks(binding.gdprAcceptButton);
    }

    @Override
    public Observable<Unit> howToUseDataClicked() {
        return RxView.clicks(binding.gdprHowWeUseDataButton);
    }

    @Override
    public Observable<Unit> privacyPolicyClicked() {
        return RxView.clicks(binding.gdprPrivacyPolicyButton);
    }

    @Override
    public Context getContext() {
        return getActivity().getBaseContext();
    }

    @Override
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void goToWebLink(@NonNull String webUrl) {
        getActivity().startActivity(IntentUtils.createWebLinkIntent(webUrl));
    }

    @Override
    public void openHowWeUseYourData() {
        getActivity().startActivityForResult(GdprDataUseActivity.createIntent(getActivity()), GdprDataUseActivity.REQUEST_CODE);
    }

    @Override
    public void showInterstitialMessage(String messageContent) {
        binding.gdprInterstitialMessage.setText(messageContent);
        binding.gdprProgress.setVisibility(View.GONE);
        showContent();
    }

    private void showContent() {
        // TODO - Set visibility via ConstraintLayout Group instead (could not get this to work)
        binding.privacyChangesTitle.setVisibility(View.VISIBLE);
        binding.interstitialImage.setVisibility(View.VISIBLE);
        binding.gdprHowWeUseDataButton.setVisibility(View.VISIBLE);
        binding.gdprPrivacyPolicyButton.setVisibility(View.VISIBLE);
        binding.gdprInterstitialMessage.setVisibility(View.VISIBLE);
        binding.gdprAcceptButton.setVisibility(View.VISIBLE);
    }
}