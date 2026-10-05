package com.whitbread.premierinn.data.common.persistence

import android.content.SharedPreferences
import androidx.core.content.edit
import com.whitbread.premierinn.data.common.persistence.AdobeABPersistenceManagerImpl.Constants.KEY_AB_TWIN_ROOMS
import javax.inject.Inject

class AdobeABPersistenceManagerImpl @Inject constructor(private val sharedPreferences: SharedPreferences) : AdobeABPersistenceManager{
    override fun setTwinRoomPreference(value: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_AB_TWIN_ROOMS,value) }
    }

    override fun getTwinRoomPreference(): Boolean {
        return sharedPreferences.getBoolean(KEY_AB_TWIN_ROOMS,false)
    }

    object Constants {
        const val KEY_AB_TWIN_ROOMS = "KEY_AB_TWIN_ROOMS"
    }
}