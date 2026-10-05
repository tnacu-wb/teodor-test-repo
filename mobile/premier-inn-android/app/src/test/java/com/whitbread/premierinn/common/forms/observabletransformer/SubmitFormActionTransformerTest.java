package com.whitbread.premierinn.common.forms.observabletransformer;

import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static com.whitbread.premierinn.common.forms.Input.ValidationType.EMAIL;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.NONE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static com.whitbread.premierinn.common.forms.observabletransformer.SubmitFormActionTransformer.getFailedValidationType;
import static com.whitbread.premierinn.common.forms.observabletransformer.SubmitFormActionTransformer.getFormFailedInputs;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class SubmitFormActionTransformerTest {

    @Test
    public void isFormValid() {
        Input input1 = Input.builder().id(1).value(EMPTY_STRING).validationTypes(EnumSet.of(REQUIRED)).build();
        Input input2 = Input.builder().id(1).value("test").validationTypes(EnumSet.of(REQUIRED, EMAIL)).build();
        Input input3 = Input.builder().id(1).value("test").validationTypes(EnumSet.of(EMAIL, REQUIRED)).build();
        Input input4 = Input.builder().id(1).value("test@mail.com").validationTypes(EnumSet.of(EMAIL, REQUIRED)).build();

        MatcherAssert.assertThat(getFormFailedInputs(Collections.unmodifiableList(Arrays.asList(
                input1, input2, input4, input3
        ))), CoreMatchers.is(Arrays.asList(InputState.failed(input1.id(), REQUIRED),
                InputState.failed(input2.id(), EMAIL),
                InputState.failed(input3.id(), EMAIL))));
    }

    @Test
    public void isFormValid2() {
        Input input5 = Input.builder().id(1).value(EMPTY_STRING).validationTypes(EnumSet.of(NONE)).build();
        Input input6 = Input.builder().id(1).value("test@mail.com").validationTypes(EnumSet.of(EMAIL, REQUIRED)).build();
        Input input7 = Input.builder().id(1).value("test@mail.com").validationTypes(EnumSet.of(REQUIRED, EMAIL)).build();

        assertThat(getFormFailedInputs(Collections.unmodifiableList(Arrays.asList(
                input5, input6, input7
        ))), is(new ArrayList<>()));
    }

    @Test
    public void isInputValid1() {
        Input input1 = Input.builder().id(1).value(EMPTY_STRING).validationTypes(EnumSet.of(REQUIRED)).build();

        assertThat(getFailedValidationType(input1), is(REQUIRED));
    }

    @Test
    public void isInputValid2() {
        Input input2 = Input.builder().id(1).value("test").validationTypes(EnumSet.of(REQUIRED, EMAIL)).build();

        assertThat(getFailedValidationType(input2), is(EMAIL));
    }

    @Test
    public void isInputValid3() {
        Input input3 = Input.builder().id(1).value("test").validationTypes(EnumSet.of(EMAIL, REQUIRED)).build();

        assertThat(getFailedValidationType(input3), is(EMAIL));
    }

    @Test
    public void isInputValid4() {
        Input input4 = Input.builder().id(1).value("test@mail.com").validationTypes(EnumSet.of(EMAIL, REQUIRED)).build();

        assertThat(getFailedValidationType(input4), is(nullValue()));
    }

    @Test
    public void isInputValid5() {
        Input input4 = Input.builder().id(1).value("test@mail.com").validationTypes(EnumSet.of(REQUIRED, EMAIL)).build();

        assertThat(getFailedValidationType(input4), is(nullValue()));
    }
}