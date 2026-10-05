//
//  HomeSuggestionsViewModel.swift
//  PremierInn
//
//  Created by Freddie Parks on 16/01/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

extension HomeView {
    private static var suggestions: [PISuggestion] {
        SettingsManager.sharedInstance.topDestinations.compactMap { $0 as? PISuggestion }
    }
}

extension HomeView: UICollectionViewDelegate {
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let suggestion = HomeView.suggestions[indexPath.item]

        guard let presenter = eventHandler as? HomePresenterInput else { return }
        presenter.selected(suggestion: suggestion)

        eventHandler?.selectedSearch()
    }
}

extension HomeView: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        HomeView.suggestions.count
    }

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        1
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: String(describing: CuratedSuggestionCell.self),
            for: indexPath
        ) as? CuratedSuggestionCell else { return UICollectionViewCell() }
        let suggestion = HomeView.suggestions[indexPath.item]

        cell.photo.image = UIImage(named: getImageName(suggestion.title))
        cell.suggestionName.text = suggestion.title

        return cell
    }

    // Extract the image name from a string
    private func getImageName(_ text: String?) -> String {
        guard let text else { return ViewConstants.destinationFallbackImage }

        return text.toASCIIImageName()
    }
}

extension HomeView: UICollectionViewDelegateFlowLayout {
    func collectionView(
        _ collectionView: UICollectionView,
        layout collectionViewLayout: UICollectionViewLayout,
        sizeForItemAt indexPath: IndexPath
    ) -> CGSize {
        CGSize(width: 266, height: 218)
    }

    func collectionView(
        _ collectionView: UICollectionView,
        layout collectionViewLayout: UICollectionViewLayout,
        insetForSectionAt section: Int
    ) -> UIEdgeInsets {
        UIEdgeInsets(top: 19, left: 26, bottom: 19, right: 26)
    }

    func collectionView(
        _ collectionView: UICollectionView,
        layout collectionViewLayout: UICollectionViewLayout,
        minimumInteritemSpacingForSectionAt section: Int
    ) -> CGFloat {
        25
    }
}
