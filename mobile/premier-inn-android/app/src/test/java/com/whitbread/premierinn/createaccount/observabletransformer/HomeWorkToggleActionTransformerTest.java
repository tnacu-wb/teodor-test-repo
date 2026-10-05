package com.whitbread.premierinn.createaccount.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.observabletransformer.HomeWorkToggleActionTransformer;
import com.whitbread.premierinn.common.forms.result.FormResult;
import com.whitbread.premierinn.common.view.ToggleButtonView;
import com.whitbread.premierinn.createaccount.action.HomeWorkToggleAction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

@RunWith(MockitoJUnitRunner.class)
public class HomeWorkToggleActionTransformerTest {

    private HomeWorkToggleActionTransformer homeWorkToggleActionTransformer;

    @Before
    public void setup() {
        homeWorkToggleActionTransformer = new HomeWorkToggleActionTransformer();
    }

    @Test
    public void shouldMakeCompanySectionInvisible() {
        List<InputState> expectedInputsLeftClicked = Arrays.asList(
                InputState.builder()
                        .id(R.id.address_form_company_input)
                        .state(InputState.State.INVISIBLE)
                        .build());

        Observable.just(HomeWorkToggleAction.create(ToggleButtonView.State.LEFT))
                .compose(homeWorkToggleActionTransformer)
                .test()
                .assertValue(FormResult.create(expectedInputsLeftClicked));
    }

    @Test
    public void shouldMakeCompanySectionVisible() {
        List<InputState> expectedInputsRightClicked = Arrays.asList(
                InputState.builder()
                        .id(R.id.address_form_company_input)
                        .state(InputState.State.VISIBLE)
                        .build());

        Observable.just(HomeWorkToggleAction.create(ToggleButtonView.State.RIGHT))
                .compose(homeWorkToggleActionTransformer)
                .test()
                .assertValue(FormResult.create(expectedInputsRightClicked));
    }
}
