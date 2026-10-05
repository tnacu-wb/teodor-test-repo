package com.whitbread.premierinn.debugmenu;

import static com.whitbread.premierinn.api.Urls.GRAPHQL_DEMO;
import static com.whitbread.premierinn.api.Urls.GRAPHQL_DIT;
import static com.whitbread.premierinn.api.Urls.GRAPHQL_PERF;
import static com.whitbread.premierinn.api.Urls.GRAPHQL_PRE_PROD;
import static com.whitbread.premierinn.api.Urls.GRAPHQL_SIT;
import static com.whitbread.premierinn.api.Urls.GRAPHQL_UAT;
import static com.whitbread.premierinn.api.Urls.UAT_MICRO_SERVICE_URL;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.HULK;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.LIVE;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.QA;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.STAGING;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.WANDA;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.AppConfiguration;
import com.whitbread.premierinn.common.activity.BaseActivity;
import com.whitbread.premierinn.databinding.ActivityDebugMenuBinding;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@SuppressLint("NonConstantResourceId")
public class DebugMenuActivity extends BaseActivity<ActivityDebugMenuBinding> {
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    @Inject AppConfiguration configuration;

    @NonNull
    @Override
    protected ActivityDebugMenuBinding inflateBinding(@NonNull LayoutInflater inflater) {
        return ActivityDebugMenuBinding.inflate(inflater);
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(binding.toolbar);
        setState(configuration);

        binding.globalApiRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.stagingRadioBtn:
                    configuration.setEnvironment(STAGING);
                    enableStagingInputs(true);
                    binding.sslPinningSwitch.setEnabled(false);
                    break;
                case R.id.liveRadioBtn:
                    configuration.setEnvironment(LIVE);
                    enableStagingInputs(false);
                    binding.sslPinningSwitch.setEnabled(true);
                    break;
                case R.id.qaRadioBtn:
                    configuration.setEnvironment(QA);
                    enableStagingInputs(false);
                    binding.sslPinningSwitch.setEnabled(false);
                    break;
                case R.id.wandaRadioBtn:
                    configuration.setEnvironment(WANDA);
                    enableStagingInputs(false);
                    binding.sslPinningSwitch.setEnabled(true);
                    break;
                case R.id.hulkRadioBtn:
                    configuration.setEnvironment(HULK);
                    enableStagingInputs(false);
                    binding.sslPinningSwitch.setEnabled(true);
                    break;
                default:
            }
            binding.domainUrlEditText.setText(configuration.getMicroServicesUrl());
            binding.graphQlDomainUrlEditText.setText(configuration.getGraphQLUrl());
        });

        binding.graphQlRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            binding.graphQlDomainUrlEditText.setEnabled(false);
            switch (checkedId) {
                case R.id.graphQLDitRadioBtn:
                    binding.graphQlDomainUrlEditText.setText(GRAPHQL_DIT);
                    break;
                case R.id.graphQLSitRadioBtn:
                    binding.graphQlDomainUrlEditText.setText(GRAPHQL_SIT);
                    break;
                case R.id.graphQLDemoRadioBtn:
                    binding.graphQlDomainUrlEditText.setText(GRAPHQL_DEMO);
                    break;
                case R.id.graphQLPreProdRadioBtn:
                    binding.graphQlDomainUrlEditText.setText(GRAPHQL_PRE_PROD);
                    break;
                case R.id.graphQLPerfRadioBtn:
                    binding.graphQlDomainUrlEditText.setText(GRAPHQL_PERF);
                    break;
                case R.id.graphQLUATRadioBtn:
                    binding.graphQlDomainUrlEditText.setText(GRAPHQL_UAT);
                    break;
                case R.id.graphQlManualRadioBtn:
                    binding.graphQlDomainUrlEditText.setEnabled(true);
                    break;
                default:
            }
        });
        binding.sslPinningSwitch.setOnCheckedChangeListener((button, enabled) -> {
            if (configuration.isSSLPinningEnabled() != enabled) {
                configuration.setSSLPinningEnabled(enabled);
            }
        });

        binding.cacheSwitch.setOnCheckedChangeListener((button, enabled) -> {
            if (configuration.isCacheEnabled() != enabled) {
                configuration.setCacheEnabled(enabled);
            }
        });

        binding.apolloSwitch.setOnCheckedChangeListener((button, enabled) -> {
            if (configuration.isApolloEnabled() != enabled) {
                configuration.setApolloEnabled(enabled);
            }
        });

        binding.employeeOfferSwitch.setOnCheckedChangeListener((button, enabled) -> {
            if (configuration.isEmployeeOfferEnabled() != enabled) {
                configuration.setEmployeeOfferEnabled(enabled);
            }
        });
    }

    private void setState(AppConfiguration configuration) {
        switch (configuration.getEnvironment()) {
            case STAGING:
                binding.stagingRadioBtn.setChecked(true);
                break;
            case LIVE:
                binding.liveRadioBtn.setChecked(true);
                break;
            case QA:
                binding.qaRadioBtn.setChecked(true);
                break;
            case WANDA:
                binding.wandaRadioBtn.setChecked(true);
                break;
            case HULK:
                binding.hulkRadioBtn.setChecked(true);
                break;
            default:
        }


        if (configuration.getGraphQLUrl().equals(GRAPHQL_DIT)) {
            binding.graphQLDitRadioBtn.setChecked(true);
        } else if (configuration.getGraphQLUrl().equals(GRAPHQL_SIT)) {
            binding.graphQLSitRadioBtn.setChecked(true);
        } else if (configuration.getGraphQLUrl().equals(GRAPHQL_DEMO)) {
            binding.graphQLDemoRadioBtn.setChecked(true);
        } else if (configuration.getGraphQLUrl().equals(GRAPHQL_PRE_PROD)) {
            binding.graphQLPreProdRadioBtn.setChecked(true);
        }  else if (configuration.getGraphQLUrl().equals(GRAPHQL_PERF)) {
            binding.graphQLPerfRadioBtn.setChecked(true);
        } else if (configuration.getGraphQLUrl().equals(GRAPHQL_UAT)) {
            binding.graphQLUATRadioBtn.setChecked(true);
        } else {
            binding.graphQlManualRadioBtn.setChecked(true);
        }

        binding.domainUrlEditText.setText(configuration.getMicroServicesUrl());
        binding.domainUrlEditText.setEnabled(!configuration.isLive()
                        && !configuration.isPreLive());

        binding.graphQlDomainUrlEditText.setText(configuration.getGraphQLUrl());
        binding.graphQlDomainUrlEditText.setEnabled(binding.graphQlManualRadioBtn.isChecked()
                && !configuration.isLive()
                && !configuration.isPreLive());

        binding.sslPinningSwitch.setEnabled(LIVE.equals(configuration.getEnvironment()));
        binding.sslPinningSwitch.setChecked(configuration.isSSLPinningEnabled());

        binding.cacheSwitch.setChecked(configuration.isCacheEnabled());
        binding.employeeOfferSwitch.setChecked(configuration.isEmployeeOfferEnabled());
        binding.apolloSwitch.setChecked(configuration.isApolloEnabled());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_debug_menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        String selectedUrl = binding.domainUrlEditText.getText().toString();
        String selectedGQLUrl = binding.graphQlDomainUrlEditText.getText().toString();
        if (Patterns.WEB_URL.matcher(selectedUrl).matches()
                && (selectedUrl.contains(HTTP) || selectedUrl.contains(HTTPS))
                && Patterns.WEB_URL.matcher(selectedGQLUrl).matches()
                && (selectedGQLUrl.contains(HTTP) || selectedGQLUrl.contains(HTTPS))) {
            super.onBackPressed();
            configuration.setMicroServicesURL(selectedUrl);
            if (STAGING.equals(configuration.getEnvironment())) {
                configuration.setSnowdropServicesURL(UAT_MICRO_SERVICE_URL);
            }
            configuration.setGraphQlURL(selectedGQLUrl);
            ProcessPhoenix.triggerRebirth(this);
        } else {
            Toast.makeText(this, "The custom url you entered is not valid, please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void enableStagingInputs(boolean value) {
        binding.graphQLDitRadioBtn.setEnabled(value);
        binding.graphQLSitRadioBtn.setEnabled(value);
        binding.graphQLDemoRadioBtn.setEnabled(value);
        binding.graphQLPreProdRadioBtn.setEnabled(value);
        binding.graphQLPerfRadioBtn.setEnabled(value);
        binding.graphQLUATRadioBtn.setEnabled(value);
        binding.graphQlManualRadioBtn.setEnabled(value);
        binding.domainUrlEditText.setEnabled(value
                && !configuration.isLive()
                && !configuration.isPreLive());
        binding.graphQlDomainUrlEditText.setEnabled(binding.graphQlManualRadioBtn.isChecked()
                && !configuration.isLive()
                && !configuration.isPreLive());
    }
}