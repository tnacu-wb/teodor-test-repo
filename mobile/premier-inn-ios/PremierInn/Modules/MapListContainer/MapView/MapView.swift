//
//  MapView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import MapKit
import SimpleNetwork

protocol MapPresenterProtocol: AnyObject {
	var annotations: [MKAnnotation] { get }
	var nearbyAnnotations: [MKAnnotation] { get }
    var lastSuccessfulSuggestion: Suggestion? { get }

    func mapViewIsReady()
	func mapDidPanByUser()
	func didTapAnnotation(_ annotation: MKAnnotation)
	func listButtonDidTap()
	func resetButtonDidTap()
    func searchHereButtonDidTap()
    func performLastSuccessfulSearch()
	func mapViewDidDeselectAnnotation()
}

class Map2ViewController: UIViewController {
	var presenter: MapPresenterProtocol?
	var smallMapStyleMapInsets = UIEdgeInsets.zero

    @IBOutlet weak var resetButtonTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var resetButtonLeadingConstraint: NSLayoutConstraint!
    @IBOutlet weak var searchHereButtonTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var listButtonTopConstraint: NSLayoutConstraint!
    @IBOutlet weak var noHotelsNotificationHeightConstraint: NSLayoutConstraint!

	@IBOutlet weak var mapView: MKMapView! {
		didSet {
			mapView.isPitchEnabled = false
			mapView.showsCompass = false

			mapView.delegate = self
			mapView.isUserInteractionEnabled = false

            let panGestureRecognizer = UIPanGestureRecognizer(target: self, action: #selector(handlePanGesture))
            panGestureRecognizer.delegate = self

            mapView.addGestureRecognizer(panGestureRecognizer)
		}
	}
	@IBOutlet weak var listButton: UIButton! {
		didSet {
			listButton.setTitle(PILocalizedString("mapListButtonTitle", comment: "Map List button title"), for: .normal)
			listButton.titleLabel?.font = UIFont.Body()
			listButton.setTitleColor(.TintD1, for: .normal)
			listButton.backgroundColor = .BaseWhite
			listButton.alpha = 0
			listButton.roundCorners(withDropShadowOffset: CGSize(width: 0, height: 2))
			listButton.layer.borderWidth = 1
			listButton.layer.borderColor = UIColor.TintL3.cgColor
		}
	}
	@IBOutlet weak var resetButton: UIButton! {
		didSet {
			resetButton.setTitle(PILocalizedString("mapResetButtonTitle", comment: "Map Reset button title"), for: .normal)
            resetButton.titleLabel?.font = UIFont.Body()
			resetButton.setTitleColor(.TintD1, for: .normal)
			resetButton.backgroundColor = .BaseWhite
			resetButton.alpha = 0
			resetButton.roundCorners(withDropShadowOffset: CGSize(width: 0, height: 2))
            resetButton.layer.borderWidth = 1
            resetButton.layer.borderColor = UIColor.TintL3.cgColor
		}
	}
    @IBOutlet weak var searchHereButton: UIButton! {
        didSet {
            searchHereButton.setTitle(PILocalizedString("Search this area"), for: .normal)
            searchHereButton.titleLabel?.font = UIFont.Body()
            searchHereButton.setTitleColor(.BaseWhite, for: .normal)
        }
    }
    @IBOutlet weak var searchHereContainer: UIView! {
        didSet {
            searchHereContainer.alpha = 0
            searchHereContainer.backgroundColor = .Tint1
            searchHereContainer.roundCorners(withDropShadowOffset: CGSize(width: 0, height: 1))
        }
    }
    @IBOutlet weak var noHotelsNotification: NotificationView! {
        didSet {
            noHotelsNotification.backgroundColor = .Tint2
            noHotelsNotification.titleLabel.text = PILocalizedString("No hotels found")
            noHotelsNotification.messageLabel.text = PILocalizedString("No available hotels in area")
            noHotelsNotification.actionButton.setTitle(PILocalizedString("Go back"), for: .normal)
            noHotelsNotification.notificationViewInputDelegate = self
        }
    }

	private var firstTime = true

    deinit {
        print("DEINIT: \(self)")
    }

	override func viewDidLoad() {
		super.viewDidLoad()

        mapView.register(HotelPin.self, forAnnotationViewWithReuseIdentifier: HotelPin.reuseIdentifier)
        mapView.register(SuggestionPin.self, forAnnotationViewWithReuseIdentifier: SuggestionPin.reuseIdentifier)

        switch UIDevice.current.userInterfaceIdiom {
        case .pad:
            resetButtonTopConstraint.constant = 10
            resetButtonLeadingConstraint.constant = UIScreen.main.bounds.width - resetButton.frame.width - 10
            searchHereButtonTopConstraint.constant = 72

        default:
            resetButtonTopConstraint.constant = 104
            resetButtonLeadingConstraint.constant = 15
            searchHereButtonTopConstraint.constant = 164
        }
	}

	override func viewDidAppear(_ animated: Bool) {
		super.viewDidAppear(animated)

		// This needs to be triggered only once,
		// after the view has been rendered
		if firstTime {
			firstTime = false
			presenter?.mapViewIsReady()
		}
	}

    // this will only be called for iPad, since for iPhone we only support portrait mode
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)

        UIView.animate(withDuration: .ocd) {
            self.resetButtonLeadingConstraint.constant = UIScreen.main.bounds.width - self.resetButton.frame.width - 10
        }
    }

    @objc private func handlePanGesture(_ gestureRecognizer: UIPanGestureRecognizer) {
        if gestureRecognizer.state == .began {
            presenter?.mapDidPanByUser()
        }
    }

	@IBAction func listButtonDidTap(_ sender: UIButton) {
		presenter?.listButtonDidTap()
	}

	@IBAction func resetButtonDidTap(_ sender: Any) {
		presenter?.resetButtonDidTap()
	}

    @IBAction func searchHereButtonDidTap(_ sender: Any) {
        presenter?.searchHereButtonDidTap()
    }
}

extension Map2ViewController: UIGestureRecognizerDelegate {
    func gestureRecognizer(
    	_ gestureRecognizer: UIGestureRecognizer,
    	shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer
    ) -> Bool {
        true
    }
}

extension Map2ViewController: MKMapViewDelegate {
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        switch annotation {
        case is Hotel:
            let pin: HotelPin? = mapView.dequeueReusableAnnotationView(with: HotelPin.reuseIdentifier, for: annotation)

            return pin

        case is Suggestion:
            let pin: SuggestionPin? = mapView.dequeueReusableAnnotationView(
            	with: SuggestionPin.reuseIdentifier,
            	for: annotation
            )

            return pin

        default:
            return nil
        }
    }

	func mapView(_ mapView: MKMapView, didSelect view: MKAnnotationView) {
		guard let annotation = view.annotation else { return }

		presenter?.didTapAnnotation(annotation)
	}

	func mapView(_ mapView: MKMapView, didDeselect view: MKAnnotationView) {
        presenter?.mapViewDidDeselectAnnotation()
	}
}

extension Map2ViewController: MapViewProtocol {
    public var centerCoordinate: CLLocationCoordinate2D {
        mapView.centerCoordinate
    }

	func reload() {
        guard let annotations = presenter?.annotations else { return }

        hideNoHotelsFoundNotification()

        mapView.removeAnnotations(mapView.annotations)
        mapView.addAnnotations(annotations)

        if annotations.count <= 1 {
            presentNoHotelsFoundNotification()
        }
	}

    private func presentNoHotelsFoundNotification() {
        let lastSuccessfulSuggestionExists = presenter?.lastSuccessfulSuggestion != nil
        let noHotelsNotificationHeight = lastSuccessfulSuggestionExists ? 120 : 70 as CGFloat

        noHotelsNotificationHeightConstraint.constant = noHotelsNotificationHeight
        noHotelsNotification.actionButton.isHidden = !lastSuccessfulSuggestionExists


        resetButtonTopConstraint.constant += noHotelsNotificationHeightConstraint.constant
        searchHereButtonTopConstraint.constant += noHotelsNotificationHeightConstraint.constant
        listButtonTopConstraint.constant += noHotelsNotificationHeightConstraint.constant

        noHotelsNotification.show()
    }

    private func hideNoHotelsFoundNotification() {
        noHotelsNotification.hide()

        resetButtonTopConstraint.constant -= noHotelsNotificationHeightConstraint.constant
        searchHereButtonTopConstraint.constant -= noHotelsNotificationHeightConstraint.constant
        listButtonTopConstraint.constant -= noHotelsNotificationHeightConstraint.constant

        noHotelsNotificationHeightConstraint.constant = 0
    }

    func fitAnnotationsInView(style: VenuesListStyle, animated: Bool) {
        guard let annotations = presenter?.nearbyAnnotations else { return }

        mapView.fitAnnotations(
        	annotations,
        	inset: style == .card ? Constants.cardStyleMapInsets : smallMapStyleMapInsets,
        	animated: animated
        )
    }

	func mapWillGoFullScreen() {
		mapView.isUserInteractionEnabled = true
	}

	func mapWillGoSmall() {
		mapView.isUserInteractionEnabled = false
	}

	func centerAnnotation(_ annotation: MKAnnotation) {
        mapView.setCenter(annotation.coordinate, animated: true)
	}

    func zoomToAnnotation(_ annotation: MKAnnotation) {
        mapView.setRegion(
        	MKCoordinateRegion(center: annotation.coordinate, span: MKCoordinateSpan(
        		latitudeDelta: 0.01,
        		longitudeDelta: 0.01
        	)),
        	animated: true
        )
    }

	func deselectAnnotation(_ annotation: MKAnnotation) {
        mapView.deselectAnnotation(annotation, animated: false)
	}

	func selectAnnotation(_ annotation: MKAnnotation) {
        mapView.selectAnnotation(annotation, animated: false)
	}

	func showResetButton() {
		UIView.animate(withDuration: .ocd) {
			self.resetButton.alpha = 1
		}
	}

	func hideResetButton() {
		UIView.animate(withDuration: .ocd) {
			self.resetButton.alpha = 0
		}
	}

	func showListButton() {
		UIView.animate(withDuration: .ocd) {
			self.listButton.alpha = 1
		}
	}

	func hideListButton() {
		UIView.animate(withDuration: .ocd) {
			self.listButton.alpha = 0
		}
	}

    func showSearchHereButton() {
        UIView.animate(withDuration: .ocd) {
            self.searchHereContainer.alpha = 1
        }
    }

    func hideSearchHereButton() {
        UIView.animate(withDuration: .ocd) {
            self.searchHereContainer.alpha = 0
        }
    }
}

extension Map2ViewController: NotificationViewInputDelegate {
    func dismissTapped(withSenderView senderView: NotificationView?) {
        hideNoHotelsFoundNotification()
    }

    func actionTapped(withSenderView senderView: NotificationView?) {
        presenter?.performLastSuccessfulSearch()
    }
}
