package com.whitbread.premierinn.common.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.whitbread.premierinn.data.common.AppPackageDetails;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class VariantModuleTest {

    @Mock Context context;
    @Mock AppPackageDetails appPackageDetails;
    @Mock ContentManagedResourceRepository contentManagedResourceRepository;
    @Mock IsFeatureOn isFeatureOn;
    @Mock SharedPreferences preferences;

    @Test
    public void provideConfiguration() {
        assertNotNull(VariantModule.INSTANCE.provideConfiguration(isFeatureOn, preferences));
    }

    @Test
    public void provideOkHttpClient() {
        assertNotNull(VariantModule.INSTANCE.provideOkHttpClient(context, appPackageDetails, contentManagedResourceRepository));
    }
}