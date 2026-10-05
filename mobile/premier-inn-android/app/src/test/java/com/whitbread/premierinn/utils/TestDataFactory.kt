package com.whitbread.premierinn.utils

import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 *
 */
object TestDataFactory {

    fun randomUuid(): String {
        return UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, 1000 + 1)
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomDouble(): Double {
        return randomInt().toDouble()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }

    fun randomFloat(): Float {
        return randomInt().toFloat()
    }

}