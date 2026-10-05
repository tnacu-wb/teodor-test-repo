package com.whitbread.premierinn.data.common.persistence

interface AdobeABPersistenceManager {

    fun setTwinRoomPreference(value: Boolean)
    fun getTwinRoomPreference():Boolean
}