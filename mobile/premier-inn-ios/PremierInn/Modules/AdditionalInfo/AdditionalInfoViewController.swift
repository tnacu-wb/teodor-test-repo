//
//  AdditionalInfoViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 20/09/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class AdditionalInfoViewController: BaseViewController, UITableViewDelegate {
    var eventHandler: AdditionalInfoViewEventHandler?

    @IBOutlet weak var screenTitleLabel: UIPaddingLabel!

    private var dataSource: UITableViewDataSource?

    @IBOutlet var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 100.0
            table.contentInset.top = 20
        }
    }

    override var screenName: String { PIAnalytics.StateNames.hotelDetailsImportantInfo }
    override var screenType: String { PIAnalytics.StateTypes.bookingFlow }

    init() {
        super.init(nibName: String(describing: AdditionalInfoViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        table.delegate = self

        setupNavigationBar()
        eventHandler?.viewIsReady()
    }

    private func setupNavigationBar() {
        let closeModalImage = UIImage(named: "closeModal")

        let closeButton = UIBarButtonItem(
            image: closeModalImage,
            style: .plain,
            target: self,
            action: #selector(closeButtonDidTap)
        )
        closeButton.tintColor = .TintL1
        closeButton.accessibilityIdentifier = AccessibilityIdentifiers.AdditionalInfo.closeButton
        closeButton.accessibilityLabel = PILocalizedString("closeBarButtonTitle", comment: "Close bar button title")

        navigationItem.rightBarButtonItem = closeButton
    }

    @objc func closeButtonDidTap() {
        eventHandler?.closeButtonDidTap()
    }
}

extension AdditionalInfoViewController: AdditionalInfoViewProtocol {
    func update(with viewModel: AdditionalInfoViewModel) {
        table.registerCellNib(with: HotelTitleCell.self)

        switch viewModel.infoType {
        case .hotelNotes:
            configureTitle(
                text: PILocalizedString("importantHotelInfoCellTitle"),
                accessibilityIdentifier: AccessibilityIdentifiers.HotelDetails.importantHotelInfoButton,
                accessibilityTraits: .header
            )
            configureForNotes(with: viewModel.hotelNotes)
        case .hotelLocation:
            configureTitle(
                text: PILocalizedString("aboutThisHotel"),
                accessibilityIdentifier: AccessibilityIdentifiers.AdditionalInfo.aboutThisHotel,
                accessibilityTraits: .header
            )
            configureForLocation(with: viewModel)

        case .hotelParking:
            configureTitle(
                text: PILocalizedString("parkingAtThisHotel"),
                accessibilityIdentifier: AccessibilityIdentifiers.ParkingInfo.parkingInfoHeader,
                accessibilityTraits: .header
            )
            configureForParking(with: viewModel.parkingDetails)
        case .facilities:
            configureTitle(
                text: PILocalizedString("facilitiesButtonTitle"),
                accessibilityIdentifier: AccessibilityIdentifiers.AdditionalInfo.hotelFacilitiesTitle,
                accessibilityTraits: .header
            )
            configureForFacilities(with: viewModel)
        }
        table.dataSource = dataSource
        table.reloadData()
    }

    private func configureTitle(
        text: String,
        accessibilityIdentifier: String,
        accessibilityTraits: UIAccessibilityTraits? = nil
    ) {
        screenTitleLabel.setupLabel(
            text: text,
            font: .Heading2_Bold(),
            textColor: .TintD1,
            accessibilityIdentifier: accessibilityIdentifier,
            accessibilityTraits: accessibilityTraits
        )
    }

    private func configureForNotes(with hotelNotes: [String]?) {
        table.registerCellNib(with: HotelNotesCell.self)
        guard let notes = hotelNotes else { return }

        dataSource = HotelInfoViewModel(with: notes)
    }

    private func configureForLocation(with viewModel: AdditionalInfoViewModel) {
        table.registerCellNib(with: HotelDescriptionCell.self)
        table.registerCellNib(with: HotelDetailItemCell.self)

        guard let description = viewModel.hotelDescription, let directions = viewModel.hotelDirections else { return }

        dataSource = HotelLocationInfoViewModel(with: viewModel.hotelName, description: description, and: directions)
    }

    private func configureForParking(with parkingDetails: String?) {
        table.registerCellNib(with: HotelDetailItemCell.self)

        guard let parkingDetails = parkingDetails else { return }

        dataSource = HotelParkingInfoViewModel(parkingDetails: parkingDetails)
    }

    private func configureForFacilities(with viewModel: AdditionalInfoViewModel) {
        table.registerCellNib(with: HotelDetailSeparatorCell.self)
        table.registerCellNib(with: FlexibleTextContentCell.self)
        table.registerCellNib(with: ActionIconCell.self)

        guard let facilityTitle = viewModel.facilityTitle else { return }
        guard let facilityDescriptions = viewModel.facilityDescriptions else { return }
        guard let roomFeaturesTitle = viewModel.roomFeatureTitle else { return }
        guard let roomFeatureDescriptions = viewModel.roomFeatureDescriptions else { return }

        dataSource = HotelFacilitiesAndRoomFeaturesViewModel(
            withFacilitiesTitle: facilityTitle,
            facilityDescriptions: facilityDescriptions,
            roomFeaturesTitle: roomFeaturesTitle,
            andRoomFeatureDescriptions: roomFeatureDescriptions
        )
    }
}
