//
//  FullscreenImageSetViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 15/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

class FullscreenImageSetViewController: UIViewController {
    @IBOutlet private weak var photosView: PhotoPageContainerView!
    @IBOutlet weak var closeButon: UIButton! {
        didSet {
            closeButon.accessibilityIdentifier = AccessibilityIdentifiers.Shared.navigateUp
        }
    }

    var eventHandler: FullScreenImageViewerViewEventHandler?

    deinit {
        print("DEINIT: \(self)")
    }

    init() {
		super.init(nibName: String(describing: FullscreenImageSetViewController.self), bundle: nil)
	}

	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

    override func viewDidLoad() {
        super.viewDidLoad()

        photosView?.delegate = self

        eventHandler?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        SettingsManager.sharedInstance.supportedInterfaceOrientations = .all
    }

    override var prefersStatusBarHidden: Bool {
        true
    }



    override var shouldAutorotate: Bool {
        true
    }

    @IBAction func closeButtonDidTap(_ sender: AnyObject) {
        SettingsManager.sharedInstance.supportedInterfaceOrientations = .portrait

        eventHandler?.closeButtonDidTap()
    }
}

extension FullscreenImageSetViewController: PhotoPageContainerViewProtocol {
    func tappedPhotoContainer(atIndex index: Int) {
        print("Tap not handled in fullscreen")
    }

    func swipedPhotoContainer(atIndex index: Int) {
        eventHandler?.imageSetDidChangePicture(atIndex: index)
    }
}

extension FullscreenImageSetViewController: FullScreenImageViewerViewProtocol {
    func update(with viewModel: FullScreenImageViewerViewModel) {
        photosView?.update(
            with: viewModel.carouselRoundelDesigns,
            slideBackgroundColor: .clear,
            startIndex: viewModel.startIndex,
            imageSize: .large
        )
    }
}
