package com.whitbread.premierinn.ciol.views.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.ciol.uimodel.PreStayEditItemUIModel
import com.whitbread.premierinn.ciol.viewmodel.PreStayEditItemViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.utils.PreStayEditItemFieldType

@Composable
fun PreStayEditItemPhoneNumber(
    phoneNumber: PreStayEditItemUIModel,
    onAction: (PreStayEditItemViewModel.PreStayEditItemAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(start = 10.dp, end = 10.dp, top = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        PreStayOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = stringResource(R.string.pre_stay_phone_number),
            text = phoneNumber.value,
            shouldShowError = phoneNumber.isInvalid,
            errorText = stringResource(R.string.form_field_phone_number_not_valid),
            onItemValueChanged = { newValue ->
                onAction(
                    PreStayEditItemViewModel.PreStayEditItemAction.ItemValueChanged(
                        newValue,
                        PreStayEditItemFieldType.PHONE_NUMBER
                    )
                )
            }
        )
    }
}
