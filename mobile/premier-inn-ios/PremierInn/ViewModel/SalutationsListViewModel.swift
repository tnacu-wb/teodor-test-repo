//
//  SalutationsListViewModel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 27/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation

struct SalutationSection: ListSection {
    private var salutations: [String]

    var items: [ListItem] {
        // Convert salutations Array to an ordered set so if German repeat titles are filtered out
        guard let uniqueSalutations = Array(NSOrderedSet(array: salutations)) as? [String] else { return [] }

        return uniqueSalutations.compactMap { SalutationItem(salutation: $0) }
    }

    init(salutations: [String]) {
        self.salutations = salutations
    }
}

struct SalutationItem: ListItem {
    let title: String
    var associatedObject: Any { title }

    init(salutation: String) {
        self.title = salutation
    }
}

class SalutationsListViewModel: ListViewModel {
    var mainSectionTitle: String? { PILocalizedString("salutationSelectTitle", comment: "Salutation section title") }
    var noItemsErrorMessage: String? { nil }
    var trackable: Bool? { false }
    var accessibilityPrefix: String? = AccessibilityIdentifiers.TitleList.titleListPrefixFormat
    var showsSearchBar: Bool = false

    internal var salutations: [String]
    internal var sections: [ListSection]?

    init(salutations: [String]) {
        self.salutations = salutations
        self.sections = [SalutationSection(salutations: salutations)]
    }

    func items(forSection: Int) -> [ListItem] {
        guard let sections = sections, forSection < sections.count else { return [] }

        return sections[forSection].items
    }

    func preloadData(completion: @escaping () -> Void) {
        completion()
    }

    func item(at indexPath: IndexPath) -> ListItem? {
        let items = self.items(forSection: indexPath.section)

        guard indexPath.row < items.count else { return nil }

        return items[indexPath.row]
    }

    func search(text: String, completion: @escaping (_ shouldShowError: Bool) -> Void) {
        if text.isEmpty {
            sections = [SalutationSection(salutations: salutations)]
        } else {
            let filteredSalutations = salutations.filter { $0.uppercased().contains(text.uppercased()) }

            let sortedMatches = filteredSalutations
                .sorted(by: {
                ($0.uppercased().range(of: text.uppercased())?.lowerBound)! <
                ($1.uppercased().range(of: text.uppercased())?.lowerBound)! })

            sections = [SalutationSection(salutations: sortedMatches)]
        }

        completion(false)
    }
}

class BusinessQuestionAnswersListViewModel: SalutationsListViewModel {
    private var dynamicSectionTitle: String?

    override var mainSectionTitle: String? { dynamicSectionTitle ?? PILocalizedString("Please select one") }

    init(answers: [String], mainSectionTitle: String? = nil) {
        super.init(salutations: answers)
        self.dynamicSectionTitle = mainSectionTitle
    }
}
