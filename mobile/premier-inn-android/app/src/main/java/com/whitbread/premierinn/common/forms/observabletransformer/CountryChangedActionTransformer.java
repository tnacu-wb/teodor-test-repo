package com.whitbread.premierinn.common.forms.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.result.FormResult;
import com.whitbread.premierinn.createaccount.action.CountryChangedAction;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

import static com.whitbread.premierinn.common.forms.InputState.State.INVISIBLE;
import static com.whitbread.premierinn.common.forms.InputState.State.VISIBLE;

public class CountryChangedActionTransformer implements ObservableTransformer<CountryChangedAction, FormResult> {

    @Override
    public ObservableSource<FormResult> apply(Observable<CountryChangedAction> upstream) {
        return upstream.map(countryChangedAction -> {
            boolean isCountryUk = CountryDomain.Companion.isCountryUk(countryChangedAction.countryCode());
            List<InputState> inputStates = new ArrayList<>();


            if (isCountryUk) {
                InputState lookupPostcodeState = InputState.builder()
                        .state(VISIBLE)
                        .id(R.id.address_form_lookup_postcode_input)
                        .build();

                InputState findAddressButtonState = InputState.builder()
                        .state(VISIBLE)
                        .id(R.id.address_form_find_address_button)
                        .build();

                InputState postcodeState = InputState.builder()
                        .state(INVISIBLE)
                        .value("")
                        .id(R.id.address_form_postcode_input)
                        .build();

                inputStates.add(lookupPostcodeState);
                inputStates.add(findAddressButtonState);
                inputStates.add(postcodeState);
            } else {
                InputState lookupPostcodeState = InputState.builder()
                        .state(INVISIBLE)
                        .value("")
                        .id(R.id.address_form_lookup_postcode_input)
                        .build();

                InputState findAddressButtonState = InputState.builder()
                        .state(INVISIBLE)
                        .id(R.id.address_form_find_address_button)
                        .build();

                InputState postcodeState = InputState.builder()
                        .state(VISIBLE)
                        .id(R.id.address_form_postcode_input)
                        .build();

                InputState addressWrapperState = InputState.builder()
                        .state(VISIBLE)
                        .id(R.id.address_form_manual_address_wrapper)
                        .build();

                InputState addressLabelState = InputState.builder()
                        .state(INVISIBLE)
                        .id(R.id.address_form_manual_address_label)
                        .build();

                inputStates.add(lookupPostcodeState);
                inputStates.add(findAddressButtonState);
                inputStates.add(postcodeState);
                inputStates.add(addressWrapperState);
                inputStates.add(addressLabelState);
            }
            return FormResult.create(inputStates);
        });
    }
}