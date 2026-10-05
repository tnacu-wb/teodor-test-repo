# Design Document

## Overview

This design describes the iOS implementation of the Datatrans Mobile SDK v4 new-card payment
flow in the Premier Inn app. It introduces a new VIPER module (`DatatransPayment`) that
initiates a payment session via the Payment Orchestrator, starts the Datatrans SDK to collect
card details and run 3-D Secure natively, and shows a placeholder result dialog.

The design follows the existing VIPER conventions (module factory, protocol-driven layers,
`RequestsManager` as data provider) and integrates into the current booking flow via the
`ReviewAndBookRouter`.

---

## Architecture

### Component Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                       PremierInn App                             │
│                                                                 │
│  ┌──────────────────┐       ┌─────────────────────────────────┐ │
│  │ ReviewAndBook    │       │   DatatransPayment Module       │ │
│  │ Router           │──────▶│                                 │ │
│  │                  │       │  ┌───────────┐  ┌────────────┐  │ │
│  └──────────────────┘       │  │  Router   │  │   View     │  │ │
│                             │  └─────┬─────┘  └─────┬──────┘  │ │
│                             │        │              │          │ │
│                             │  ┌─────▼──────────────▼──────┐  │ │
│                             │  │       Presenter           │  │ │
│                             │  └─────────────┬─────────────┘  │ │
│                             │                │                │ │
│                             │  ┌─────────────▼─────────────┐  │ │
│                             │  │       Interactor          │  │ │
│                             │  └─────────────┬─────────────┘  │ │
│                             │                │                │ │
│                             └────────────────┼────────────────┘ │
│                                              │                  │
│  ┌───────────────────────────────────────────▼───────────────┐  │
│  │              SimpleNetwork                                │  │
│  │  RequestsManager+DatatransPayment (new extension)         │  │
│  └───────────────────────────────────────────┬───────────────┘  │
│                                              │                  │
└──────────────────────────────────────────────┼──────────────────┘
                                               │ HTTPS (TLS pinned)
                                               ▼
                                 ┌──────────────────────────┐
                                 │   Payment Orchestrator   │
                                 │ POST /api/payments/      │
                                 │      mobile-sdk          │
                                 └──────────────────────────┘
```

### New Files

| Path | Purpose |
|------|---------|
| `PremierInn/Modules/DatatransPayment/DatatransPaymentModule.swift` | Module factory (assembles VIPER stack) |
| `PremierInn/Modules/DatatransPayment/DatatransPaymentView.swift` | Minimal UIViewController (loading overlay) |
| `PremierInn/Modules/DatatransPayment/DatatransPaymentPresenter.swift` | Orchestrates flow: init session → start SDK → handle result |
| `PremierInn/Modules/DatatransPayment/DatatransPaymentInteractor.swift` | Calls RequestsManager for session init |
| `PremierInn/Modules/DatatransPayment/DatatransPaymentRouter.swift` | Navigation: present SDK, show result dialog, dismiss |
| `SimpleNetwork/Sources/RequestsManager/RequestsManager+DatatransPayment.swift` | New extension: `initMobileSDKPayment(basketId:completion:)` |
| `SimpleNetwork/Sources/Model/DatatransPaymentSession.swift` | Response model: `transactionId` |
| `SimpleNetwork/Sources/Model/DatatransPaymentError.swift` | Typed error enum mapping API error codes |

### Modified Files

| Path | Change |
|------|--------|
| `PremierInn.xcodeproj` | Add Datatrans iOS SDK SPM dependency (`github.com/datatrans/ios-sdk`, exact version) |
| `PremierInn/Modules/ReviewAndBook/ReviewAndBookRouter.swift` | Add method to navigate to `DatatransPaymentModule` |
| `PremierInn/Modules/ReviewAndBook/ReviewAndBookRouterProtocol` | Add `startDatatransPayment(basketId:)` to protocol |
| `PremierInn/Resources/Base.lproj/Localizable.strings` | Add payment string keys |
| `PremierInn/Resources/de.lproj/Localizable.strings` | Add German translations |
| `PremierInn/Utilities/AccessibilityIdentifiers.swift` | Add `DatatransPayment` section |
| `SimpleNetwork/Sources/Networking/Router.swift` | Add route for `POST /api/payments/mobile-sdk` |

---

## Detailed Design

### 1. Datatrans SDK Dependency

Add the [Datatrans iOS SDK](https://github.com/datatrans/ios-sdk) via SPM to the
`PremierInn.xcodeproj` (not to SimpleNetwork — the SDK is a UI concern).

```
.package(url: "https://github.com/datatrans/ios-sdk.git", exact: "4.0.0")
```

Include packages: `Datatrans` and `ThreeDS_SDK` (required for native 3-D Secure).
The Swift module name is `Datatrans` (use `import Datatrans`, not `import DatatransSDK`).

**Note:** The Braintree/PayPal (`braintree_ios`) SPM dependency was removed from the project
because both `braintree_ios` and `ios-sdk` (Datatrans) declare an SPM target named
`PPRiskMagnes`, causing a build-time collision. PayPal payment flows via Braintree are no
longer available in this build.

Add to `Info.plist`:
- `NSCameraUsageDescription` — for card scanning (required by SDK even if not used).

### 2. Network Layer (`SimpleNetwork`)

#### 2.1 Route

Add to `Router.swift`:

```swift
static func initMobileSDKPayment(basketId: String) throws -> Resource<DatatransPaymentSessionResponse> {
    let body: [String: Any] = ["basketId": basketId]
    return try postResource(
        path: "/api/payments/mobile-sdk",
        body: body
    )
}
```

#### 2.2 Response Model

```swift
// SimpleNetwork/Sources/Model/DatatransPaymentSession.swift

public struct DatatransPaymentSessionResponse: Decodable {
    public let transactionId: String
}
```

#### 2.3 Error Model

```swift
// SimpleNetwork/Sources/Model/DatatransPaymentError.swift

public enum DatatransPaymentErrorCode: String, Decodable {
    case invalidRequest = "INVALID_REQUEST"
    case basketNotFound = "BASKET_NOT_FOUND"
    case bookingAlreadyPaid = "BOOKING_ALREADY_PAID"
    case bookingAlreadyConfirmed = "BOOKING_ALREADY_CONFIRMED"
    case paymentMethodNotAvailable = "PAYMENT_METHOD_NOT_AVAILABLE"
    case gatewayError = "GATEWAY_ERROR"
    case serviceUnavailable = "SERVICE_UNAVAILABLE"
}

public struct DatatransPaymentErrorResponse: Decodable {
    public let error: DatatransPaymentErrorDetail
}

public struct DatatransPaymentErrorDetail: Decodable {
    public let code: String
    public let message: String

    public var typed: DatatransPaymentErrorCode? {
        DatatransPaymentErrorCode(rawValue: code)
    }
}
```

#### 2.4 RequestsManager Extension

```swift
// SimpleNetwork/Sources/RequestsManager/RequestsManager+DatatransPayment.swift

public extension RequestsManager {
    func initMobileSDKPayment(
        basketId: String,
        completion: @escaping (_ response: DatatransPaymentSessionResponse?, _ error: Error?) -> Void
    ) {
        do {
            let resource = try Router.current.initMobileSDKPayment(basketId: basketId)
            load(resource: resource, completion: completion)
        } catch {
            completion(nil, error)
        }
    }
}
```

### 3. VIPER Module: `DatatransPayment`

#### 3.1 Module Factory

```swift
// PremierInn/Modules/DatatransPayment/DatatransPaymentModule.swift

import UIKit

enum DatatransPaymentModule {
    static func build(basketId: String, delegate: DatatransPaymentDelegate?) -> UIViewController {
        let controller = DatatransPaymentView()

        let router = DatatransPaymentRouter()
        router.view = controller
        router.delegate = delegate

        let interactor = DatatransPaymentInteractor(basketId: basketId)

        let presenter = DatatransPaymentPresenter()
        presenter.view = controller
        presenter.interactor = interactor
        presenter.router = router

        controller.presenter = presenter

        return controller
    }
}
```

#### 3.2 Protocols

```swift
// Within DatatransPaymentModule.swift

protocol DatatransPaymentViewProtocol: AnyObject {
    func showLoading()
    func hideLoading()
}

protocol DatatransPaymentPresenterProtocol {
    func viewDidLoad()
}

protocol DatatransPaymentInteractorProtocol {
    func initiatePaymentSession(completion: @escaping (Result<DatatransPaymentSessionResponse>) -> Void)
}

protocol DatatransPaymentRouterProtocol {
    func presentSDK(transactionId: String, from controller: UIViewController)
    func showSuccessDialog()
    func showErrorDialog(message: String)
    func dismiss()
}

protocol DatatransPaymentDelegate: AnyObject {
    func paymentDidComplete()
    func paymentDidFail()
    func paymentDidCancel()
}
```

#### 3.3 View

```swift
// PremierInn/Modules/DatatransPayment/DatatransPaymentView.swift

import UIKit

class DatatransPaymentView: UIViewController {
    var presenter: DatatransPaymentPresenterProtocol?

    private let activityIndicator = UIActivityIndicatorView(style: .large)

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        presenter?.viewDidLoad()
    }

    private func setupUI() {
        view.backgroundColor = .white
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(activityIndicator)
        NSLayoutConstraint.activate([
            activityIndicator.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            activityIndicator.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
    }
}

extension DatatransPaymentView: DatatransPaymentViewProtocol {
    func showLoading() {
        activityIndicator.startAnimating()
    }

    func hideLoading() {
        activityIndicator.stopAnimating()
    }
}
```

#### 3.4 Presenter

```swift
// PremierInn/Modules/DatatransPayment/DatatransPaymentPresenter.swift

import Foundation
import UIKit
import Datatrans
import SimpleNetwork

class DatatransPaymentPresenter {
    weak var view: DatatransPaymentViewProtocol?
    var interactor: DatatransPaymentInteractorProtocol?
    var router: DatatransPaymentRouterProtocol?

    private var isPaymentInProgress = false
}

extension DatatransPaymentPresenter: DatatransPaymentPresenterProtocol {
    func viewDidLoad() {
        guard !isPaymentInProgress else { return }
        isPaymentInProgress = true

        view?.showLoading()
        interactor?.initiatePaymentSession { [weak self] result in
            DispatchQueue.main.async {
                self?.handleSessionResult(result)
            }
        }
    }

    private func handleSessionResult(_ result: Result<DatatransPaymentSessionResponse>) {
        switch result {
        case .success(let session):
            guard let viewController = view as? UIViewController else { return }
            router?.presentSDK(transactionId: session.transactionId, from: viewController)

        case .failure(let error):
            view?.hideLoading()
            isPaymentInProgress = false
            if let flowError = error as? DatatransPaymentFlowError {
                router?.showErrorDialog(message: flowError.localizedMessage)
            } else {
                router?.showErrorDialog(message: DatatransPaymentFlowError.unknown.localizedMessage)
            }
        }
    }
}

extension DatatransPaymentPresenter: TransactionDelegate {
    func transactionDidFinish(_ transaction: Transaction, result: TransactionSuccess) {
        view?.hideLoading()
        isPaymentInProgress = false
        router?.showSuccessDialog()
    }

    func transactionDidFail(_ transaction: Transaction, error: TransactionError) {
        view?.hideLoading()
        isPaymentInProgress = false
        router?.showErrorDialog(message: error.message ?? DatatransPaymentFlowError.unknown.localizedMessage)
    }

    func transactionDidCancel(_ transaction: Transaction) {
        view?.hideLoading()
        isPaymentInProgress = false
        router?.dismiss()
    }
}
```

**SDK API notes (v4.0.0):**
- `TransactionDelegate` methods all receive a `Transaction` parameter as the first argument.
- `TransactionError` exposes `message: String?` (not `localizedDescription`).
- The project uses SimpleNetwork's custom `Result<T>` enum (single generic parameter with
  `.success(result: T)` and `.failure(error: Error)` cases), not Swift's standard
  `Result<Success, Failure>`.

#### 3.5 Interactor

```swift
// PremierInn/Modules/DatatransPayment/DatatransPaymentInteractor.swift

import Foundation
import SimpleNetwork

class DatatransPaymentInteractor {
    private let basketId: String
    private let dataProvider: DatatransPaymentDataProvider

    init(basketId: String, dataProvider: DatatransPaymentDataProvider = RequestsManager()) {
        self.basketId = basketId
        self.dataProvider = dataProvider
    }
}

protocol DatatransPaymentDataProvider {
    func initMobileSDKPayment(
        basketId: String,
        completion: @escaping (_ response: DatatransPaymentSessionResponse?, _ error: Error?) -> Void
    )
}

extension RequestsManager: DatatransPaymentDataProvider {}

extension DatatransPaymentInteractor: DatatransPaymentInteractorProtocol {
    func initiatePaymentSession(
        completion: @escaping (Result<DatatransPaymentSessionResponse>) -> Void
    ) {
        dataProvider.initMobileSDKPayment(basketId: basketId) { response, error in
            if let response = response {
                completion(.success(result: response))
            } else if let error = error {
                let flowError = DatatransPaymentFlowError.from(error: error)
                completion(.failure(error: flowError))
            } else {
                completion(.failure(error: DatatransPaymentFlowError.unknown))
            }
        }
    }
}
```

#### 3.6 Router

```swift
// PremierInn/Modules/DatatransPayment/DatatransPaymentRouter.swift

import UIKit
import Datatrans

class DatatransPaymentRouter {
    weak var view: UIViewController?
    weak var delegate: DatatransPaymentDelegate?
}

extension DatatransPaymentRouter: DatatransPaymentRouterProtocol {
    func presentSDK(transactionId: String, from controller: UIViewController) {
        let transaction = Transaction(transactionId: transactionId)

        // Set callback URL for 3-D Secure bank app return
        transaction.options.appCallbackURL = "https://www.premierinn.com"

        // Set accent colour to PI brand
        transaction.theme.accentColor = UIColor.BasePurple

        // Presenter is the TransactionDelegate
        if let presenter = (controller as? DatatransPaymentView)?.presenter as? TransactionDelegate {
            transaction.delegate = presenter
        }

        transaction.start(presentingController: controller)
    }

    func showSuccessDialog() {
        let alert = UIAlertController(
            title: PILocalizedString("datatransPaymentSuccessTitle"),
            message: PILocalizedString("datatransPaymentSuccessMessage"),
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(
            title: PILocalizedString("OK"),
            style: .default
        ) { [weak self] _ in
            self?.delegate?.paymentDidComplete()
            self?.dismiss()
        })
        view?.present(alert, animated: true)
    }

    func showErrorDialog(message: String) {
        let alert = UIAlertController(
            title: PILocalizedString("datatransPaymentFailedTitle"),
            message: message,
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(
            title: PILocalizedString("OK"),
            style: .default
        ) { [weak self] _ in
            self?.delegate?.paymentDidFail()
            self?.dismiss()
        })
        view?.present(alert, animated: true)
    }

    func dismiss() {
        view?.dismiss(animated: true)
    }
}
```

**SDK API notes (v4.0.0):**
- `TransactionOptions` does NOT have `testing` or `language` properties. The
  sandbox/production environment is determined entirely server-side when the backend creates
  the `transactionId`. The SDK language follows the device locale automatically.
- Available `TransactionOptions` properties: `appCallbackScheme`, `appCallbackURL`,
  `applePayConfig`, `cardLabelType`, `merchantProperties`, `suppressCriticalErrorDialog`,
  `twintMaxIssuerNumber`, `customInitialLoaderDelegate`, `savedCardDCCShowMode`, `autoAuthorize`.

#### 3.7 Flow Error Enum

```swift
// PremierInn/Modules/DatatransPayment/DatatransPaymentFlowError.swift

import Foundation

enum DatatransPaymentFlowError: Error {
    case invalidRequest
    case basketNotFound
    case bookingAlreadyPaid
    case bookingAlreadyConfirmed
    case paymentMethodNotAvailable
    case gatewayError
    case serviceUnavailable
    case timeout
    case noConnectivity
    case unknown

    var localizedMessage: String {
        switch self {
        case .invalidRequest:
            return PILocalizedString("datatransPaymentErrorInvalidRequest")
        case .basketNotFound:
            return PILocalizedString("datatransPaymentErrorBasketNotFound")
        case .bookingAlreadyPaid:
            return PILocalizedString("datatransPaymentErrorAlreadyPaid")
        case .bookingAlreadyConfirmed:
            return PILocalizedString("datatransPaymentErrorAlreadyConfirmed")
        case .paymentMethodNotAvailable:
            return PILocalizedString("datatransPaymentErrorMethodNotAvailable")
        case .gatewayError, .serviceUnavailable:
            return PILocalizedString("datatransPaymentErrorTemporary")
        case .timeout:
            return PILocalizedString("datatransPaymentErrorTimeout")
        case .noConnectivity:
            return PILocalizedString("datatransPaymentErrorNoConnectivity")
        case .unknown:
            return PILocalizedString("datatransPaymentErrorUnknown")
        }
    }

    static func from(error: Error) -> DatatransPaymentFlowError {
        // Map HTTP status / error code to typed error
        // Implementation will inspect the error's underlying response code
        // and parse DatatransPaymentErrorResponse from the response body
        return .unknown
    }
}
```

### 4. Integration Point: ReviewAndBookRouter

Add a new method to `ReviewAndBookRouter`:

```swift
// In ReviewAndBookRouter.swift

func startDatatransPayment(basketId: String) {
    let controller = DatatransPaymentModule.build(basketId: basketId, delegate: self)
    controller.modalPresentationStyle = .fullScreen
    viewController?.present(controller, animated: true)
}
```

And conformance to `DatatransPaymentDelegate`:

```swift
extension ReviewAndBookRouter: DatatransPaymentDelegate {
    func paymentDidComplete() {
        // Placeholder: stay on review screen. Future: navigate to confirmation.
    }

    func paymentDidFail() {
        // Guest stays on review screen to retry
    }

    func paymentDidCancel() {
        // Guest stays on review screen
    }
}
```

### 5. SDK Environment Configuration

In Datatrans SDK v4.0.0, the sandbox/production environment is **not** configured client-side.
The `TransactionOptions` class does not expose a `testing` property. Instead, the environment
is determined entirely server-side when the Payment Orchestrator creates the transaction via
Datatrans's API. The backend uses its own environment configuration to decide whether to call
Datatrans sandbox or production endpoints.

This means:
- DEV/UAT builds → backend creates transactions against Datatrans sandbox
- Production builds → backend creates transactions against Datatrans production

The app does not need to (and cannot) set the environment on the SDK directly.

### 6. 3-D Secure (Native)

The Datatrans SDK v4 handles 3DS natively when the `ThreeDS_SDK` package is included. The
app must:

1. Include `ThreeDS_SDK` in SPM package selection.
2. Set `TransactionOptions.appCallbackURL` to a universal link that the app handles (for
   returning from bank apps during SCA). Use the existing `premierinn.com` associated domain.

The SDK invokes its own 3DS challenge UI inside the app. The presenter receives the result
through the `TransactionDelegate` callbacks — no additional app-side work needed.

### 7. Localization

New keys added to `Localizable.strings`:

| Key | English (Base) | German (de) |
|-----|---------------|-------------|
| `datatransPaymentSuccessTitle` | Payment successful | Zahlung erfolgreich |
| `datatransPaymentSuccessMessage` | Your payment has been processed. | Ihre Zahlung wurde verarbeitet. |
| `datatransPaymentFailedTitle` | Payment failed | Zahlung fehlgeschlagen |
| `datatransPaymentErrorInvalidRequest` | We couldn't process your request. Please try again. | Wir konnten Ihre Anfrage nicht verarbeiten. Bitte versuchen Sie es erneut. |
| `datatransPaymentErrorBasketNotFound` | We couldn't find your booking. Please try again. | Wir konnten Ihre Buchung nicht finden. Bitte versuchen Sie es erneut. |
| `datatransPaymentErrorAlreadyPaid` | This booking has already been paid. | Diese Buchung wurde bereits bezahlt. |
| `datatransPaymentErrorAlreadyConfirmed` | This booking is already confirmed. | Diese Buchung ist bereits bestätigt. |
| `datatransPaymentErrorMethodNotAvailable` | Card payment is not available for this hotel. | Kartenzahlung ist für dieses Hotel nicht verfügbar. |
| `datatransPaymentErrorTemporary` | Something went wrong. Please try again. | Etwas ist schiefgelaufen. Bitte versuchen Sie es erneut. |
| `datatransPaymentErrorTimeout` | The request timed out. Please try again. | Die Anfrage hat das Zeitlimit überschritten. Bitte versuchen Sie es erneut. |
| `datatransPaymentErrorNoConnectivity` | No internet connection. Please check your connection and try again. | Keine Internetverbindung. Bitte überprüfen Sie Ihre Verbindung und versuchen Sie es erneut. |
| `datatransPaymentErrorUnknown` | An unexpected error occurred. Please try again. | Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es erneut. |
| `datatransPaymentErrorSessionExpired` | Your payment session has expired. Please try again. | Ihre Zahlungssitzung ist abgelaufen. Bitte versuchen Sie es erneut. |
| `datatransPaymentErrorAuthFailed` | Authentication failed. Please try again. | Authentifizierung fehlgeschlagen. Bitte versuchen Sie es erneut. |

### 8. Concurrency Guard

The presenter uses an `isPaymentInProgress` boolean flag to prevent double-submission.
This is set to `true` when `viewDidLoad()` fires and only reset on terminal states
(success, failure, cancel). The view's loading state also blocks user interaction.

---

## Data Flow

```
1. User taps Pay (ReviewAndBook)
       │
       ▼
2. ReviewAndBookRouter.startDatatransPayment(basketId:)
       │
       ▼
3. DatatransPaymentModule.build(basketId:delegate:)
       │ presents modally
       ▼
4. DatatransPaymentView.viewDidLoad()
       │
       ▼
5. Presenter.viewDidLoad() → shows loading, calls interactor
       │
       ▼
6. Interactor.initiatePaymentSession()
       │ calls RequestsManager
       ▼
7. POST /api/payments/mobile-sdk  { basketId }
       │
       ▼
8a. Success (201) → transactionId
       │
       ▼
9. Router.presentSDK(transactionId:)
       │ creates Transaction, sets delegate, starts SDK
       ▼
10. Datatrans native card entry + 3DS
       │
       ├─ transactionDidFinish → Router.showSuccessDialog()
       ├─ transactionDidFail  → Router.showErrorDialog(message:)
       └─ transactionDidCancel → Router.dismiss()

8b. Failure → DatatransPaymentFlowError
       │
       ▼
    Router.showErrorDialog(message:)
```

---

## Security Considerations

- **PCI scope**: Card data is collected exclusively by the Datatrans SDK native UI. The app
  never sees, stores, or transmits raw PAN or CVV.
- **TLS pinning**: All backend calls go through `RequestsManager` which uses the existing
  certificate-pinned Alamofire session.
- **No disk persistence**: `transactionId` is held in memory only (presenter instance
  property); dismissed when the module is torn down.
- **No logging of sensitive data**: The `transactionId` and `basketId` are not written to
  console logs or crash reporters.

---

## Testing Strategy

- **Unit tests**: `DatatransPaymentInteractorTests` — mock `DatatransPaymentDataProvider` to
  verify correct handling of success, each error code, and timeout.
- **Unit tests**: `DatatransPaymentPresenterTests` — mock interactor and router to verify
  flow transitions (loading → SDK start, loading → error dialog, SDK cancel → dismiss).
- **No integration/UI tests for the SDK itself** — Datatrans SDK is a third-party black box;
  test only our wrapper logic.

---

## Assumptions & Decisions

| # | Decision | Rationale |
|---|----------|-----------|
| 1 | Datatrans SDK added to the app target, not SimpleNetwork | The SDK presents UI; SimpleNetwork is a pure networking package with no UIKit dependency |
| 2 | Module presented modally (full screen) | Matches existing payment flow (ThreeCiPageViewController is also modal full-screen) |
| 3 | Placeholder UIAlertController for result | Booking confirmation screen integration is out of scope; will be replaced in a follow-up |
| 4 | Environment (sandbox/production) is server-side only | Datatrans SDK v4.0.0 `TransactionOptions` does not expose `testing` or `language` properties; the backend controls this when creating the transaction |
| 5 | Single `DatatransPaymentDataProvider` protocol | Keeps interactor testable with a mock without touching real networking |
| 6 | No retry logic in interactor | Retry is user-initiated (tap Pay again); automatic retries could double-charge |
| 7 | Universal link for `appCallbackURL` | Required by Datatrans for native 3DS bank-app return; reuses existing associated domain |
| 8 | Braintree/PayPal SPM dependency removed | Both `braintree_ios` and `ios-sdk` declare a `PPRiskMagnes` SPM target; cannot coexist in the same package graph. Datatrans replaces Braintree for card payments |
| 9 | Uses SimpleNetwork's custom `Result<T>` | The project defines `enum Result<T>` (single parameter) which shadows Swift's built-in `Result<Success, Failure>`. All completion handlers use this type |
