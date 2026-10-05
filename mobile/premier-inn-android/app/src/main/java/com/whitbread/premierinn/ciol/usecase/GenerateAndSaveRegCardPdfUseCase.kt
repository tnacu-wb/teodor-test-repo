package com.whitbread.premierinn.ciol.usecase

import com.whitbread.premierinn.ciol.entity.GenerateAndSaveRegCardPdfStatus
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.AttachFileToReservationUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AttachFileToReservationRequestBody
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

typealias GenerateAndSaveRegCardPdfResult = Result<GenerateAndSaveRegCardPdfStatus, DomainError.GenericError>

const val STATUS_SUCCESS = "Success"

class GenerateAndSaveRegCardPdfUseCase @Inject constructor(
    private val generateRegCardPdfUseCase: GenerateRegCardPdfUseCase,
    private val attachFileToReservationUseCase: AttachFileToReservationUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(
        hotelId: String,
        regCardPdfModel: RegCardPdfModel
    ): Flow<GenerateAndSaveRegCardPdfResult> = generateRegCardPdfUseCase.invoke(regCardPdfModel).flatMapLatest { generatePdfResult ->
        when (generatePdfResult) {
            is Result.Error -> {
                flow { emit(Result.Error(generatePdfResult.error)) }
            }

            is Result.Success -> {
                val attachFileRequestBody = AttachFileToReservationRequestBody(
                    generatePdfResult.data.fileName,
                    regCardPdfModel.reservationId,
                    hotelId,
                    generatePdfResult.data.pdfBase64Encoded
                )
                attachFileToReservationUseCase.invoke(attachFileRequestBody).flatMapLatest { attachFileResult ->
                    when (attachFileResult) {
                        is Result.Error -> {
                            flow { emit(Result.Error(DomainError.GenericError())) }
                        }

                        is Result.Success -> {
                            flow { emit(Result.Success(GenerateAndSaveRegCardPdfStatus(STATUS_SUCCESS))) }
                        }
                    }
                }
            }
        }
    }
}
