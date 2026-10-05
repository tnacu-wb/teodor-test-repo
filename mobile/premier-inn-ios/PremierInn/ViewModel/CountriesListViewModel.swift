//
//  CountriesListViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 04/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import Foundation
import UIKit
import SimpleNetwork

struct CountrySection: ListSection {
    private var countries: [Country?]
    private let source: ListViewSource

    var items: [ListItem] {
        countries.compactMap { CountryItem(country: $0, source: source) }
    }

    init(countries: [Country?], source: ListViewSource) {
        self.countries = countries
        self.source = source
    }
}

struct CountryItem: ListItem {
    private(set) var country: Country
    private let source: ListViewSource

    var title: String {
        switch source {
        case .countries: return country.displayName
        case .nationalities: return country.displayNationality
        }
    }
    var nationality: String { country.displayNationality }
    var associatedObject: Any { country }

    var isPassportRequired: Bool {
        country.passportRequired ?? false
    }

    var isGreatBritain: Bool {
        country.isoCode == "GB"
    }

    var isGerman: Bool {
        country.isoCode == "DE"
    }

    init?(country: Country?, source: ListViewSource = .countries) {
        guard let country = country else { return nil }

        self.country = country
        self.source = source
    }
}

final class CountriesListViewModel: ListViewModel {
    private let requestsManager = RequestsManager()
    private var suggestedCountries: [Country?] = [.greatBritain, .germany, .unitedStates, .unitedEmirates]
    private var countries: [Country]
    private let source: ListViewSource

    var mainSectionTitle: String? { PILocalizedString("countrySelectionTitle", comment: "Country selection title") }
    var noItemsErrorMessage: String? { nil }

    var trackable: Bool? { false }
    var accessibilityPrefix: String?
    var showsSearchBar: Bool = true

    init(
        countries: [Country],
        source: ListViewSource = .countries
    ) {
        self.countries = countries
        self.source = source
    }

    internal var sections: [ListSection]?

    func items(forSection: Int) -> [ListItem] {
        guard let sections = sections, forSection < sections.count else { return [] }

        return sections[forSection].items
    }

    func preloadData(completion: @escaping () -> Void) {
        if countries.isEmpty {
            Country.refreshCountries {
                self.countries = Country.countriesList
                self.suggestedCountries = [.greatBritain, .germany, .unitedStates, .unitedEmirates]
                self.dataLoaded(completion: completion)
            }
        } else {
            self.dataLoaded(completion: completion)
        }
    }

    private func dataLoaded(completion: @escaping () -> Void) {
        update(sectionsWithCountries: countries)

        completion()
    }

    func update(sectionsWithCountries countries: [Country]) {
        let sortedCountries = countries.sorted {
            $0.name.localizedCaseInsensitiveCompare($1.name) == .orderedAscending
        }

        let section1 = CountrySection(countries: suggestedCountries, source: source)
        let section2 = CountrySection(countries: sortedCountries, source: source)

        self.sections = [section1, section2]
    }

    func item(at indexPath: IndexPath) -> ListItem? {
        let items = self.items(forSection: indexPath.section)

        guard indexPath.row < items.count else { return nil }

        return items[indexPath.row]
    }

    func search(text: String, completion: @escaping (_ shouldShowError: Bool) -> Void) {
        if text.isEmpty {
            let section1 = CountrySection(countries: self.suggestedCountries, source: source)
            let section2 = CountrySection(countries: countries, source: source)

            self.sections = [section1, section2]
        } else {
            let filteredCountries = countries.filter { $0.name.uppercased().contains(text.uppercased()) }

            let sortedMatches = filteredCountries
                .sorted(by: {
                ($0.name.uppercased().range(of: text.uppercased())?.lowerBound)! <
                ($1.name.uppercased().range(of: text.uppercased())?.lowerBound)! })

            self.sections = [CountrySection(countries: sortedMatches, source: source)]
        }

        completion(false)
    }
}
