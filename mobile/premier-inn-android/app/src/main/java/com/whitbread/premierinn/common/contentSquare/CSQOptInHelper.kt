package com.whitbread.premierinn.common.contentSquare

import android.content.Context
import android.widget.EditText
import com.contentsquare.android.Contentsquare
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import javax.inject.Inject

class CSQOptInHelper @Inject constructor(
    private val context: Context,
    private val isFeatureOn: IsFeatureOn
) {
    private var configured = false

    fun optInAndMask() {
        if (!configured) {
            Contentsquare.optIn(context)
            if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_CONTENT_SQUARE_UNMASK)) {
                Contentsquare.setDefaultMasking(false)
                Contentsquare.unMask(EditText::class.java)
            } else {
                Contentsquare.setDefaultMasking(true)
                Contentsquare.mask(EditText::class.java)
            }
            configured = true
        }
    }
}