package com.whitbread.premierinn.businessbooker.domain.company

import com.whitbread.premierinn.domain.authentication.repository.IdToken
import io.reactivex.Single

interface CompanyRepository {
    fun getCompany(idToken: IdToken, companyId : String, centralCard: String?) : Single<Company>
}