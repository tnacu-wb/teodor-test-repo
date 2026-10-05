package com.whitbread.premierinn.data.common

interface DatabaseTransactionRunner {
    operator fun invoke(func: () -> Unit)
}