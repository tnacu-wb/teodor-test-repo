package com.whitbread.premierinn.common.dagger;

import static com.whitbread.premierinn.common.dagger.StagingConfiguration.PREF_ENVIRONMENT;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.PREF_UAT_GQL_URL;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.PREF_UAT_MS_SNOWDROP_URL;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.PREF_USE_APOLLO;
import static com.whitbread.premierinn.common.dagger.StagingConfiguration.STAGING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.common.AppConfiguration;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StagingConfigurationTest {

    AppConfiguration configuration;
    @Mock SharedPreferences prefs;
    @Mock IsFeatureOn isFeatureOn;

    @Before
    public void setUp() throws Exception {
        configuration = new StagingConfiguration(prefs, isFeatureOn);
        when(prefs.getString(PREF_ENVIRONMENT, STAGING)).thenReturn(STAGING);
        when(prefs.getString(PREF_UAT_MS_SNOWDROP_URL, Urls.UAT_MICRO_SERVICE_URL)).thenReturn(Urls.UAT_MICRO_SERVICE_URL);
        when(prefs.getString(PREF_UAT_GQL_URL, Urls.GRAPHQL_UAT)).thenReturn(Urls.GRAPHQL_UAT);
    }

    @Test
    public void getSnowdropUrl() throws Exception {
        assertThat(configuration.getSnowdropUrl(), is("https://api-uat.whitbread.co.uk"));
    }

    @Test
    public void getDefaultMicroServicesUrl() throws Exception {
        assertThat(configuration.getMicroServicesUrl(), is("https://restapi.uat.premierinn.digital"));
    }

    @Test
    public void getApolloSwitch() throws Exception {
        when(prefs.getBoolean(PREF_USE_APOLLO, false)).thenReturn(true);

        assertTrue(configuration.isApolloEnabled());
    }

}