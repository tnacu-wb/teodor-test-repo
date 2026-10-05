//
//  EditDetailsViewTests.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 25.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

final class MockEditDetailsEventHandler: EditDetailsEventHandler {

    var address: PremierInn.StoredAddressModel?
    var flow: EditDetailsFlow? = .leadGuestTitleNameInfo(indexPath: IndexPath())

    var viewIsReadyCalled = false
    var handleValidationSuccessCalled = false
    var showSalutationViewCalled = false
    var salutationUpdatedCalled = false
    var showCountriesViewCalled = false
    var nationalityUpdated = false
    var updateEditDetailsModel = false
    var isLeadGuest = false

    var didCallTapPostcodeSearch = false
    var didCallDismissPostcodeSearch = false
    var didCallSelectAddress = false
    var postcodeSearchValue: String? = nil

    func viewIsReady() {
        viewIsReadyCalled = true
    }

    func handleValidationSuccess() {
        handleValidationSuccessCalled = true
    }

    func showSalutationView(indexPath: IndexPath) {
        showSalutationViewCalled = true
    }

    func salutationUpdated(with: String, indexPath: IndexPath) {
        salutationUpdatedCalled = true
    }

    func showCountriesView(indexPath: IndexPath, source: PremierInn.ListViewSource) {
        showCountriesViewCalled = true
    }

    func nationalityUpdated(with country: CountryItem, indexPath: IndexPath) {
        nationalityUpdated = true
    }

    func updateEditDetailsModel(with viewModel: EditDetailsModel) {
        updateEditDetailsModel = true
    }

    func didUpdateNationality(with country: PremierInn.CountryItem) {}

    func didUpdateCountry(with country: PremierInn.CountryItem) {}

    func didTapPostcodeSearch(with postcode: String?) {
        didCallTapPostcodeSearch = true
        postcodeSearchValue = postcode
    }

    func didDismissPostcodeSearch() {
        didCallDismissPostcodeSearch = true
    }

    func didSelectAddress(_ address: Any?) {
        didCallSelectAddress = true
    }
}

final class EditDetailsViewTests: XCTestCase {
    var viewController: EditDetailsViewController!
    var mockEventHandler: MockEditDetailsEventHandler!

    override func setUp() {
        super.setUp()
        viewController = EditDetailsViewController()
        mockEventHandler = MockEditDetailsEventHandler()
   
        viewController.eventHandler = mockEventHandler
    }

    func test_viewIsReadyCalled_Successful() {
        mockEventHandler.viewIsReady()
        XCTAssertTrue(mockEventHandler.viewIsReadyCalled)
    }

    func test_handleValidationSuccessCalled_Successful() {
        mockEventHandler.handleValidationSuccess()
        XCTAssertTrue(mockEventHandler.handleValidationSuccessCalled)
    }

    func test_showCountrieViewCalled_Successful() {
        mockEventHandler.showCountriesView(indexPath: IndexPath(), source: .countries)
        XCTAssertTrue(mockEventHandler.showCountriesViewCalled)
    }

    func test_nationalityUpdated_Successful() {
        guard let countryItem = CountryItem(country: Country(code: "",
                                                             name: "",
                                                             isoCode: "",
                                                             dialingCode: "",
                                                             flagImage: "",
                                                             passportRequired: false,
                                                             nationality: nil)) else { return }

        mockEventHandler.nationalityUpdated(with: countryItem, indexPath: IndexPath())
        XCTAssertTrue(mockEventHandler.nationalityUpdated)
    }

    func test_updateEditDetailsModel_Successful() {
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath()))
        
        mockEventHandler.updateEditDetailsModel(with: editDetailsModel)
        XCTAssertTrue(mockEventHandler.updateEditDetailsModel)
    }

    func test_showSalutationViewCalled_Successful() {
        mockEventHandler.showSalutationView(indexPath: IndexPath())
        XCTAssertTrue(mockEventHandler.showSalutationViewCalled)
    }

    func test_salutationUpdatedCalled_Successful() {
        mockEventHandler.salutationUpdated(with: "", indexPath: IndexPath())
        XCTAssertTrue(mockEventHandler.salutationUpdatedCalled)
    }

    func test_shouldShowIdentificationDocumentsSection_PIBrandAndNationalityGB() {
        let country = CountryItem(country: Country(code: "GB",
                                                   name: "",
                                                   isoCode: "GB",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: false, nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .premierInn)
        XCTAssertFalse(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section shouldn't be displayed for guest from GB for a PI hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_PIBrandAndNationalityDE() {
        let country = CountryItem(country: Country(code: "DE",
                                                   name: "",
                                                   isoCode: "DE",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: true, nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .premierInn)
        XCTAssertTrue(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section should be displayed for guest from GB for a PI hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_PIGermanyBrandAndNationalityGB() {
        let country = CountryItem(country: Country(code: "GB",
                                                   name: "",
                                                   isoCode: "GB",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: false,
                                                   nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .premierInnGermany)
        XCTAssertTrue(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section should be displayed for guest from GB for a PI Germany hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_PIGermanyBrandAndNationalityDE() {
        let country = CountryItem(country: Country(code: "DE",
                                                   name: "",
                                                   isoCode: "DE",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: true,
                                                   nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .premierInnGermany)
        XCTAssertFalse(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section shouldn't be displayed for guest from DE for a PI Germany hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_ZipBrandAndNationalityGB() {
        let country = CountryItem(country: Country(code: "GB",
                                                   name: "",
                                                   isoCode: "GB",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: false,
                                                   nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .zip)
        XCTAssertFalse(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section shouldn't be displayed for guest from GB for a Zip hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_ZipBrandAndNationalityDe() {
        let country = CountryItem(country: Country(code: "DE",
                                                   name: "",
                                                   isoCode: "DE",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: true,
                                                   nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .zip)
        XCTAssertTrue(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section should be displayed for guest from DE for a Zip hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_HubAndNationalityGB() {
        let country = CountryItem(country: Country(code: "GB",
                                                   name: "",
                                                   isoCode: "GB",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: false,
                                                   nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .hub)
        XCTAssertFalse(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section shouldn't be displayed for guest from GB for a hub hotel")
    }

    func test_shouldShowIdentificationDocumentsSection_HubAndNationalityDE() {
        let country = CountryItem(country: Country(code: "DE",
                                                   name: "",
                                                   isoCode: "DE",
                                                   dialingCode: "",
                                                   flagImage: "",
                                                   passportRequired: true,
                                                   nationality: nil))
        let editDetailsModel = EditDetailsModel(flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
                                                title: "",
                                                firstName: "",
                                                lastName: "",
                                                country: country,
                                                passportNumber: "",
                                                hotelBrand: .hub)
        XCTAssertTrue(editDetailsModel.shouldShowIdentificationDocumentsSection, "Identification Documents section should be displayed for guest from DE for a hub hotel")
    }

    func test_didTapPostcodeSearch() {
        let postcode = "ABC 123"
        mockEventHandler?.didTapPostcodeSearch(with: postcode)
        XCTAssertTrue(mockEventHandler.didCallTapPostcodeSearch)
        XCTAssertEqual(postcode, mockEventHandler.postcodeSearchValue)
    }

    func test_didDismissPostcodeSearch() {
        mockEventHandler?.didDismissPostcodeSearch()
        XCTAssertTrue(mockEventHandler.didCallDismissPostcodeSearch)
    }

    func test_didSelectAddress() {
        let address = try? Address(dictionary: nil)
        mockEventHandler?.didSelectAddress(address)
        XCTAssertTrue(mockEventHandler.didCallSelectAddress)
    }

    func testFindAddressButtonShouldBeHiddenForGermany() {
        let address = try! Address(
            dictionary: [
                "addressline1": "Address Line 1",
                "addressline2": "",
                "addressline3": "London",
                "countryCode": "DE",
                "postcode": "12345"
            ]
        )
        let addressModel = StoredAddressModel(with: address)

        let country = CountryItem(
            country: Country(
                code: "DE",
                name: "",
                isoCode: "DE",
                dialingCode: "",
                flagImage: "",
                passportRequired: true,
                nationality: nil
            )
        )
        let editDetailsModel = EditDetailsModel(
            flow: .regCard(index: 0),
            title: "Mr",
            firstName: "John",
            lastName: "Smith",
            country: country,
            passportNumber: "",
            hotelBrand: .premierInn,
            address: addressModel
        )

        XCTAssertFalse(editDetailsModel.showFindAddressButton)
    }

    func testFindAddressButtonShouldBeHiddenForGreatBritainIfInRegCardFlow() {
        let mockAddress = try! Address(
            dictionary: [
                "addressline1": "Royal Victoria Dock",
                "addressline2": "2 Festoon Way",
                "addressline3": "London",
                "countryCode": "GB",
                "postcode": "E16 1SJ"
            ]
        )

        let country = CountryItem(
            country: Country(
                code: "GB",
                name: "",
                isoCode: "GB",
                dialingCode: "",
                flagImage: "",
                passportRequired: true,
                nationality: nil
            )
        )
        let editDetailsModel = EditDetailsModel(
            flow: .regCard(index: 0),
            title: "Mr",
            firstName: "John",
            lastName: "Smith",
            country: country,
            passportNumber: "",
            hotelBrand: .premierInn,
            address: StoredAddressModel(with: mockAddress)
        )

        XCTAssertFalse(editDetailsModel.showFindAddressButton)
    }

    // MARK: - Nationality / Country nil tests

    func testShouldShowIdentificationDocumentsSectionNilCountry() {
        let editDetailsModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "",
            firstName: "",
            lastName: "",
            country: nil,
            passportNumber: "",
            hotelBrand: .premierInn
        )
        XCTAssertFalse(
            editDetailsModel.shouldShowIdentificationDocumentsSection,
            "Identification Documents section shouldn't be displayed when country is nil for a PI hotel"
        )
    }

    func testShouldHideIdentificationDocumentsSectionNilCountryPIGermany() {
        let editDetailsModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "",
            firstName: "",
            lastName: "",
            country: nil,
            passportNumber: "",
            hotelBrand: .premierInnGermany
        )
        XCTAssertFalse(
            editDetailsModel.shouldShowIdentificationDocumentsSection,
            "Identification Documents section should be displayed when country is nil for a PI Germany hotel"
        )
    }

    func testNationalityRowDisplaysPrePopulatedNationality() {
        let country = CountryItem(country: Country(
            code: "FR",
            name: "France",
            isoCode: "FR",
            dialingCode: "+33",
            flagImage: "",
            passportRequired: true,
            nationality: "French"
        ))
        let editDetailsModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "Mr",
            firstName: "Jean",
            lastName: "Dupont",
            country: country,
            passportNumber: "AB123456",
            hotelBrand: .premierInn
        )
        XCTAssertEqual(editDetailsModel.country?.nationality, "🇫🇷 French")
        XCTAssertNotNil(editDetailsModel.country)
    }

    func testNationalityRowNilWhenCountryIsNil() {
        let editDetailsModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "",
            firstName: "",
            lastName: "",
            country: nil,
            passportNumber: nil,
            hotelBrand: .premierInn
        )
        XCTAssertNil(editDetailsModel.country)
        XCTAssertNil(editDetailsModel.country?.nationality)
    }

    func testUpdateEditDetailsModelWithPrePopulatedCountry() {
        let country = CountryItem(country: Country(
            code: "DE",
            name: "Germany",
            isoCode: "DE",
            dialingCode: "+49",
            flagImage: "",
            passportRequired: true,
            nationality: "German"
        ))
        let editDetailsModel = EditDetailsModel(
            flow: .regCard(index: 0),
            title: "Ms",
            firstName: "Anna",
            lastName: "Müller",
            country: country,
            passportNumber: "DE987654",
            hotelBrand: .premierInn
        )
        mockEventHandler.updateEditDetailsModel(with: editDetailsModel)

        XCTAssertTrue(mockEventHandler.updateEditDetailsModel)
        XCTAssertEqual(editDetailsModel.country?.country.isoCode, "DE")
        XCTAssertEqual(editDetailsModel.country?.country.name, "Germany")
    }

    func testEditDetailsModelPrePopulatedFieldsAreRetained() {
        let country = CountryItem(country: Country(
            code: "GB",
            name: "United Kingdom",
            isoCode: "GB",
            dialingCode: "+44",
            flagImage: "",
            passportRequired: false,
            nationality: "British"
        ))
        let editDetailsModel = EditDetailsModel(
            flow: .regCard(index: 0),
            title: "Dr",
            firstName: "Jane",
            lastName: "Smith",
            country: country,
            passportNumber: "GB111222",
            hotelBrand: .premierInn
        )

        XCTAssertEqual(editDetailsModel.title, "Dr")
        XCTAssertEqual(editDetailsModel.firstName, "Jane")
        XCTAssertEqual(editDetailsModel.lastName, "Smith")
        XCTAssertEqual(editDetailsModel.country?.country.code, "GB")
        XCTAssertEqual(editDetailsModel.passportNumber, "GB111222")
    }

    func testShowFindAddressButtonHiddenWhenCountryIsNil() {
        let editDetailsModel = EditDetailsModel(
            flow: .regCard(index: 0),
            title: "Mr",
            firstName: "John",
            lastName: "Doe",
            country: nil,
            passportNumber: nil,
            hotelBrand: .premierInn,
            address: nil
        )
        XCTAssertFalse(
            editDetailsModel.showFindAddressButton,
            "Find Address button should be hidden when country is nil"
        )
    }

    func testShouldShowIdentificationDocumentsSectionNilPassportNumberWithPassportRequiredCountry() {
        let country = CountryItem(country: Country(
            code: "IT",
            name: "Italy",
            isoCode: "IT",
            dialingCode: "+39",
            flagImage: "",
            passportRequired: true,
            nationality: "Italian"
        ))
        let editDetailsModel = EditDetailsModel(
            flow: .leadGuestTitleNameInfo(indexPath: IndexPath(row: 0, section: 0)),
            title: "",
            firstName: "",
            lastName: "",
            country: country,
            passportNumber: nil,
            hotelBrand: .premierInn
        )
        XCTAssertTrue(
            editDetailsModel.shouldShowIdentificationDocumentsSection,
            "Identification Documents section should be displayed for passport-required country with nil passport number"
        )
    }
}
