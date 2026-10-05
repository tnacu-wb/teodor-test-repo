package com.whitbread.premierinn.domain

import java.util.*
import javax.inject.Inject

class ClockImpl @Inject constructor() : Clock {

    override fun now(): Long {
        return Date().time
    }

}