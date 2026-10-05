package com.whitbread.premierinn.data.utils

import com.whitbread.premierinn.data.common.DatabaseTransactionRunner

internal object TestTransactionRunner : DatabaseTransactionRunner {
    override fun invoke(func: () -> Unit) = func()
}