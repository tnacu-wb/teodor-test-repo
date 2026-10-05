//
//  MapViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 30/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import MapKit
import UIKit
import SimpleNetwork

class MapViewController: BaseViewController {
    var eventHandler: MapDetailViewEventHandler?

    private(set) var viewModel: MapDetailViewModel? {
        didSet {
            refreshView()
        }
    }

    override var screenName: String { PIAnalytics.StateNames.map }

    private static let pinImage = #imageLiteral(resourceName: "hotelMapPinLarge")

    @IBOutlet weak var footerView: UIView?
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var footerViewHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var resetButtonTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var resetButtonBottomConstraint: NSLayoutConstraint!
    @IBOutlet weak var referenceCompass: MapAnnotationView!
	@IBOutlet weak var referenceCompassWidthConstraint: NSLayoutConstraint!
	@IBOutlet weak var referenceCompassHeightConstraint: NSLayoutConstraint!
    @IBOutlet weak var referenceCenterXConstraint: NSLayoutConstraint!
    @IBOutlet weak var referenceCenterYConstraint: NSLayoutConstraint!
    @IBOutlet weak var hotelCompassXConstraint: NSLayoutConstraint!
    @IBOutlet weak var hotelCompassYConstraint: NSLayoutConstraint!

    @IBOutlet var resetButton: RoundedCornersButton? {
        didSet {
            resetButton?.titleLabel?.font = UIFont.Action1()
        }
    }
    @IBOutlet var distanceLabel: UILabel? {
        didSet {
            distanceLabel?.font = UIFont.Body()
            distanceLabel?.accessibilityIdentifier = AccessibilityIdentifiers.MapDetail.distanceFromSearch
        }
    }
    @IBOutlet var hotelCompass: Compass?

	var timer: Timer?
    var mapView: MKMapView?

	deinit {
		timer?.invalidate()
	}

    init() {
         super.init(nibName: String(describing: MapViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func refreshView() {
        resetButton?.isHidden = true
        distanceLabel?.attributedText = viewModel?.distanceText
        hotelCompass?.alpha = 0
        hotelCompass?.transform = CGAffineTransform(scaleX: 0.01, y: 0.01)
        title = viewModel?.hotelName

        setupMapView()
        centerAnnotations(animated: false)
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        resetButton?.isHidden = true
        hotelCompass?.alpha = 0
        hotelCompass?.transform = CGAffineTransform(scaleX: 0.01, y: 0.01)

        eventHandler?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(false, animated: animated)

        updateReferenceViewText()
        updateMapHelpers()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        centerAnnotations(animated: animated)

        mapView?.delegate = self
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        mapView?.delegate = nil
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        updateUI()
    }


    // MARK: - Map utilities


    private func shouldShowResetButton() -> Bool {
        guard let mapView = mapView else { return false }
        guard let hotelAnnotation = viewModel?.hotelAnnotation else { return false }

        let isHotelVisible = mapView.isAnnotationVisible(annotation: hotelAnnotation)

        if let referenceAnnotation = viewModel?.referenceAnnotation {
            let isLabelVisible = mapView.isAnnotationVisible(annotation: referenceAnnotation)

            return !isHotelVisible || !isLabelVisible
        }

        return !isHotelVisible
    }

    private func updateUI() {
        navigationController?.navigationBar.backItem?.backBarButtonItem?.accessibilityIdentifier = AccessibilityIdentifiers
            .MapDetail.closeButton
        navigationController?.navigationBar.topItem?.accessibilityLabel = AccessibilityIdentifiers.MapDetail
            .navigationItemTitle

        guard let layout = viewModel?.layout else { return }

        switch layout {
        case .journeyPlanner:
            resetButtonTopConstraint.isActive = true
            resetButtonBottomConstraint.isActive = false
            footerViewHeightConstraint.constant = 0
            footerView?.isHidden = true

        default:
            resetButtonTopConstraint.isActive = false
            resetButtonBottomConstraint.isActive = true
            footerViewHeightConstraint.constant = 55
            footerView?.isHidden = false
        }
    }

    private func setupMapView() {
        mapView = SettingsManager.sharedInstance.sharedMapView

        guard let mapView = mapView else { return }
        guard let hotelAnnotation = viewModel?.hotelAnnotation else { return }

        mapView.delegate = self
        mapView.mapType = .standard
        mapView.frame = view.bounds
        mapView.translatesAutoresizingMaskIntoConstraints = false
        view.insert(mapView, .at(index: 0))

        mapView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        mapView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        mapView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        mapView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true

        let panRecognizer = UIPanGestureRecognizer(target: self, action: #selector(mapDidDrag))
        panRecognizer.delegate = self
        mapView.addGestureRecognizer(panRecognizer)

        mapView.removeAnnotations(mapView.annotations)
        mapView.addAnnotation(hotelAnnotation)

        if let referenceAnnotation = viewModel?.referenceAnnotation {
            mapView.addAnnotation(referenceAnnotation)
        }

        setUpCompass()
    }

    private func setUpCompass() {
        hotelCompass?.backgroundImageView.image = hotelCompass?.backgroundImageView.image?.withRenderingMode(.alwaysTemplate)
        hotelCompass?.backgroundImageView.tintColor = .BasePurple
        hotelCompass?.button.imageView?.contentMode = .scaleAspectFill

        guard let design = viewModel?.mapCompassDesign else { return }

        hotelCompass?.backgroundImageView.tintColor = design.colour
        hotelCompass?.button.setImage(UIImage(named: design.imageName), for: .normal)
    }

    func centerAnnotations(animated: Bool) {
        guard let mapView = mapView else { return }
        guard mapView.delegate != nil else { return }
        guard let layout = viewModel?.layout else { return }

        let pinSize = MapViewController.pinImage.size

        let topPadding = Constants.mapPadding + (pinSize.height * 1.2)
        let rightLeftPadding = Constants.mapPadding + pinSize.width
        var bottomPadding = (footerView?.frame.height ?? 0.0) + Constants.mapPadding + pinSize.height

        if layout == .journeyPlanner {
            bottomPadding += mapView.frame.height * 3 / 4
        }

        bottomPadding = 0
        let inset = UIEdgeInsets(top: topPadding, left: rightLeftPadding, bottom: bottomPadding, right: rightLeftPadding)

        mapView.fitAnnotations(mapView.annotations, inset: inset, animated: animated)
    }

	@objc private func mapDidDrag(_ gesture: UIGestureRecognizer) {
		updateMapHelpers()
	}

	@objc func timerDidFire() {
		updateMapHelpers()
	}

	private func updateReferenceViewText() {
		referenceCompass.delegate = self

		if let referenceAnnotation = viewModel?.referenceAnnotation,
		   let title = referenceAnnotation.title {
			referenceCompass.updateWithTextForView(title ?? "", arrowDirection: .down)
			referenceCompass.isHidden = false
		} else {
			referenceCompass.isHidden = true
		}
	}

	func updateMapHelpers() {
        guard let mapView = mapView else { return }
        guard let hotelAnnotation = viewModel?.hotelAnnotation else { return }

		updateCompassPositionAndRotation()
		updateReferencePositionAndRotation()

        let hotelIsVisible = mapView.isAnnotationVisible(annotation: hotelAnnotation)
		hotelCompass?.show = !hotelIsVisible

        if let view = mapView.view(for: hotelAnnotation) {
            view.isHidden = hotelCompass?.show ?? true
        }

        if let referenceAnnotation = viewModel?.referenceAnnotation {
            referenceCompass.isHidden = mapView.isAnnotationVisible(annotation: referenceAnnotation)

            if let view = mapView.view(for: referenceAnnotation) {
                view.isHidden = !referenceCompass.isHidden
            }
        }

        resetButton?.isHidden = !shouldShowResetButton()
	}

    func updateCompassPositionAndRotation() {
        guard let mapView = mapView else { return }
        guard let hotelAnnotation = viewModel?.hotelAnnotation else { return }

        let point = mapView.convert(hotelAnnotation.coordinate, toPointTo: view)
        hotelCompassXConstraint.constant = point.x
        hotelCompassYConstraint.constant = point.y

        let angle = atan2((hotelCompass?.center.y ?? 0.0) - point.y, (hotelCompass?.center.x ?? 0.0) - point.x)

        hotelCompass?.rotation = angle.radiansToDegrees
    }

	func updateReferencePositionAndRotation() {
        guard let mapView = mapView else { return }

		if let referenceAnnotation = viewModel?.referenceAnnotation {
            // This conversion maybe causing the crash, if the view is not build yet it seems to make the point variable NaN
            let point = mapView.convert(referenceAnnotation.coordinate, toPointTo: view)

            if !point.x.isFinite { return }
            if !point.y.isFinite { return }

            referenceCenterXConstraint.constant = point.x

            referenceCenterYConstraint.constant = point.y - referenceCompass.frame.height * 0.5

			let angle = atan2(referenceCompass.center.y - point.y, referenceCompass.center.x - point.x)

			referenceCompass.arrowDirection = {
				if mapView.isAnnotationVisible(annotation: referenceAnnotation) {
					return .down
				}

				switch angle.radiansToDegrees {
				case -45 ... 45:
					return .left
				case 46 ... 135:
					return .up
				case 136 ... 180, -180 ... -136:
					return .right
				default:
					return .down
				}
			}()
		}
	}
}

extension MapViewController: MapDetailViewProtocol {
    func update(with viewModel: MapDetailViewModel) {
        self.viewModel = viewModel
    }
}

extension MapViewController: UIGestureRecognizerDelegate {
    func gestureRecognizer(
        _ gestureRecognizer: UIGestureRecognizer,
        shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
    ) -> Bool {
        true
    }
}
