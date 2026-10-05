package com.whitbread.premierinn.createaccount.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.ClickAction;
import com.whitbread.premierinn.common.forms.result.FormResult;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class ClickActionTransformerTest {

    private ClickActionTransformer clickActionTransformer;

    @Before
    public void setup() {
        clickActionTransformer = new ClickActionTransformer();
    }

    @Test
    public void shouldMakeManualAddressSectionVisible() {
        List<InputState> expectedInputs = Arrays.asList(
                InputState.builder()
                        .id(R.id.address_form_manual_address_wrapper)
                        .state(InputState.State.VISIBLE).build(),
                InputState.builder()
                        .id(R.id.address_form_manual_address_label)
                        .state(InputState.State.INVISIBLE).build());

        Observable.just(ClickAction.create(R.id.address_form_manual_address_label))
                .compose(clickActionTransformer)
                .test()
                .assertValue(formResult -> formResult.inputs().containsAll(expectedInputs));
    }

    @Test
    public void shouldMakeTermsConditionsErrorInvisible() {
        List<InputState> expectedInputs = Arrays.asList(
                InputState.builder()
                        .id(R.id.create_account_terms_conditions_switch)
                        .state(InputState.State.INVISIBLE).build());

        Observable.just(ClickAction.create(R.id.create_account_terms_conditions_switch))
                .compose(clickActionTransformer)
                .test()
                .assertValue(FormResult.create(expectedInputs));
    }


}
