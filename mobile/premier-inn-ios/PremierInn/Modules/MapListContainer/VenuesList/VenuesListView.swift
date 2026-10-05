//
//  VenuesListView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import SwiftUI

// MARK: - ListPresenterProtocol

protocol ListPresenterProtocol: VenueFullyBookedCellDelegate {
    var shouldShowCoronavirusInformationBanner: Bool { get }

	func listViewIsReady()
	func numberOfVenues() -> Int
	func hotel(for indexPath: IndexPath) -> Hotel?
	func didSelectItem(at indexPath: IndexPath, mapVisible: Bool)
	func listDidScroll(to indexPath: IndexPath)
	func listDidScroll(to y: CGFloat)
    func didTapMapArea()

    func didStartScrolling()
    func didTapDismissCoronavirusInformationBanner()
}

// MARK: - VenuesListViewController

final class VenuesListViewController: UIViewController {
	var presenter: ListPresenterProtocol?

    private let contentInsetTop = UIDevice.current.userInterfaceIdiom == .pad ? 0 : Constants.smallMapHeight + Constants
        .optionsViewHeight
    private var collectionViewBackgroundView: UIView?
	private static let errorViewTag = 666
	private var mapButton: UIButton?
    private var coronavirusInformationBanner: UIView?

	var siteWidePromotionViewModel: StickyFooterViewModel? {
		guard let siteWidePromotion = SettingsManager.sharedInstance.siteWidePromotionContent,
		      let title = siteWidePromotion.title,
		      let subtitle = siteWidePromotion.subtitle,
		      title.isNotEmpty,
		      subtitle.isNotEmpty,
		      !SettingsManager.sharedInstance.isAppIncentiveAvailable else {
			return nil
		}

		var attributedDescription = AttributedString(subtitle)
		attributedDescription.font = UIFont.BodySmall()

		return SiteWidePromotionViewModel(
		    delegate: self,
		    title: title,
		    description: attributedDescription,
		    urlString: siteWidePromotion.urlString
		)
	}

	var freeBreakfastPromotionViewModel: StickyFooterViewModel? {
		if SettingsManager.sharedInstance.isFreeBreakfastPromotionAvailable && !SettingsManager.sharedInstance
		   .isAppIncentiveAvailable {
			FreeBreakfastViewModel(delegate: self)
		} else { nil }
	}

	var shouldHideStickyBanner: Bool {
		SettingsManager.sharedInstance.isAppIncentiveAvailable == false
		&& SettingsManager.sharedInstance.isFreeBreakfastPromotionAvailable == false
		&& siteWidePromotionViewModel == nil
	}

    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            collectionView.registerCellForNib(with: VenueCell.self)
            collectionView.registerCellForNib(with: VenueFullyBookedCell.self)
            collectionView.registerSupplementaryViewNib(
                with: GdprShieldFooterView.self,
                kind: UICollectionView.elementKindSectionFooter
            )
            collectionView.register(
                UICollectionViewCell.self,
                forCellWithReuseIdentifier: VenueCellModule.cellReuseIdentifier
            )
            collectionView.register(
                UICollectionViewCell.self,
                forCellWithReuseIdentifier: VenueFullyBookedCellModule.cellReuseIdentifier
            )

            collectionView.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.venuesList

			collectionView.contentInset.top = contentInsetTop
            collectionView.verticalScrollIndicatorInsets.top = contentInsetTop
        }
    }

    @IBOutlet weak var stickyFooter: UIView! {
        didSet {
            reloadStickyBanner()
        }
    }

    deinit {
        print("DEINIT: \(self)")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .clear
        collectionView.backgroundColor = .clear
		collectionView.backgroundView = movingBackgroundView()

        collectionView.setCollectionViewLayout(
            UICollectionViewLayout.verticalDynamicLayout(
                footerHeight: 180 // This is hardcoded to align with previous UIKit footer implementation
            ),
            animated: false
        )

        mapButton = UIButton(frame: CGRect(
            x: 0,
            y: -contentInsetTop,
            width: collectionView.frame.width,
            height: contentInsetTop
        ))
        mapButton?.addTarget(self, action: #selector(mapAreaButtonDidTap), for: .touchUpInside)
		if let button = mapButton {
			collectionView.addSubview(button)
		}

        presenter?.listViewIsReady()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        // for some reason on iOS 10.0 (iPad) the search results are not visible until we do this..
        if let version = Double(UIDevice.current.systemVersion), version < 12.0,
           UIDevice.current.userInterfaceIdiom == .pad {
            self.reload()
        }

        updateCoronavirusBanner()
    }

    private func updateCoronavirusBanner() {
        if presenter?.shouldShowCoronavirusInformationBanner ?? false {
            if coronavirusInformationBanner != nil {
                coronavirusInformationBanner?.isHidden = false
            } else {
                let view: AlertMessageCell? = AlertMessageCell.fromNib()

                view?.update(using: collectionView.frame.width)
                view?.alertMessageCellInputDelegate = self

                if let coronavirusView = view {
                    coronavirusInformationBanner = coronavirusView
                    self.collectionView.addSubview(coronavirusView)
                }
            }
        } else {
            coronavirusInformationBanner?.isHidden = true
        }
    }

    @objc func mapAreaButtonDidTap() {
        presenter?.didTapMapArea()
    }

    private func movingBackgroundView() -> UIView {
        let view = UIView(frame: collectionView.bounds)
        view.backgroundColor = .clear

        collectionViewBackgroundView = UIView(
            frame: CGRect(
                x: 0,
                y: contentInsetTop,
                width: collectionView.bounds.width,
                height: collectionView.bounds.height
            )
        )

        collectionViewBackgroundView?.backgroundColor = .BaseGrey
        collectionViewBackgroundView?.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        if let collectionViewBackgroundView {
            view.addSubview(collectionViewBackgroundView)
        }

        return view
    }

    private func adjustBackgroundView(_ scrollView: UIScrollView) {
        guard let backgroundView = collectionViewBackgroundView else { return }

        backgroundView.frame.origin.y = -scrollView.contentOffset.y
        backgroundView.frame.size.height = collectionView.frame.height + scrollView.contentOffset.y
    }

    private func reloadStickyBanner() {
        stickyFooter.subviews.forEach({ $0.removeFromSuperview() })

        var stickyViewModel: StickyFooterViewModel = AppIncentiveStickyFooterViewModel(delegate: self)

        if let siteWidePromotionViewModel {
            stickyViewModel = siteWidePromotionViewModel
        } else if let freeBreakfastPromotionViewModel {
            stickyViewModel = freeBreakfastPromotionViewModel
        }

        stickyFooter.isHidden = shouldHideStickyBanner

        stickyFooter.backgroundColor = .BasePurple

        // add stickyFooter method
        let childView = UIHostingController(rootView: StickyFooter(viewModel: stickyViewModel))
        addChild(childView)
        childView.view.frame = stickyFooter.bounds
        childView.view.backgroundColor = .BasePurple
        stickyFooter.addSubview(childView.view)

        let constraints = [
            childView.view.leadingAnchor.constraint(equalTo: stickyFooter.leadingAnchor),
            childView.view.trailingAnchor.constraint(equalTo: stickyFooter.trailingAnchor),
            childView.view.topAnchor.constraint(equalTo: stickyFooter.topAnchor),
            childView.view.bottomAnchor.constraint(equalTo: stickyFooter.bottomAnchor)
        ]

        NSLayoutConstraint.activate(constraints)

        childView.didMove(toParent: self)
    }
}

// MARK: - AlertMessageCellInputDelegate

extension VenuesListViewController: AlertMessageCellInputDelegate {
    func dismissButtonTapped(withSenderView senderView: AlertMessageCell?) {
        guard let senderView = senderView else { return }

        senderView.dismissButton.alpha = 0

        UIView.animate(
            withDuration: .ocd,
            delay: 0,
            usingSpringWithDamping: 0.7,
            initialSpringVelocity: 0.9,
            options: [.curveEaseOut],
            animations: {
                senderView.frame.size.height = 0
            },
            completion: { _ in
            senderView.removeFromSuperview()
        }
        )

        presenter?.didTapDismissCoronavirusInformationBanner()
    }
}

// MARK: - ListViewProtocol

extension VenuesListViewController: ListViewProtocol {
	func reload() {
		if collectionView != nil {
            collectionView.isScrollEnabled = false

            collectionView.reloadData()

            collectionView.collectionViewLayout.invalidateLayout()

            collectionView.isScrollEnabled = true
		}

        reloadStickyBanner()
	}

	func mapWillGoFullScreen() {
	}

	func mapDidGoFullScreen() {
	}

    func scrollToItemAt(indexPath: IndexPath, andSelect selectCell: Bool?) {
        guard collectionView != nil else { return }

        collectionView.scrollToItem(at: indexPath, at: .top, animated: false)
	}

	func willReturnToListView() {
	}

	func selectCellAt(indexPath: IndexPath) {
	}

	func showErrorMessage(message: String?, bottomConstraint: CGFloat) {
        guard collectionView != nil else { return }
		guard let errorView: SimpleErrorView = SimpleErrorView.fromNib() else { return }

		errorView.translatesAutoresizingMaskIntoConstraints = true
		errorView.autoresizingMask = [.flexibleWidth]
		errorView.tag = VenuesListViewController.errorViewTag
		errorView.frame.size.width = collectionView.frame.width
		errorView.errorLabel.text = message
		errorView.frame.origin.x = 0

        errorView.errorLabel.textColor = .TintD1
        errorView.errorImageView.image = NotificationStyle.alert.icon
        errorView.errorImageView.tintColor = NotificationStyle.alert.tint
        errorView.errorView.backgroundColor = NotificationStyle.alert.background
        errorView.errorView.layer.borderColor = NotificationStyle.alert.tint.withAlphaComponent(0.4).cgColor
        errorView.errorView.layer.cornerRadius = 3

        // force the errorView and its subviews to layout and re-calculate their constraints
        errorView.setNeedsLayout()
        errorView.layoutIfNeeded()

        errorView.frame.size.height = (errorView.errorLabel.sizeThatFits(CGSize(
            width: errorView.errorLabel.frame.size.width,
            height: CGFloat.greatestFiniteMagnitude
        )).height + (errorView.errorView.frame.origin.y + (errorView.errorLabel.frame.origin.y * 2)) + bottomConstraint)

		errorView.bottomConstraint.constant = bottomConstraint
        errorView.frame.origin.y = -errorView.frame.size.height

        // show as warning instead of error
        if let message = message, message.contains(PILocalizedString("hubFamilyRoomNotAvailableMessage", comment: "")) {
            errorView.errorImageView.image = NotificationStyle.info.icon
            errorView.errorImageView.tintColor = NotificationStyle.info.tint
            errorView.errorView.backgroundColor = NotificationStyle.info.background
            errorView.errorView.layer.borderColor = NotificationStyle.info.tint.withAlphaComponent(0.4).cgColor
        }

        errorView.accessibilityIdentifier = {
            if let message = message, message.contains(PILocalizedString("no hotels", comment: "")) {
                return AccessibilityIdentifiers.SearchResults.noAvailabilitiesForHotelSearchMessage
            } else if let message = message, message.contains(PILocalizedString(
                "hubFamilyRoomNotAvailableMessage",
                comment: ""
            )) {
                return AccessibilityIdentifiers.SearchResults.hubFamilyRoomNotAvailableMessage
            }
            return AccessibilityIdentifiers.SearchResults.noAvailabilityForHotelSearchMessage
        }()

		collectionView.addSubview(errorView)

		collectionView.contentInset.top += errorView.frame.height
		mapButton?.frame.origin.y -= errorView.frame.height

        // re-position the view to its initial state if there are hotel results
        guard collectionView.numberOfItems(inSection: 0) > 0 else { return }

        collectionView.scrollToItem(at: IndexPath(row: 0, section: 0), at: [], animated: true)
	}

	func removeErrorMessage() {
        guard collectionView != nil else { return }
        guard let errorView = collectionView.viewWithTag(VenuesListViewController.errorViewTag) else { return }

		collectionView.contentInset.top -= errorView.frame.height
		mapButton?.frame.origin.y += errorView.frame.height
		errorView.removeFromSuperview()
	}

    func selectedListCell() -> VenueCell? {
        guard let indexPath = collectionView.indexPathsForSelectedItems?.first else { return nil }

        return collectionView.cellForItem(at: indexPath) as? VenueCell
    }
}

// MARK: - UICollectionViewDelegate, UICollectionViewDataSource

extension VenuesListViewController: UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        presenter?.numberOfVenues() ?? 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let hotel = presenter?.hotel(for: indexPath) else {
            assertionFailure("Missing hotel for indexPath: \(indexPath)")

            return collectionView.dequeueReusableCell(
                withReuseIdentifier: VenueCellModule.cellReuseIdentifier,
                for: indexPath
            )
        }

        return buildVenueCell(
            collectionView: collectionView,
            indexPath: indexPath,
            hotel: hotel
        )
    }

    func collectionView(
        _ collectionView: UICollectionView,
        viewForSupplementaryElementOfKind kind: String,
        at indexPath: IndexPath
    ) -> UICollectionReusableView {
        switch kind {
        case UICollectionView.elementKindSectionFooter:
            guard let view = collectionView.dequeueReusableSupplementaryView(
                ofKind: UICollectionView.elementKindSectionFooter,
                withReuseIdentifier: String(describing: GdprShieldFooterView.self),
                for: indexPath
            ) as? GdprShieldFooterView else { return UICollectionReusableView() }
            view.delegate = self
            view.button.accessibilityIdentifier = AccessibilityIdentifiers.SearchResults.dataPolicyLink

            return view

        default:
            return UICollectionReusableView()
        }
    }

    func collectionView(
        _ collectionView: UICollectionView,
        willDisplaySupplementaryView view: UICollectionReusableView,
        forElementKind elementKind: String,
        at indexPath: IndexPath
    ) {
    }

    func collectionView(
        _ collectionView: UICollectionView,
        willDisplay cell: UICollectionViewCell,
        forItemAt indexPath: IndexPath
    ) {
        /* Empty implementation */
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if UIDevice.current.userInterfaceIdiom == .pad {
            UIView.animate(
                withDuration: .ocd,
                animations: {
                    self.view.superview?.frame.origin.y = self.view.superview?.frame.size.height ?? UIScreen.main.bounds.size
                        .height
                    self.view.superview?.alpha = 0.2
                },
                completion: { _ in
                self.presenter?.didSelectItem(at: indexPath, mapVisible: false)
            }
            )
        } else {
            didSelectItemAt(indexPath)
        }
    }

    private func didSelectItemAt(_ indexPath: IndexPath) {
        presenter?.didSelectItem(at: indexPath, mapVisible: false)
        NotificationFeedbackManager.shared.provideLightTapFeedback()
    }
}

// MARK: - UIScrollViewDelegate

extension VenuesListViewController: UIScrollViewDelegate {
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        presenter?.didStartScrolling()
    }

    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        adjustBackgroundView(scrollView)

		presenter?.listDidScroll(to: scrollView.contentOffset.y + scrollView.contentInset.top)
    }
}

// MARK: - GdprShieldFooterViewDelegate

extension VenuesListViewController: GdprShieldFooterViewDelegate {
    func gdprButtonDidTap() {
        openPrivacyPolicyExternalLink()
    }
}

// MARK: - buildVenueCell

private extension VenuesListViewController {
    func buildVenueCell(
        collectionView: UICollectionView,
        indexPath: IndexPath,
        hotel: Hotel
    ) -> UICollectionViewCell {
        hotel.available
        ? availableVenueCell(
            collectionView: collectionView,
            indexPath: indexPath,
            hotel: hotel
          )
        : fullyBookedVenueCell(
            collectionView: collectionView,
            indexPath: indexPath,
            hotel: hotel
          )
    }

    func availableVenueCell(
        collectionView: UICollectionView,
        indexPath: IndexPath,
        hotel: Hotel
    ) -> UICollectionViewCell {
        VenueCellModule.build(
            collectionView: collectionView,
            indexPath: indexPath,
            dataProvider: hotel
        ) { [weak self] in
            self?.didSelectItemAt(indexPath)
        }
    }

    func fullyBookedVenueCell(
        collectionView: UICollectionView,
        indexPath: IndexPath,
        hotel: Hotel
    ) -> UICollectionViewCell {
        VenueFullyBookedCellModule.build(
            collectionView: collectionView,
            indexPath: indexPath,
            dataProvider: hotel
        ) { [weak self] in
            self?.presenter?.venueCellDidTapEditButton()
        }
    }
}
