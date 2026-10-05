//
//  PostCodeLookupViewModel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import Formeka
import SimpleNetwork

struct AddressSection: ListSection {
    private var addresses: [AddressSummary]

    var items: [ListItem] {
        addresses.compactMap { AddressItem(addressSummary: $0) }
    }

    init(addresses: [AddressSummary]) {
        self.addresses = addresses
    }
}

struct AddressItem: ListItem {
    private let requestsManager = RequestsManager()

    private var addressSummary: AddressSummary

    var title: String { addressSummary.description }

    // this should not be used but is implemented to conform to the ListItem protocol
    var associatedObject: Any { addressSummary }

    init(addressSummary: AddressSummary) {
        self.addressSummary = addressSummary
    }

    public func getAddressDetails(completion: @escaping (_ address: Address?) -> Void) {
        do {
            try Validator.ukPostCode.validate(value: addressSummary.searchedPostCode)

            requestsManager.addressLookup(postCode: addressSummary.searchedPostCode, id: addressSummary.id) { address, _ in
                guard let address = address else {
                    return completion(nil)
                }

                return completion(address)
            }
        } catch {
            completion(nil)
        }
    }
}

class PostCodeLookupViewModel: ListViewModel {
    private let requestsManager = RequestsManager()

    internal var sections: [ListSection]?

    var mainSectionTitle: String? { PILocalizedString("postcodeLookupScreenTitle", comment: "Postcode lookup screen title") }
    var noItemsErrorMessage: String? { PILocalizedString(
        "postcodeLookupErrorMessage",
        comment: "Postcode lookup error message"
    ) }
    var trackable: Bool? { false }
    var accessibilityPrefix: String?
    var showsSearchBar: Bool = true
    var addressType: AddressType = .home

    func items(forSection: Int) -> [ListItem] {
        guard let sections = sections, let firstSection = sections.first else { return [] }

        return firstSection.items
    }

    func item(at indexPath: IndexPath) -> ListItem? {
        guard let sections = sections, let firstSection = sections.first else { return nil }
        guard indexPath.row < firstSection.items.count else { return nil }

        return firstSection.items[indexPath.row]
    }

    func preloadData(completion: @escaping () -> Void) {
        // Don't need to preload any data here
        completion()
    }

    func selectedItemObject(selectedItem: ListItem?, completion: @escaping (_ object: Any?) -> Void) {
        let selectedItem = selectedItem as? AddressItem

        selectedItem?.getAddressDetails { address in
            completion(address)
        }
    }

    func search(text: String, completion: @escaping (_ shouldShowError: Bool) -> Void) {
        do {
			try Validator.ukPostCode.validate(value: text)

            requestsManager.addressLookup(postCode: text) { addresses, _ in
                guard let addresses = addresses else {
                    self.sections = nil
                    return completion(true)
                }

                self.sections = [AddressSection(addresses: addresses)]
                let shouldShowError = addresses.isNotEmpty ? false : true

                return completion(shouldShowError)
            }
        } catch {
            completion(false)
        }
    }
}
