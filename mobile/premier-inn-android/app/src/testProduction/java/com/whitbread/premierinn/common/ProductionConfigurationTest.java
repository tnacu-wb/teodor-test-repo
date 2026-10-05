package com.whitbread.premierinn.common;

import static com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_SECTION;
import static com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_EMPLOYEE_OFFER_TOGGLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import android.content.SharedPreferences;

import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProductionConfigurationTest {

    AppConfiguration configuration;

    @Mock IsFeatureOn isFeatureOn;
    @Mock SharedPreferences preferences;

    @Before
    public void setUp() throws Exception {
        configuration = new ProductionConfiguration(isFeatureOn, preferences);
    }

    @Test
    public void getMiddlewareUrl() {
        assertThat(configuration.getMicroServicesUrl(), is("https://restapi.premierinn.com"));
    }

    @Test
    public void getMicroServicesUrl() {
        assertThat(configuration.getMicroServicesUrl(), is("https://restapi.premierinn.com"));
    }

    @Test
    public void getCheckInUrl() {
        assertThat(configuration.getCheckInOnlineUrl(),
                is("https://secure.premierinn.com/en/prepareCheckinOnline.action?INTCMP=And_CIOL"));
    }

    @Test
    public void isCacheEnabled() throws Exception {
        assertThat(configuration.isCacheEnabled(), is(true));
    }

    @Test
    public void isSSLPinningEnabled() throws Exception {
        assertThat(configuration.isSSLPinningEnabled(), is(true));
    }

    @Test
    public void whenEmployeeOfferAndToogleIsEnabled_returnTrue() throws Exception {
        when(preferences.getBoolean(KEY_EMPLOYEE_OFFER_SECTION, false)).thenReturn(true);
        when(preferences.getBoolean(KEY_EMPLOYEE_OFFER_TOGGLE, false)).thenReturn(true);
        assertThat(configuration.isEmployeeOfferEnabled(), is(true));
    }

    @Test
    public void whenEmployeeOfferIsEnabledAndToggleDisabled_returnFalse() throws Exception {
        when(preferences.getBoolean(KEY_EMPLOYEE_OFFER_SECTION, false)).thenReturn(true);
        assertThat(configuration.isEmployeeOfferEnabled(), is(false));
    }

    @Test
    public void whenEmployeeOfferIsDisabled_returnFalse() throws Exception {
        assertThat(configuration.isEmployeeOfferEnabled(), is(false));
    }

    @Test
    public void isLive() throws Exception {
        assertThat(configuration.isLive(), is(true));
    }

    @Test
    public void getApolloSwitch() throws Exception {
        assertFalse(configuration.isApolloEnabled());
    }
}
