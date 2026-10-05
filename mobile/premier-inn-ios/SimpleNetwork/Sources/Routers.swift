//
//  Routers.swift
//  SimpleNetwork
//
//  Created by Marcello Mascia on 06/08/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation

public class ProductionRouter: Router {
    var graphQLService: GraphQL = Webservice.productionGraphQL

    override func service(for action: WebserviceAction) throws -> WebserviceProtocol {
        switch action {
        case .migratedLogin, .migratedGetUserCredentials, .logout:
            return Webservice.productionMigratedAuth0
        case .migratedBBLogin, .migratedBBGetUserCredentials:
            return Webservice.productionMigratedBBAuth0
        case .getUser, .startCheckInOnlineSession, .addCheckInOnlineGuestDetails, .addCheckInOnlineUpsells,
             .closeCheckInOnlineSession, .completePayment, .savePaymentCard, .deletePaymentCard, .getCompany,
             .checkInOnlinePayment, .changePassword, .updateUserDetails, .deleteUser, .additionalGuests,
             .updateFoodPref, .updateRoomPref, .dashboardComponents, .doorKey:
            guard Router.isRestMigrated else { return Webservice.liveMicroservices }

            return Webservice.migratedLiveMicroservices
        case .suggestions:

            guard Router.isAutocompleteMigrated else { return Webservice.liveMicroservices }

            return Webservice.migratedLiveMicroservices
        case .wallet:
            return Webservice.migratedLiveMicroservices
        case .registerUser, .hotelDetails, .availability, .release, .hold, .saveUpsells, .holdWithGuests,
             .totalCostWithCityTax, .checkBasketStatus, .reservation, .headerInformation, .paymentMethods,
             .cccpPayment, .cancelReservation, .availabilityForAmend, .getRestrictions, .findBookingSource,
             .ratesInformation, .countries, .availabilities, .getStays, .copyBooking, .amendBookingDates,
             .amendPackages, .confirmAmend, .getPackages, .amendEditRoom, .amendRemoveRoom, .addNewRoom,
             .reservationForAmend, .amendSummary, .amendConfirmationPrices, .getReservationWithAmendSummary,
             .addressLookup, .bookingInformation, .getHotelBySlug, .updateReservationPreferences,
             .getCategoryLabels, .confirmPreCheckInOut, .getHotelPreferences, .amendCiolPackages,
             .initiateSaveCard, .resendInvoiceEmail, .homepageContent, .roomClassConfig, .authorizePayment,
             .forgotPassword, .promotionsInformation, .validateDiscountCode, .updateCiolStatus,
             .ciolPaymentActions, .ciolBackgroundCharge,
             .attachFileToReservation, .updatePreCheckInStatus, .ciolPDFGenerator, .generateOTP, .verifyOTP,
             .keyCheckIn, .provisionKey, .marketingPreferences, .updateMarketingPreferences, .anonNewsletter:
            return graphQLService
        case .initMobileSDKPayment:
            return graphQLService
        }
    }
}

public class LowerEnvGraphQLRouter: Router {
    var graphQLService: GraphQL = Webservice.uatGraphQL
    var nonMigratedRestService: Microservices = Webservice.uatMicroservicesAlpha2
    var restService: Microservices? = Webservice.migratedUatMicroservices
    var migratedLeisureAuth0Service: Auth0ConfigurableRealmService = Webservice.migratedUatAuth0
    var migratedBBAuth0Service: Auth0ConfigurableRealmService = Webservice.migratedBBUatAuth0

    override func service(for action: WebserviceAction) throws -> WebserviceProtocol {
        switch action {
        case .migratedLogin, .migratedGetUserCredentials, .logout:
            return migratedLeisureAuth0Service
        case .migratedBBLogin, .migratedBBGetUserCredentials:
            return migratedBBAuth0Service
        case .getUser, .startCheckInOnlineSession, .addCheckInOnlineGuestDetails, .addCheckInOnlineUpsells,
             .closeCheckInOnlineSession, .completePayment, .savePaymentCard, .deletePaymentCard, .getCompany,
             .checkInOnlinePayment, .changePassword, .updateUserDetails, .deleteUser, .additionalGuests,
             .updateFoodPref, .updateRoomPref, .dashboardComponents, .doorKey:
            guard let restService, Router.isRestMigrated else { return nonMigratedRestService }

            return restService
        case .suggestions:
            // do not migrate - "Such is the nature of evil. Out there in the vast ignorance of the world it festers and spreads. A shadow that grows in the dark. A sleepless malice as black as the oncoming wall of night. So it ever was. So will it always be. In time all foul things come forth."
            guard let restService, Router.isAutocompleteMigrated else { return nonMigratedRestService }

            return restService
        case .wallet:
            guard let restService else { return nonMigratedRestService }

            return restService
        case .registerUser, .hotelDetails, .availability, .release, .hold, .saveUpsells, .holdWithGuests,
             .totalCostWithCityTax, .checkBasketStatus, .reservation, .headerInformation, .paymentMethods,
             .cccpPayment, .cancelReservation, .availabilityForAmend, .getRestrictions, .findBookingSource,
             .ratesInformation, .countries, .availabilities, .getStays, .copyBooking, .amendBookingDates,
             .amendPackages, .confirmAmend, .getPackages, .amendEditRoom, .amendRemoveRoom, .addNewRoom,
             .reservationForAmend, .amendSummary, .amendConfirmationPrices, .getReservationWithAmendSummary,
             .addressLookup, .bookingInformation, .getHotelBySlug, .updateReservationPreferences,
             .getCategoryLabels, .confirmPreCheckInOut, .getHotelPreferences, .amendCiolPackages,
             .initiateSaveCard, .resendInvoiceEmail, .homepageContent, .roomClassConfig, .authorizePayment,
             .forgotPassword, .promotionsInformation, .validateDiscountCode, .updateCiolStatus,
             .ciolPaymentActions, .ciolBackgroundCharge,
             .attachFileToReservation, .updatePreCheckInStatus, .ciolPDFGenerator, .generateOTP, .verifyOTP,
             .keyCheckIn, .provisionKey, .anonNewsletter, .marketingPreferences, .updateMarketingPreferences:
            return graphQLService
        case .initMobileSDKPayment:
            return graphQLService
        }
    }
}

public class DitGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.ditGraphQL
        restService = Webservice.migratedDitMicroservices
    }
}

public class UatGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.uatGraphQL
        restService = Webservice.migratedUatMicroservices
    }
}

public class SitGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.sitGraphQL
        restService = Webservice.migratedSitMicroservices
        migratedLeisureAuth0Service = Webservice.migratedSitAuth0
        migratedBBAuth0Service = Webservice.migratedBBSitAuth0
    }
}

public class DemoGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.demoGraphQL
        restService = Webservice.migratedDemoMicroservices
        migratedLeisureAuth0Service = Webservice.migratedDemoAuth0
        migratedBBAuth0Service = Webservice.migratedBBDemoAuth0
    }
}

public class PreprodGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.preprodGraphQL
        restService = Webservice.migratedPreProdMicroservices
    }
}

public class PerfGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.perfGraphQL
        restService = Webservice.migratedPerfMicroservices
        migratedLeisureAuth0Service = Webservice.migratedPerfAuth0
        migratedBBAuth0Service = Webservice.migratedBBPerfAuth0
    }
}

public class HulkGraphQLRouter: ProductionRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.hulkGraphQL
    }
}

public class WandaGraphQLRouter: ProductionRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.wandaGraphQL
    }
}

public class DevelopmentGraphQLRouter: LowerEnvGraphQLRouter {
    override public init() {
        super.init()

        graphQLService = Webservice.developmentGraphQL
        restService = Webservice.migratedDevMicroservices
    }
}
