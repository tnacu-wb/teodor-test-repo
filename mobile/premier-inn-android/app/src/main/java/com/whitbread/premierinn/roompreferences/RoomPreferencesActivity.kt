package com.whitbread.premierinn.roompreferences

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.amendguestsrooms.CriteriaViewModelProvider
import com.whitbread.premierinn.common.AutoCompositeDisposable
import com.whitbread.premierinn.common.PreferencesContentProvider
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.addTo
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.databinding.ActivityRoomPreferencesBinding
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.roomcriteria.RoomCriteriaFragment
import com.whitbread.premierinn.roomcriteria.viewmodel.BaseRoomCriteriaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomPreferencesActivity : BaseActivity<ActivityRoomPreferencesBinding>(), CriteriaViewModelProvider {

    private val disposable: AutoCompositeDisposable by lazy { AutoCompositeDisposable(lifecycle) }
    private val criteriaViewModel: RoomPreferencesCriteriaViewModel by viewModels()
    private val preferencesViewModel: RoomPreferencesViewModel by viewModels()

    private val crashlyticsLogger = LogService()
    private val messageProvider: PreferencesContentProvider = PreferencesContentProvider(this)
    lateinit var customer: Customer
    lateinit var roomState: RoomCriteria

    override fun getCriteriaViewModel(): BaseRoomCriteriaViewModel {
        return criteriaViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_privacy_policy_badge, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun inflateBinding(inflater: LayoutInflater): ActivityRoomPreferencesBinding {
        return ActivityRoomPreferencesBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(getString(R.string.stay_preferences_label), true)
        firstSession = true
        binding.roomPreferencesSaveChangesButton.setOnClickListener { renderNumberOfGuestsButtonUpdates(roomState) }

        preferencesViewModel.events()
                .subscribe {
                    when (it) {
                        is RoomPreferencesViewModel.RoomPreferencesEvent.GenericErrorEvent -> {
                            it.error?.let { crashlyticsLogger.logException(it.fillInStackTrace()) }
                            showToast(getString(R.string.generic_error_description))
                            showError(messageProvider.errorMessageGetCustomer)
                            binding.roomPreferencesSaveChangesButton.setLoadingState(false)
                        }
                        is RoomPreferencesViewModel.RoomPreferencesEvent.UpdatedRoomPreferences ->
                            goToBookingsPreferences()
                        is RoomPreferencesViewModel.RoomPreferencesEvent.InitRoomPreferences -> {
                            it.roomCriteria?.let { it1 -> criteriaViewModel.displayCriteria(it1)
                                renderRoomFragmentUpdates(it1)
                            }
                        }
                    }
                }.addTo(disposable)


        preferencesViewModel.states()
                .distinctUntilChanged()
                .subscribe {
                    updateProgressBar(it)
                    initCustomer(it.getCustomer)
                }.addTo(disposable)

        criteriaViewModel.roomState()
                .distinctUntilChanged()
                .subscribe { state -> roomState = state }
                .addTo(disposable)
    }

    private fun renderNumberOfGuestsButtonUpdates(state: RoomCriteria) {
        if (roomState != null) {
            binding.roomPreferencesSaveChangesButton.setLoadingState(true)
            preferencesViewModel.onSubmitSelection(state, customer) }
    }

   private fun updateProgressBar(state: RoomPreferencesState) {
        binding.roomPreferencesLoading.isVisible = state.isLoading
    }

    private fun renderRoomFragmentUpdates(state: RoomCriteria?) {
        if (state != null) {
            val roomCriteria = RoomCriteria(
                    numberOfAdults = state.numberOfAdults,
                    numberOfChildren = state.numberOfChildren,
                    includeCot = state.includeCot,
                    numberOfInfants = state.numberOfInfants,
                    roomType = state.roomType,
                    roomNumber = state.roomNumber,
                    roomId = state.roomId)

            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container,
                        RoomCriteriaFragment.newInstance(
                                roomId = roomCriteria.roomNumber,
                                roomDisplayNumber = roomCriteria.roomNumber,
                                homepageActivity = false,
                                initRoomCriteria = roomCriteria))
                commitNow()
            }
        }
    }

    private fun initCustomer(customerResult: Customer?) {
        if (customerResult != null) {
            customer = customerResult
        }
    }

    private fun goToBookingsPreferences() {
        binding.roomPreferencesSaveChangesButton.setLoadingState(false)
        this.setResult(Activity.RESULT_OK)
        this.finish()
    }

    fun showError(errorMessageUpdateCustomer: String) {
        Toast.makeText(this, errorMessageUpdateCustomer, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ROOM_PREFERENCES_RESULT_KEY = 417
        var firstSession: Boolean = false
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, RoomPreferencesActivity::class.java)
        }
    }
}