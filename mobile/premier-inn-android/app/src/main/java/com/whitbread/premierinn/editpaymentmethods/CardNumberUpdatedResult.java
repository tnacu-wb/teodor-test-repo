package com.whitbread.premierinn.editpaymentmethods;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.result.Result;

import java.util.Arrays;
import java.util.List;

import static com.whitbread.premierinn.common.forms.InputState.State.IDLE;
import static com.whitbread.premierinn.common.forms.InputState.State.INVISIBLE;

@AutoValue
public abstract class CardNumberUpdatedResult implements Result {

    @Nullable
    public abstract Boolean startDateRequired();
    @Nullable
    public abstract Boolean issueNumberRequired();

    public List<InputState> inputStates() {
        InputState startDateInputState = InputState.builder()
                .id(R.id.til_card_details_form_start_date)
                .state(startDateRequired() != null && startDateRequired() ? IDLE : INVISIBLE).build();

        InputState issueNumberInputState = InputState.builder()
                .id(R.id.til_card_details_form_issue_number)
                .state(issueNumberRequired() != null && issueNumberRequired() ? IDLE : INVISIBLE).build();

        return Arrays.asList(startDateInputState, issueNumberInputState);
    }

    public static CardNumberUpdatedResult.Builder builder() {
        return new AutoValue_CardNumberUpdatedResult.Builder();
    }

    public abstract CardNumberUpdatedResult.Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder startDateRequired(Boolean cardDateRequired);
        public abstract Builder issueNumberRequired(Boolean issueNumberRequired);
        public abstract CardNumberUpdatedResult build();
    }
}
