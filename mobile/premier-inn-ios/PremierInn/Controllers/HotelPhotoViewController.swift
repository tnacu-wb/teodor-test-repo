//
//  HotelPhotoViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 19/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

class HotelPhotoViewController: UIViewController {
	@IBOutlet var imageView: UIImageView!
	@IBOutlet private var activityIndicator: UIActivityIndicatorView!

	private var imageUrl: URL?
	private var loadingError = false
	private var contentMode: UIView.ContentMode {
		if loadingError {
			return .center
		}

		return imageView.frame.width > imageView.frame.height ? .scaleAspectFill : .scaleAspectFit
	}

    var roundel: CircledSquareLabel?

	weak var interactionDelegate: PhotoInteractionDelegate?

    deinit {
         NotificationCenter.default.removeObserver(self)
    }

	init(withImageURL url: URL?) {
		self.imageUrl = url

		super.init(nibName: String(describing: HotelPhotoViewController.self), bundle: nil)

        NotificationCenter.default.addObserver(
        	self,
        	selector: #selector(rotated),
        	name: UIDevice.orientationDidChangeNotification,
        	object: nil
        )
	}

	required init?(coder aDecoder: NSCoder) {
		fatalError("init(coder:) has not been implemented")
	}

	override func viewDidLoad() {
		super.viewDidLoad()

		view.backgroundColor = .warmGrey
	}

	override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)

		activityIndicator.startAnimating()

        guard let imageUrl = imageUrl else { return }

		imageView.setImage(with: imageUrl, transition: true) { [weak self] error in
			self?.activityIndicator.stopAnimating()

			if error == nil {
				self?.loadingError = false
			} else {
				self?.loadingError = true
				self?.imageView.image = #imageLiteral(resourceName: "imageLoadError")
			}

			self?.imageView.contentMode = self?.contentMode ?? .center
		}
    }

	override func viewDidLayoutSubviews() {
		super.viewDidLayoutSubviews()

		imageView.contentMode = contentMode

        if UIDevice.current.orientation.isLandscape {
            roundel?.frame.origin = CGPoint(x: 20, y: 20)
        } else {
            roundel?.frame.origin = CGPoint(x: 10, y: (imageView.frame.size.height / 2) - 100)
        }
	}

	@IBAction func handleTapGesture(_ sender: UITapGestureRecognizer) {
		interactionDelegate?.tappedPhoto()
	}

    @objc func rotated() {
        if UIDevice.current.orientation.isLandscape {
            roundel?.frame.origin = CGPoint(x: 20, y: 20)
        } else {
            roundel?.frame.origin = CGPoint(x: 10, y: (imageView.frame.size.height / 2) - 100)
        }
    }
}
