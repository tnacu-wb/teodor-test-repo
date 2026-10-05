package com.whitbread.premierinn.summary

sealed class SummaryDialogAction {
    abstract val action: () -> Unit

    class Positive(override val action: () -> Unit) : SummaryDialogAction()
    class Continue(override val action: () -> Unit) : SummaryDialogAction()
    class TryAgain(override val action: () -> Unit) : SummaryDialogAction()
}
