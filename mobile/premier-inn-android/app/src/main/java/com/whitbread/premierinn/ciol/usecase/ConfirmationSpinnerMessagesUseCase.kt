package com.whitbread.premierinn.ciol.usecase

import com.google.gson.Gson
import com.whitbread.premierinn.data.remote.graphql.ConfirmationSpinnerMessages
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import javax.inject.Inject

class GetConfirmationSpinnerMessagesUseCase @Inject constructor(
    private val getStringResource: GetStringResource
) {

    operator fun invoke(): ConfirmationSpinnerMessages = Gson().fromJson(
        getStringResource(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_MESSAGES_CONFIG),
        ConfirmationSpinnerMessages::class.java
    )
}