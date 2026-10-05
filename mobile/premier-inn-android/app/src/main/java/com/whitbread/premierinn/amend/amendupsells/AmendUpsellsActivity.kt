package com.whitbread.premierinn.amend.amendupsells

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.summary.fragments.MenuAndAllergyInfoBottomSheet
import dagger.hilt.android.AndroidEntryPoint

const val AMEND_UPSELLS_INPUT = "amendUpsellsInput"

@AndroidEntryPoint
class AmendUpsellsActivity : AppCompatActivity() {

    private val viewModel: AmendUpsellsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            state.event?.let {
                onEvent(it)
            }
            UpsellsScreen (
                state,
                viewModel::onAction
            )
        }
    }

    private fun onEvent(event: AmendUpsellsEvent) {
        when (event) {
            is AmendUpsellsEvent.NavigateBack -> {
                setResult(RESULT_OK)
                finish()
            }

            is AmendUpsellsEvent.Continue -> {
               setResult(RESULT_OK)
                finish()
            }

            is AmendUpsellsEvent.ShowMenuAndAllergyInfo -> {
                MenuAndAllergyInfoBottomSheet(mealsList = event.menuAndAllergyInfo).show(
                    supportFragmentManager
                )
            }

            AmendUpsellsEvent.GeneralError -> {
                Toast.makeText(this, getString(R.string.generic_error_description), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.onEventConsumed()
    }

    companion object {
        fun newInstance(context: Context, amendUpsellsInput: AmendUpsellsInput): Intent {
            return Intent(context, AmendUpsellsActivity::class.java).apply {
                putExtra(AMEND_UPSELLS_INPUT, amendUpsellsInput)
            }
        }
    }
}
