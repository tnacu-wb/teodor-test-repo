package com.whitbread.premierinn.createaccount.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.ClickAction;
import com.whitbread.premierinn.common.forms.result.FormResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class ClickActionTransformer implements ObservableTransformer<ClickAction, FormResult> {

    @Override
    public ObservableSource<FormResult> apply(Observable<ClickAction> upstream) {
        return upstream.map(action -> {
            List<InputState> inputs;
            InputState inputState;
            switch (action.id()) {
                case R.id.create_account_terms_conditions_switch:
                    inputs = new ArrayList<>();
                    inputState = InputState.builder()
                            .id(R.id.create_account_terms_conditions_switch)
                            .state(InputState.State.INVISIBLE).build();
                    inputs.add(inputState);

                    return FormResult.create(inputs);
                case R.id.address_form_manual_address_label:
                    inputs = new ArrayList<>();
                    inputState = InputState.builder()
                            .id(R.id.address_form_manual_address_wrapper)
                            .state(InputState.State.VISIBLE).build();
                    inputs.add(inputState);

                    inputState = InputState.builder()
                            .id(R.id.address_form_manual_address_label)
                            .state(InputState.State.INVISIBLE).build();
                    inputs.add(inputState);

                    inputState = InputState.builder()
                            .id(R.id.address_form_postcode_input)
                            .state(InputState.State.INVISIBLE)
                            .build();
                    inputs.add(inputState);

                    return FormResult.create(inputs);
                default:
                    return null;
            }
        });
    }
}
