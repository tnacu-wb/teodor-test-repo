package com.whitbread.premierinn.ciol.views.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.whitbread.premierinn.ciol.fragments.PreStayEditItemFragment
import com.whitbread.premierinn.ciol.uimodel.PreStayEditItemUIModel
import com.whitbread.premierinn.ciol.viewmodel.PreStayEditItemViewModel
import com.whitbread.premierinn.ciol.viewmodel.state.utils.PreStayEditItemFieldType

@Composable
fun PreStayEditItemScreen(
    itemType: String,
    state: PreStayEditItemViewModel.PreStayEditItemState,
    onAction: (PreStayEditItemViewModel.PreStayEditItemAction) -> Unit
) {
    when (itemType) {
        PreStayEditItemFragment.ITEM_TYPE_EMAIL_ADDRESS -> {
            PreStayEditItemEmailAddress(
                state.itemTypeValueMap[PreStayEditItemFieldType.EMAIL_ADDRESS] ?: PreStayEditItemUIModel(),
                onAction
            )
        }

        PreStayEditItemFragment.ITEM_TYPE_PHONE_NUMBER -> {
            PreStayEditItemPhoneNumber(
                state.itemTypeValueMap[PreStayEditItemFieldType.PHONE_NUMBER] ?: PreStayEditItemUIModel(),
                onAction
            )
        }

        PreStayEditItemFragment.ITEM_TYPE_ADDRESS -> {
            PreStayEditItemAddress(state.itemTypeValueMap, onAction)
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreStayEditItemPhoneNumberPreview() {
    PreStayEditItemPhoneNumber(PreStayEditItemUIModel()) { }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreStayEditItemAddressPreview() {
    PreStayEditItemAddress(emptyMap()) { }
}
