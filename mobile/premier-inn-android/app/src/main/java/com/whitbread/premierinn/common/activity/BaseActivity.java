package com.whitbread.premierinn.common.activity;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewbinding.ViewBinding;

import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.ActivityResultMessage;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.contentSquare.CSQMaskingRegistryHelper;
import com.whitbread.premierinn.common.service.OfflineDetectorService;
import com.whitbread.premierinn.common.span.CustomTypefaceSpan;
import com.whitbread.premierinn.common.utils.IntentUtils;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.common.utils.ViewUtils;
import com.whitbread.premierinn.common.view.InternetConnectionInfoView;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {

    private final PublishRelay<ActivityResultMessage> activityResultIntentRelay = PublishRelay.create();
    @Inject
    public TrackingAnalytics analytics;

    private CSQMaskingRegistryHelper cSQMaskingRegistryHelper;
    protected DialogMaker dialogMaker;
    @Inject
    public OfflineDetectorService offlineDetectorService;
    private String componentKey;
    private Disposable internetAvailabilityDisposable;
    private InternetConnectionInfoView internetInfoView;
    private boolean shouldTrackOnResume = false;

    protected T binding;

    @NonNull
    protected abstract T inflateBinding(@NonNull LayoutInflater inflater);

    protected abstract Toolbar getToolbar();

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        cSQMaskingRegistryHelper.maskRegisteredViews(this);
    }

    /**
     * This method is used only for the bottom navigation
     *
     * @param binding is the binding view
     * @param rootView is the binding and the bottomnavigation
     */
    protected void setBinding(T binding, View rootView) {
        this.binding = binding;
        setContentView(rootView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cSQMaskingRegistryHelper = new CSQMaskingRegistryHelper();
        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());

        dialogMaker = new DialogMaker(new DialogMaker.AlertDialogBuilderFactory());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Signal that we're returning from back stack
        shouldTrackOnResume = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        analytics.activityOnResume(this);

        // Track destination screen when returning from back navigation
        if (shouldTrackOnResume) {
            analytics.trackActivityBackNavigation(this);
            shouldTrackOnResume = false;
        }

        if (attachInternetInfoView()) {
            setupInternetMessageView();
        }
    }

    @Override
    protected void onPause() {
        if (internetAvailabilityDisposable != null && !internetAvailabilityDisposable.isDisposed()) {
            internetAvailabilityDisposable.dispose();
        }
        super.onPause();
        analytics.activityOnPause();
    }

    private void setupInternetMessageView() {
        if (internetInfoView == null) {
            internetInfoView = new InternetConnectionInfoView(getApplicationContext());
            addViewBelowToolbar(internetInfoView);
        }

        internetAvailabilityDisposable = offlineDetectorService.getNetworkObservable()
                .subscribe(internetAvailable -> {
                    if (!internetAvailable) {
                        analytics.track(ScreenState.NO_INTERNET, TrackingAnalyticsUtils.getStateType(BaseActivity.this
                                .getClass().getCanonicalName()));
                        internetInfoView.showConnectionSuccessful(false);
                    }
                    if (internetAvailable && internetInfoView.getVisibility() == View.VISIBLE) {
                        internetInfoView.showConnectionSuccessful(true);
                    }
                });
    }

    /**
     * In case the child activity doesn't have a toolbar and we want to add the InternetInfoView to a different place
     * that is not the top of the page, this method should be overridden and return the view where u want it to be added
     * <p>
     * It will be added at the top of that view
     */
    protected ViewGroup getParentViewForInternetInfoBar() {
        return null;
    }

    protected boolean attachInternetInfoView() {
        return true;
    }

    private void addViewBelowToolbar(@NonNull View childView) {
        Toolbar toolbar = getToolbar();
        if (toolbar != null && toolbar.getParent() != null) {
            ViewGroup toolbarParent = (ViewGroup) toolbar.getParent();

            if (toolbarParent instanceof LinearLayout) {
                int toolbarPosition = toolbarParent.indexOfChild(toolbar);
                toolbarParent.addView(childView, toolbarPosition + 1);

            } else if (toolbarParent instanceof RelativeLayout) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, toolbar.getId());
                childView.setLayoutParams(layoutParams);
                toolbarParent.addView(childView);
            } else if (toolbarParent instanceof ConstraintLayout) {
                //View is currently not being constraint to toolbar
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.connect(childView.getId(), ConstraintSet.TOP, toolbar.getId(), ConstraintSet.BOTTOM);
                constraintSet.applyTo((ConstraintLayout) toolbarParent);
                toolbarParent.addView(childView);
            }
        } else if (getParentViewForInternetInfoBar() != null) {
            ViewGroup referenceView = getParentViewForInternetInfoBar();
            if (referenceView instanceof LinearLayout) {
                referenceView.addView(childView, 0);

            } else if (referenceView instanceof RelativeLayout) {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                childView.setLayoutParams(layoutParams);
                referenceView.addView(childView);

            } else if (referenceView instanceof ConstraintLayout) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.connect(childView.getId(), ConstraintSet.TOP, referenceView.getId(), ConstraintSet.TOP);
                constraintSet.applyTo((ConstraintLayout) referenceView);
                referenceView.addView(childView);
            }
        } else {
            ViewGroup contentChild = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            contentChild.addView(childView, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        activityResultIntentObservable().accept(ActivityResultMessage.create(requestCode, resultCode,
                intent, getClass()));
    }

    @Deprecated
    public PublishRelay<ActivityResultMessage> activityResultIntentObservable() {
        return activityResultIntentRelay;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.toolbar_privacy_policy_icon:
                startActivity(IntentUtils.createWebLinkIntent(getString(R.string.privacy_policy_web_url)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void setToolbarAction(String text, @FontRes int fontRes, CallBackToolbar callBackToolbar) {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            if (getSupportActionBar() == null) {
                setSupportActionBar(toolbar);
            }

            SpannableString s = new SpannableString(text);
            s.setSpan(new CustomTypefaceSpan("", ResourcesCompat.getFont(this.getApplicationContext(), fontRes)), 0, s.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            callBackToolbar.apply(s);
        } else {
            throw new ExceptionInInitializerError("You must set your toolbar with an id toolbar before using this method");
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void setToolbar(@NonNull String title, boolean showHomeAsUp) {
        setToolbarAction(title, R.font.proxima_nova_semibold,
                spannableString -> getSupportActionBar().setTitle(spannableString));
        getSupportActionBar().setDisplayHomeAsUpEnabled(showHomeAsUp);
    }

    @SuppressWarnings("ConstantConditions")
    public void setToolbar(@NonNull String title, boolean showHomeAsUp, @NonNull String subtitle) {
        setToolbar(title, showHomeAsUp);
        setToolbarAction(subtitle, R.font.proxima_nova_regular,
                spannableString -> getSupportActionBar().setSubtitle(spannableString));
    }

    public void showToast(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showDialog(@NonNull String title, @NonNull String message) {
        new AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, i) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private interface CallBackToolbar {
        void apply(SpannableString spannableString);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setKeyboardVisibilityListener(@NonNull View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                ViewUtils.hideKeyboard(this);
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setKeyboardVisibilityListener(innerView);
            }
        }
    }
}
