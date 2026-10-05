package com.whitbread.premierinn.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> collectEmissions(
    state: StateFlow<T>,
    stateResults: MutableList<T>,
    testScheduler: TestCoroutineScheduler
) {
    state.onEach { stateResults.add(it) }
        .launchIn(CoroutineScope(UnconfinedTestDispatcher(testScheduler)))
}
