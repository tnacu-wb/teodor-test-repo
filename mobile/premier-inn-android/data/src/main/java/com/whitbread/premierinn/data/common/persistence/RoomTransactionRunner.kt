package com.whitbread.premierinn.data.common.persistence

import com.whitbread.premierinn.data.common.DatabaseTransactionRunner
import com.whitbread.premierinn.data.common.PremierInnDatabase
import javax.inject.Inject

class RoomTransactionRunner @Inject constructor(
        private val db: PremierInnDatabase
) : DatabaseTransactionRunner {
    override operator fun invoke(func: () -> Unit) {
        db.runInTransaction {
            func()
        }
    }
}