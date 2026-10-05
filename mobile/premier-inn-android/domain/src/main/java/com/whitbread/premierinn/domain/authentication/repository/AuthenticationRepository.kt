package com.whitbread.premierinn.domain.authentication.repository

import com.whitbread.premierinn.domain.authentication.UserType
import io.reactivex.Completable
import io.reactivex.Single

typealias IdToken = String
typealias RefreshToken = String

interface AuthenticationRepository {
    fun authenticate(user: String, password: String, userType: UserType): Completable
    fun renewRefreshToken(): Single<IdToken>
    fun logout(): Completable
    fun isTokenValid(): Boolean

    fun getIdToken(): Single<IdToken>
    fun getRefreshToken(): Single<RefreshToken>
}