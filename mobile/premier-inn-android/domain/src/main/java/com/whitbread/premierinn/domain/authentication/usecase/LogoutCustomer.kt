package com.whitbread.premierinn.domain.authentication.usecase

import com.whitbread.premierinn.businessbooker.domain.customer.repository.BusinessCustomerRepository
import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.booking.repository.BookingRepository
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.customer.repository.CustomerRepository
import com.whitbread.premierinn.domain.dashboard.repository.DashboardRepository
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LogoutCustomer @Inject constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val businessCustomerRepository: BusinessCustomerRepository,
        private val isFeatureOn: IsFeatureOn,
        private val bookingRepository: BookingRepository,
        private val amendedReservationRepository: AmendedReservationRepository,
        private val customerRepository: CustomerRepository,
        private val dashboardRepository: DashboardRepository) {
    operator fun invoke(): Completable {
        return if (isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_BUSINESS_BOOKER_QA) &&
            businessCustomerRepository.getLoggedInBusinessEmail().isNotEmpty()
        ) {
            logoutBusinessUser()
        } else {
            logoutLeisureUser()
        }
    }

    private fun logoutBusinessUser(): Completable {
        return authenticationRepository.logout()
            .doOnComplete {
                businessCustomerRepository.clearLoggedInCustomerDetails()
                clearCommonData()
            }.subscribeOn(Schedulers.io())
    }

    private fun logoutLeisureUser(): Completable {
        return authenticationRepository.logout()
            .doOnComplete {
                customerRepository.updateCampaignDevice(isLoggedIn = false)
                customerRepository.clearLoggedInCustomerDetails()
                clearCommonData()
            }.subscribeOn(Schedulers.io())
    }

    private fun clearCommonData() {
        bookingRepository.clearAllBookings()
        bookingRepository.clearAllRoomData()
        dashboardRepository.clearDashboard()
        amendedReservationRepository.clearDaoLinkedWithAmend()
    }
}