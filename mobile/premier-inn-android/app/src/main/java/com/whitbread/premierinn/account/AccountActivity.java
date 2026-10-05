package com.whitbread.premierinn.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.changepassword.ChangePasswordActivity;
import com.whitbread.premierinn.common.bottomnavigation.BottomNavigationActivity;
import com.whitbread.premierinn.createaccount.CreateAccountActivity;
import com.whitbread.premierinn.databinding.ActivityMyAccountBinding;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.editpaymentmethods.EditPaymentMethodsActivity;
import com.whitbread.premierinn.gdpr.GdprDataUseActivity;
import com.whitbread.premierinn.login.LoginActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountActivity extends BottomNavigationActivity<ActivityMyAccountBinding> {

    public static final String EMPLOYEE_OFFER_ENABLED = "EMPLOYEE_OFFER_ENABLED";
    private static boolean isEmployeOfferEnabled;
    private final PublishRelay<Object> loginRelay = PublishRelay.create();
    private final PublishRelay<Object> refreshRelay = PublishRelay.create();
    private final PublishRelay<Object> createAccountRelay = PublishRelay.create();
    private PublishRelay<Object> changePasswordRelay = PublishRelay.create();
    private final PublishRelay<Object> saveCardRelay = PublishRelay.create();
    private final PublishRelay<Boolean> employeeOfferRelay = PublishRelay.create();
    private AccountViewContainer view;

    @Inject AccountPresenter presenter;
    @Inject IsFeatureOn isFeatureOn;

    public static void start(Context context) {
        Intent starter = new Intent(context, AccountActivity.class);
        context.startActivity(starter);
    }

    public static void startForEmployeeOffer(Context context, Boolean isEmployeeOfferFlow) {
        Intent starter = new Intent(context, AccountActivity.class);
        starter.putExtra(EMPLOYEE_OFFER_ENABLED, isEmployeeOfferFlow);

        isEmployeOfferEnabled = true;
        context.startActivity(starter);
    }

    @NonNull
    @Override
    protected ActivityMyAccountBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityMyAccountBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new AccountViewContainer(this, createAccountRelay, loginRelay, refreshRelay,
                changePasswordRelay, saveCardRelay, employeeOfferRelay, binding);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshRelay.accept(new Object());
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.detachView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isEmployeeOfferOnInFb = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_ALLOW_EMPLOYEE_OFFER);
        if (isEmployeOfferEnabled && isEmployeeOfferOnInFb) {
            presenter.saveEmployeeOfferSectionAndToggleState();
            isEmployeOfferEnabled = false;
            view.showEmployeeOffer();
            presenter.onAttachView(view);
        } else {
            presenter.attachView(view);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bottom Navigation View
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected int getSelectedMenuItem() {
        return R.id.bottom_navigation_account;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GdprDataUseActivity.REQUEST_CODE:
                    setResult(RESULT_OK);
                    finish();
                    break;
                case LoginActivity.ACTIVITY_RESULT_REQUEST_CODE:
                    loginRelay.accept(new Object());
                    break;
                case CreateAccountActivity.ACTIVITY_RESULT_REQUEST_CODE:
                    createAccountRelay.accept(new Object());
                    break;
                case ChangePasswordActivity.ACTIVITY_RESULT_REQUEST_CODE:
                    changePasswordRelay.accept(new Object());
                    break;
                case EditPaymentMethodsActivity.ACTIVITY_RESULT_REQUEST_CODE_SAVE_CARD:
                    saveCardRelay.accept(new Object());
                default:
            }
        }
    }
}
