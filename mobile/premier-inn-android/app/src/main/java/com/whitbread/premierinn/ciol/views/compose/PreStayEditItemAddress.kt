package com.whitbread.premierinn.ciol.views.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.uimodel.PreStayEditItemUIModel
import com.whitbread.premierinn.ciol.utils.getStringWithAsterisk
import com.whitbread.premierinn.ciol.viewmodel.PreStayEditItemViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.utils.PreStayEditItemFieldType
import com.whitbread.premierinn.compose.ui.components.ButtonType
import com.whitbread.premierinn.compose.ui.components.GenericButton
import com.whitbread.premierinn.compose.ui.components.Purple

import com.whitbread.premierinn.data.common.EMPTY_STRING

@Composable
fun PreStayEditItemAddress(
    addressFields: Map<PreStayEditItemFieldType, PreStayEditItemUIModel>,
    onAction: (PreStayEditItemViewModel.PreStayEditItemAction) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)
        .padding(start = 10.dp, end = 10.dp, top = 16.dp)
    ) {
        val country = addressFields[PreStayEditItemFieldType.COUNTRY] ?: PreStayEditItemUIModel()
        PreStayOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = getStringWithAsterisk(stringResource(R.string.form_field_label_country)),
            text = country.value,
            shouldShowError = country.isInvalid,
            errorText = stringResource(R.string.reg_card_country_missing_error),
            enabled = false,
            onItemValueChanged = { newValue ->
                onAction(
                    PreStayEditItemViewModel.PreStayEditItemAction.ItemValueChanged(
                        newValue,
                        PreStayEditItemFieldType.COUNTRY
                    )
                )
            },
            onItemClicked = {
                onAction(PreStayEditItemViewModel.PreStayEditItemAction.CountrySelectorClicked)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        val postcode = addressFields[PreStayEditItemFieldType.POST_CODE] ?: PreStayEditItemUIModel()
        val findAddress = addressFields[PreStayEditItemFieldType.FIND_ADDRESS] ?: PreStayEditItemUIModel()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (postcode.isVisible) {
                PreStayOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(if (findAddress.isVisible) 0.5f else 1f),
                    label = getStringWithAsterisk(stringResource(R.string.address_form_postcode)),
                    text = postcode.value,
                    shouldShowError = postcode.isInvalid,
                    errorText = stringResource(R.string.address_form_postcode_validation_error),
                    onItemValueChanged = { newValue ->
                        onAction(
                            PreStayEditItemViewModel.PreStayEditItemAction.ItemValueChanged(
                                newValue,
                                PreStayEditItemFieldType.POST_CODE
                            )
                        )
                    }
                )
            }

            if (findAddress.isVisible) {
                GenericButton(
                    text = stringResource(R.string.postcode_finder_find_address_label),
                    modifier = Modifier.padding(top = 10.dp),
                    buttonType = ButtonType.Outlined,
                    color = Purple,
                    textColor = Purple,
                    onClick = {
                        onAction(PreStayEditItemViewModel.PreStayEditItemAction.FindAddressClicked)
                    }
                )
            }
        }

        if (postcode.isVisible || findAddress.isVisible) {
            Spacer(modifier = Modifier.height(10.dp))
        }

        val addressLine1 = addressFields[PreStayEditItemFieldType.ADDRESS_LINE_1] ?: PreStayEditItemUIModel()
        PreStayOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = getStringWithAsterisk(stringResource(R.string.address_form_address_line1)),
            text = addressLine1.value,
            shouldShowError = addressLine1.isInvalid,
            errorText = stringResource(R.string.address_form_address_line_1_validation_error),
            onItemValueChanged = { newValue ->
                onAction(
                    PreStayEditItemViewModel.PreStayEditItemAction.ItemValueChanged(
                        newValue,
                        PreStayEditItemFieldType.ADDRESS_LINE_1
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        val addressLine2 = addressFields[PreStayEditItemFieldType.ADDRESS_LINE_2] ?: PreStayEditItemUIModel()
        PreStayOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = stringResource(R.string.reg_card_address_line_2_hint),
            text = addressLine2.value,
            shouldShowError = false,
            errorText = EMPTY_STRING,
            onItemValueChanged = { newValue ->
                onAction(
                    PreStayEditItemViewModel.PreStayEditItemAction.ItemValueChanged(
                        newValue,
                        PreStayEditItemFieldType.ADDRESS_LINE_2
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        val addressLine3 = addressFields[PreStayEditItemFieldType.ADDRESS_LINE_3] ?: PreStayEditItemUIModel()
        PreStayOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = stringResource(R.string.reg_card_address_line_3_hint),
            text = addressLine3.value,
            shouldShowError = false,
            errorText = EMPTY_STRING,
            onItemValueChanged = { newValue ->
                onAction(
                    PreStayEditItemViewModel.PreStayEditItemAction.ItemValueChanged(
                        newValue,
                        PreStayEditItemFieldType.ADDRESS_LINE_3
                    )
                )
            }
        )
    }
}
