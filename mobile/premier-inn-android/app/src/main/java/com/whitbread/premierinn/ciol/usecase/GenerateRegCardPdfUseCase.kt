package com.whitbread.premierinn.ciol.usecase

import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.RegCardPdfResult
import com.whitbread.premierinn.ciol.utils.RegCardPdfGenerator
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

typealias GenerateRegCardPdfResult = Result<RegCardPdfResult, DomainError.GenericError>

class GenerateRegCardPdfUseCase @Inject constructor(
        private val regCardPdfGenerator: RegCardPdfGenerator
) {
    operator fun invoke(regCardPdfModel: RegCardPdfModel): Flow<GenerateRegCardPdfResult> =
        flow {
            val regCardPdfResult = regCardPdfGenerator.generatePdfBase64Encoded(regCardPdfModel)

            regCardPdfResult?.run {
                emit(Result.Success(this))
            } ?: run {
                emit(Result.Error(DomainError.GenericError()))
            }
        }
}
