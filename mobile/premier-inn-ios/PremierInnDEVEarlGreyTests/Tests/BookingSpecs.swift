//
//  BookingSpecs.swift
//  PremierInnDEVEarlGreyTests
//
//  Created by Filippo Minelle on 13/07/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import EarlGrey
import Hippolyte
import Quick

@testable import PremierInn

class RegressionPack_FlexPIBookingSpecs: QuickSpec {

    // MARK: - Tests

    override func spec() {

        let homePage = HomePage()
        let locationSuggestionsPage = LocationSuggestionsPage()
        let searchResultsPage = SearchResultsPage()
        let hotelDetailsPage = HotelDetailsPage()
        let personalDetailsPage = PersonalDetailsPage()
        let cardDetailsPage = CardDetailsPage()
        let reviewAndBookPage = ReviewAndBookPage()
        let bookingConfirmationPage = BookingConfirmationPage()

        describe("GIVEN I am on the hotel details page AND 3 rates are available") {

            context("a) WHEN I view the rates") {

                beforeEach {
                    let appDelegate = UIApplication.shared.delegate as? AppDelegate
                    appDelegate?.resetApplicationForTesting()

                    GREYCondition(name: "Wait for main root view controller") {
                        return true
                    }.wait(withTimeout: 3)

                    homePage.hideBanner()
                    homePage.select(environment: .alpha2)

                    homePage.goToLocationSelection()
                    locationSuggestionsPage.searchLocation(text: "Liverpool")

                    locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

                        homePage.tapSearch()

                        // check SRP elements
                        searchResultsPage.checkPageElementsExist()
                        searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

                        hotelDetailsPage.table.perform(grey_scrollInDirection(.down, 200))
                    }
                }

                it("THEN I should be able to see the rate nameFlex and associated cancellation policy with the description 'Pay now or on arrival. Cancel up to 1pm on arrival day.'") {

                    let labelFlex = EarlGrey.selectElement(with: grey_accessibilityLabel("Pay now or later. Cancel up to 1 pm on arrival day. "))
                    labelFlex.assert(grey_sufficientlyVisible())

                    let labelSemiFlex = EarlGrey.selectElement(with: grey_allOf([grey_accessibilityLabel("Pay now. Change arrival date. Cancel up to 3 days before arrival."), grey_interactable()]))
                    labelSemiFlex.assert(grey_sufficientlyVisible())
                }
            }

            context("b) WHEN I view the rates WHEN I select the rate type Flex AND click on Book Now") {

                beforeEach {
                    hotelDetailsPage.flexBookButton.perform(grey_tap())
                }

                it("THEN I should be redirected to the 'Customise your stay' page AND I should be able to see the rate name Flex") {
                    let labelAddMeals = EarlGrey.selectElement(with: grey_accessibilityLabel("Customise your stay"))
                    labelAddMeals.assert(grey_sufficientlyVisible())
                }
            }

            context("c) WHEN I select 'Premier Inn Breakfast' AND I click on continue button") {

                beforeEach {
                    UpsellsPage.addBreakfast()
                    UpsellsPage.tapContinue()
                }

                it("THEN I should be redirected to the Personal details page") {
                    personalDetailsPage.chooseTitle.assert(grey_sufficientlyVisible())
                }
            }

            context("d) WHEN I am on the Personal details page AND I have filled in all valid inputs in to the fields AND I click on 'Continue to final step' button") {

                beforeEach {
                    personalDetailsPage.enterTestPersonalDetails()
                    personalDetailsPage.tapContinue()

                    cardDetailsPage.enterTestData()
                    cardDetailsPage.tapContinue()

                    reviewAndBookPage.waitToLoad()
                }

                it("THEN I should be redirected to the Review & Book page AND I should be able to see the rate name Flex and associated cancellation policy with the description AND I should see 'Premier Inn breakfast' under ExtrasAND the correct Total Cost") {
                    reviewAndBookPage.breakfastLabel(with: "Premier Inn Breakfast").assert(grey_sufficientlyVisible())
                }
            }

            context("e) I am on the Review & Book page AND I have selected the 'Pay Now' option AND I have filled in valid Credit Card (4444 3333 2222 1111) details with correct CVV number AND I have checked and agreed to T&C's WHEN I click on 'Confirm booking' button") {

                beforeEach {
                    reviewAndBookPage.selectPayment(interval: .now)
                    reviewAndBookPage.scrollDown(amount: 100)
                    reviewAndBookPage.enterCVV("123")
                    reviewAndBookPage.acceptTerms()
                    reviewAndBookPage.tapContinue()
                }

                it("THEN I should see the Confirmation pageAND I should be able to see the rate name Flex AND the correct Total of the booking") {
                    bookingConfirmationPage.cancelButton.assert(grey_sufficientlyVisible())
                }
            }

        }
    }
}

class RegressionPack_FlexPIDebitCardBookingSpecs: QuickSpec {

    override func spec() {

        let homePage = HomePage()
        let locationSuggestionsPage = LocationSuggestionsPage()
        let searchResultsPage = SearchResultsPage()
        let hotelDetailsPage = HotelDetailsPage()
        let personalDetailsPage = PersonalDetailsPage()
        let cardDetailsPage = CardDetailsPage()
        let reviewAndBookPage = ReviewAndBookPage()
        let bookingConfirmationPage = BookingConfirmationPage()

        describe("GIVEN I am on the payment details screen") {

            context("AND I enter card details for VISA CREDIT 4444333322221111") {

                beforeEach {

                    let appDelegate = UIApplication.shared.delegate as? AppDelegate
                    appDelegate?.resetApplicationForTesting()

                    GREYCondition(name: "Wait for main root view controller") {
                        return true
                    }.wait(withTimeout: 3)

                    homePage.hideBanner()
                    homePage.select(environment: .alpha2)

                    homePage.goToLocationSelection()
                    locationSuggestionsPage.searchLocation(text: "Liverpool")

                    locationSuggestionsPage.selectSuggestion(text: "Liverpool, UK") {

                        homePage.tapSearch()

                        searchResultsPage.checkPageElementsExist()
                        searchResultsPage.tapHotelInList(hotelName: "Liverpool City Centre (Moorfields)")

                        hotelDetailsPage.table.perform(grey_scrollInDirection(.down, 100))
                        hotelDetailsPage.flexBookButton.perform(grey_tap())

                        UpsellsPage.addBreakfast()
                        UpsellsPage.tapContinue()

                        personalDetailsPage.enterTestPersonalDetails()
                        personalDetailsPage.tapContinue()
                    }
                }

                afterEach {
                    reviewAndBookPage.selectPayment(interval: .now)
                    reviewAndBookPage.scrollDown(amount: 100)
                    reviewAndBookPage.enterCVV("123")
                    reviewAndBookPage.acceptTerms()
                    reviewAndBookPage.tapContinue()

                    bookingConfirmationPage.checkLiveBookingIsSuccessful()
                    bookingConfirmationPage.goBack()

                    homePage.goToSearch()
                }

                it("THEN I should see VISA CREDIT card as selected payment method") {

                    cardDetailsPage.enterTestData(for: "4444333322221111")
                    cardDetailsPage.tapContinue()

                    reviewAndBookPage.waitToLoad()

                    reviewAndBookPage.scrollDown(amount: 40)
                    reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "1111")
                }

                it("THEN I should see VISA DEBIT card as selected payment method") {

                    cardDetailsPage.enterTestData(for: "4582620000000037")
                    cardDetailsPage.tapContinue()

                    reviewAndBookPage.waitToLoad()

                    reviewAndBookPage.scrollDown(amount: 40)
                    reviewAndBookPage.checkPayingWithCardNumber(withLastFourDigits: "0037")
                }
            }
        }
    }
}
