package com.whitbread.premierinn.common.utils

import android.content.Context
import com.whitbread.premierinn.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Before

class DomainStringResourceExtensionsKtTest {

    private val context = mockk<Context>()

    @Before
    fun setUp() {

        every { context.getString(R.string.in_parenthesis, any()) } answers {
            "(${it.invocation.args[0]})"}


        every {
            val nightCountArg = less(2)
            context.resources.getQuantityString(R.plurals.nights, nightCountArg, nightCountArg)
        } answers { "1 night"}

        every {
            val guestCountArg = less(2)
            context.resources.getQuantityString(R.plurals.guests, guestCountArg, guestCountArg)
        } answers { "1 guest"}

        every {
            val nightCountArg = more(1)
            context.resources.getQuantityString(R.plurals.nights, nightCountArg, nightCountArg)
        } answers {
            "${it.invocation.args[0]} nights"
        }

        every {
            val guestCountArg = more(1)
            context.resources.getQuantityString(R.plurals.guests, guestCountArg, guestCountArg)
        } answers {
            "${it.invocation.args[0]} guests"
        }

    }
}