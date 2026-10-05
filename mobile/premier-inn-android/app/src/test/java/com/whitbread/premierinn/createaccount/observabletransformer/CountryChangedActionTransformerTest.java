package com.whitbread.premierinn.createaccount.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.observabletransformer.CountryChangedActionTransformer;
import com.whitbread.premierinn.createaccount.action.CountryChangedAction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

import static com.whitbread.premierinn.common.forms.InputState.State.VISIBLE;

@RunWith(MockitoJUnitRunner.class)
public class CountryChangedActionTransformerTest {

    private CountryChangedActionTransformer countryChangedActionTransformer;

    @Before
    public void setup() {
        countryChangedActionTransformer = new CountryChangedActionTransformer();
    }

    @Test
    public void countryUK_shouldSeePostcodeInput() {
        CountryChangedAction countryChangedAction = CountryChangedAction.create("GB");

        List<InputState> expectedInputs = new ArrayList<>();

        InputState postcodeState = InputState.builder()
                .state(VISIBLE)
                .id(R.id.address_form_lookup_postcode_input)
                .build();

        InputState findAddressButtonState = InputState.builder()
                .state(VISIBLE)
                .id(R.id.address_form_find_address_button)
                .build();
        expectedInputs.add(postcodeState);
        expectedInputs.add(findAddressButtonState);

        Observable.just(countryChangedAction)
                .compose(countryChangedActionTransformer)
                .test()
                .assertValue(formResult -> formResult.inputs().containsAll(expectedInputs));
    }

    @Test
    public void countryNotUK_shouldSeeOtherPostcodeInputAndManualAddress() {
        CountryChangedAction countryChangedAction = CountryChangedAction.create("PT");

        List<InputState> expectedInputs = Arrays.asList(
                InputState.builder()
                        .state(VISIBLE)
                        .id(R.id.address_form_postcode_input)
                        .build(),
                InputState.builder()
                        .state(VISIBLE)
                        .id(R.id.address_form_manual_address_wrapper)
                        .build());

        Observable.just(countryChangedAction)
                .compose(countryChangedActionTransformer)
                .test()
                .assertValue(formResult -> formResult.inputs().containsAll(expectedInputs));
    }
}
