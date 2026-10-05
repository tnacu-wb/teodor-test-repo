package com.whitbread.premierinn.gdpr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.AppDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GdprComposeViewModel @Inject constructor(
    private val simplePersistenceManager: SimplePersistenceManager,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    fun onAcceptPrivacyPolicyClicked() {
        viewModelScope.launch(dispatchers.io) {
            simplePersistenceManager.setHasAcceptedPrivacyPolicy()
        }
    }
}

