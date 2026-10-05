package com.whitbread.premierinn.common.forms.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.view.AddressFormView;
import com.whitbread.premierinn.common.view.ToggleButtonView;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.createaccount.action.HomeWorkToggleAction;
import com.whitbread.premierinn.common.forms.result.FormResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class HomeWorkToggleActionTransformer implements ObservableTransformer<HomeWorkToggleAction, FormResult> {

    @Override
    public ObservableSource<FormResult> apply(Observable<HomeWorkToggleAction> upstream) {
        return upstream.map(homeWorkToggleAction -> {
            List<InputState> inputs = new ArrayList<>();
            AddressFormView.AddressState addressState = homeWorkToggleAction.homeWorkState() == ToggleButtonView.State.LEFT
                    ? AddressFormView.AddressState.HOME
                    : AddressFormView.AddressState.WORK;

            InputState input = InputState.builder()
                    .id(R.id.address_form_company_input)
                    .state(addressState == AddressFormView.AddressState.HOME ? InputState.State.INVISIBLE : InputState.State.VISIBLE)
                    .build();
            inputs.add(input);

            return FormResult.create(inputs);
        });
    }
}