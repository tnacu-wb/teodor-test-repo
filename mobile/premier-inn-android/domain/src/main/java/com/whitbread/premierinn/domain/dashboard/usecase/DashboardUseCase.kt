package com.whitbread.premierinn.domain.dashboard.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.repository.IdToken
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.dashboard.repository.DashboardRepository
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.domain.recentsearch.usecase.GetRecentSearchesWithinAWeek
import io.reactivex.Completable
import org.threeten.bp.LocalDate
import javax.inject.Inject

class DashboardUseCase @Inject constructor(
        private val repository: DashboardRepository,
        private val isCustomerLoggedIn: IsCustomerLoggedIn,
        private val recentSearchesWithinAWeek: GetRecentSearchesWithinAWeek,
        private val authenticationRepository: AuthenticationRepository
) {
    operator fun invoke(reservationReference: String?, surname: String?, arrivalDate: LocalDate?) : Completable {
        return recentSearchesWithinAWeek.execute().flatMapCompletable { recentSearchesWithinAWeek ->
            isCustomerLoggedIn.invoke().flatMapCompletable { isLoggedIn ->
                when {
                    isLoggedIn -> {
                        if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer()) {
                            fetchDashboard(null, reservationReference, surname, arrivalDate, recentSearchesWithinAWeek)
                        } else {
                            authenticationRepository.getIdToken()
                                .flatMapCompletable { token ->
                                    fetchDashboard(token, reservationReference, surname, arrivalDate, recentSearchesWithinAWeek)
                                }
                        }
                    }
                    !isLoggedIn && reservationReference != null -> {
                        fetchDashboard(null, reservationReference, surname, arrivalDate, recentSearchesWithinAWeek)
                    }
                    else -> {
                        Completable.complete()
                    }
                }
            }
        }
    }

    private fun fetchDashboard(token: IdToken?, reservationReference: String?, surname: String?, arrivalDate: LocalDate?, recentSearchesWithinAWeek: List<RecentSearch>): Completable {
        return repository.getDashboardFromApi(token, reservationReference, surname, arrivalDate,
                business = isCustomerLoggedIn.isLoggedInAsBusinessCustomer(),
                hasRecentSearches = recentSearchesWithinAWeek.isNotEmpty())
    }
}