//
//  KeyMessagingVIew.swift
//  PremierInn
//
//  Created by Louis Faria-Softly on 22/08/2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//
import UIKit

class KeyMessagingView: UIViewController, KeyMessagingViewProtocol {
    var presenter: KeyMessagingPresenterProtocol?

    @IBOutlet weak var messagingTitleLabel: UILabel! {
        didSet {
            self.messagingTitleLabel.font = .Heading1_ExtraBold(29)
            self.messagingTitleLabel.textColor = .BaseWhite
        }
    }

    @IBOutlet weak var messagingDescriptionLabel: UILabel! {
        didSet {
            self.messagingDescriptionLabel.font = .Heading4_Bold()
            self.messagingDescriptionLabel.textColor = .BaseWhite
        }
    }

    @IBOutlet weak var directionsButton: UIButton! {
        didSet {
            self.directionsButton.setTitle(
                PILocalizedString("planYourTripGetDirectionsButtonTitle"),
                for: .normal
            )
            self.directionsButton.setTitleColor(.TintD1, for: .normal)
            self.directionsButton.titleLabel?.font = .Heading4_Semibold()
            self.directionsButton.tintColor = .TintD1
            self.directionsButton.backgroundColor = .white
            self.directionsButton.layer.cornerRadius = 4
            self.directionsButton.heightAnchor.constraint(equalToConstant: 50).isActive = true
            self.directionsButton.widthAnchor.constraint(equalToConstant: 320).isActive = true
        }
    }

    init() {
        super.init(nibName: String(describing: KeyMessagingView.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
          fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        presenter?.viewDidLoad()
    }

    func updateView(with viewModel: KeyMessagingViewModel) {
        self.messagingTitleLabel.text = viewModel.title
        self.messagingDescriptionLabel.text = viewModel.messaging
        self.directionsButton.isHidden = !viewModel.shouldShowDirectionsButton
    }

    @IBAction func directionsButtonClicked(_ sender: Any) {
        presenter?.mapButtonDidClick()
    }

    func showMapDirections(directionsViewModel: DirectionsViewModel) {
        self.showMapsDirectionsOptions(with: directionsViewModel)
    }
}
