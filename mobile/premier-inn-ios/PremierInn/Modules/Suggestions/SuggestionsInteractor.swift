//
//  SuggestionsInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 12/12/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import SimpleNetwork
import CoreLocation

class SuggestionsInteractor {
	private let requestsManager = RequestsManager()
    private let coordinatesProvider = GoogleAPIDataProvider.sharedInstance
}

protocol UpdateCoordinatesDataProvider {
    func updateCoordinates(
    	for identifier: String,
    	completion: @escaping (_ coordinate: CLLocationCoordinate2D?, _ success: Bool) -> Void
    )
}

extension GoogleAPIDataProvider: UpdateCoordinatesDataProvider {}

extension SuggestionsInteractor: SuggestionsInteractorProtocol {
	func cancelConnections() {
		requestsManager.cancelConnections()
	}

	func clearRecenteSearches() {
		let recentSearchesManager = RecentSuggestionsManager(dataSource: UserDefaults.standard)
		recentSearchesManager.reset(userDefaults: UserDefaults.standard)
	}

	func saveSuggestionToRecentSearches(_ suggestion: Suggestion) {
		let recentSearchesManager = RecentSuggestionsManager(dataSource: UserDefaults.standard)
		recentSearchesManager.add(suggestion)
	}

	func loadLocalSuggestions(completion: @escaping ([Suggestion]?, SuggestionsSource) -> Void) {
		let recentSearchesManager = RecentSuggestionsManager(dataSource: UserDefaults.standard)

		if recentSearchesManager.results.isNotEmpty {
			completion(recentSearchesManager.results, .history)
		} else {
			completion(SettingsManager.sharedInstance.topDestinations, .topDestinations)
		}
	}

	func loadRemoteSuggestions(
		searchTerm: String,
		completion: @escaping (_ suggestions: [PISuggestion]?, _ error: Error?) -> Void
	) {
		requestsManager.loadRemoteSuggestions(searchTerm: searchTerm, completion: completion)
	}

    func updateCoordinates(
    	for googlePlacesSuggestion: Suggestion,
    	completion: @escaping (_ suggestion: Suggestion?, _ success: Bool) -> Void
    ) {
        guard let identifier = googlePlacesSuggestion.identifier else {
            completion(nil, false)
            return
        }

        coordinatesProvider.updateCoordinates(for: identifier) { coordinate, success in
            guard success, let coordinate = coordinate else { return completion(nil, false) }

            (googlePlacesSuggestion as? PISuggestion)?.coordinate = coordinate
            self.saveSuggestionToRecentSearches(googlePlacesSuggestion)

            completion(googlePlacesSuggestion, true)
        }
    }
}
