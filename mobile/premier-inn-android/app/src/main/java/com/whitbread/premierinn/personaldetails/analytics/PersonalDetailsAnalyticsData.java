package com.whitbread.premierinn.personaldetails.analytics;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.customer.ContactDetail;
import com.whitbread.premierinn.common.analytics.AnalyticsData;
import com.whitbread.premierinn.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class PersonalDetailsAnalyticsData implements AnalyticsData {

    private static final String ACCOUNT_AMENDED_ACTION = "And: My Account Amended";
    private static final String KEY_ACCOUNT_CHANGED_DATA = "analyticsData.accountChanged";

    private static final String ADDRESS_FIELD = "address";
    private static final String NATIONALITY_FIELD = "nationality";
    private static final String PASSPORT_FIELD = "passport";
    private static final String CAR_REG_FIELD = "car reg";
    private static final String MOBILE_FIELD = "mobile";
    private static final String EMAIL_FIELD = "email";
    private static final String FIRST_NAME_FIELD = "firstname";
    private static final String LAST_NAME_FIELD = "lastname";

    @Nullable
    public abstract ContactDetail existingContactDetails();
    public abstract ContactDetail updatedContactDetails();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract PersonalDetailsAnalyticsData.Builder existingContactDetails(ContactDetail existingContactDetails);
        public abstract PersonalDetailsAnalyticsData.Builder updatedContactDetails(ContactDetail updatedContactDetails);
        public abstract PersonalDetailsAnalyticsData build();
    }

    public static PersonalDetailsAnalyticsData.Builder builder() {
        return new AutoValue_PersonalDetailsAnalyticsData.Builder();
    }

    @Override
    public Map<String, String> contextData() {
        Map<String, String> actionMap = new HashMap<>();

        actionMap.put(KEY_ACCOUNT_CHANGED_DATA, StringUtils.toCommaSeparatedString(fieldsChanged()));
        actionMap.put(ACCOUNT_AMENDED_ACTION, null);
        return actionMap;
    }

    private List<String> fieldsChanged() {
        List<String> changedFields = new ArrayList<>();
        if (!updatedContactDetails().address().equals(existingContactDetails().address())) {
            changedFields.add(ADDRESS_FIELD);
        }
        if (!updatedContactDetails().nationality().equals(existingContactDetails().nationality())) {
            changedFields.add(NATIONALITY_FIELD);
        }
        if (!updatedContactDetails().passport().equals(existingContactDetails().passport())) {
            changedFields.add(PASSPORT_FIELD);
        }
        if (!updatedContactDetails().carRegistration().equals(existingContactDetails().carRegistration())) {
            changedFields.add(CAR_REG_FIELD);
        }
        if (!updatedContactDetails().mobile().equals(existingContactDetails().mobile())) {
            changedFields.add(MOBILE_FIELD);
        }
        if (!updatedContactDetails().email().equals(existingContactDetails().email())) {
            changedFields.add(EMAIL_FIELD);
        }
        if (!updatedContactDetails().firstName().equals(existingContactDetails().firstName())) {
            changedFields.add(FIRST_NAME_FIELD);
        }
        if (!updatedContactDetails().lastName().equals(existingContactDetails().lastName())) {
            changedFields.add(LAST_NAME_FIELD);
        }
        return changedFields;
    }
}
