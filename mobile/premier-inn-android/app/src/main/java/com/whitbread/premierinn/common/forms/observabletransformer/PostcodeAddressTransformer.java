package com.whitbread.premierinn.common.forms.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.InputState;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

import static com.whitbread.premierinn.common.forms.FormUiModel.State.FORM_UPDATE;
import static com.whitbread.premierinn.common.view.ToggleButtonView.State.LEFT;
import static com.whitbread.premierinn.common.view.ToggleButtonView.State.RIGHT;

public class PostcodeAddressTransformer implements ObservableTransformer<ParcelableAddress, FormUiModel> {

    @Override
    public ObservableSource<FormUiModel> apply(Observable<ParcelableAddress> upstream) {
        return upstream.map(postcodeAddress -> {
            List<InputState> inputs = Arrays.asList(
                    InputState.value(R.id.address_form_lookup_postcode_input,
                            postcodeAddress.getPostcode() == null ? "" : postcodeAddress.getPostcode()),
                    InputState.value(R.id.address_form_line1_input, postcodeAddress.getLine1()),
                    InputState.value(R.id.address_form_line2_input, postcodeAddress.getLine2() == null ? "" : postcodeAddress.getLine2()),
                    InputState.value(
                            R.id.address_form_town_city_input,
                            postcodeAddress.getLine4() == null ? "" : postcodeAddress.getLine4()),
                    InputState.builder()
                            .state(InputState.State.VISIBLE)
                            .id(R.id.address_form_manual_address_wrapper).build(),
                    InputState.builder()
                            .state(InputState.State.INVISIBLE)
                            .id(R.id.address_form_manual_address_label).build(),
                    InputState.builder()
                            .id(R.id.address_form_home_work_toggle)
                            .state(InputState.State.IDLE)
                            .value(postcodeAddress.getCompanyName() == null ? LEFT : RIGHT).build(),
                    InputState.builder()
                            .id(R.id.address_form_company_input)
                            .state(postcodeAddress.getCompanyName() == null ? InputState.State.INVISIBLE : InputState.State.VISIBLE)
                            .value(postcodeAddress.getCompanyName() == null ? "" : postcodeAddress.getCompanyName()).build());


            return FormUiModel.builder()
                    .inputStates(inputs)
                    .state(FORM_UPDATE).build();
        });
    }
}
