# Tasks

## Task 1: Add Datatrans iOS SDK dependency via SPM

- [x] Add the Datatrans iOS SDK Swift Package to `PremierInn.xcodeproj` from `https://github.com/datatrans/ios-sdk.git` with an exact version pin
- [x] Include `Datatrans` and `ThreeDS_SDK` packages in the app target
- [x] Add `NSCameraUsageDescription` to `Info.plist` (required by SDK for card scanning capability)
- [x] Verify the project builds successfully with the new dependency

**Requirements:** Req 2 (AC 1, 2), Req 3 (AC 1)

---

## Task 2: Create network layer models and route in SimpleNetwork

- [x] Create `SimpleNetwork/Sources/Model/DatatransPaymentSession.swift` with `DatatransPaymentSessionResponse` struct containing `transactionId: String` (Decodable)
- [x] Create `SimpleNetwork/Sources/Model/DatatransPaymentError.swift` with `DatatransPaymentErrorCode` enum (rawValue String, cases: `invalidRequest`, `basketNotFound`, `bookingAlreadyPaid`, `bookingAlreadyConfirmed`, `paymentMethodNotAvailable`, `gatewayError`, `serviceUnavailable`), `DatatransPaymentErrorResponse` struct, and `DatatransPaymentErrorDetail` struct
- [x] Add `initMobileSDKPayment(basketId:)` route to `SimpleNetwork/Sources/Router.swift` returning a `Resource<DatatransPaymentSessionResponse>` for `POST /api/payments/mobile-sdk`
- [x] Create `SimpleNetwork/Sources/RequestsManager/RequestsManager+DatatransPayment.swift` with `initMobileSDKPayment(basketId:completion:)` public extension method
- [x] Verify SimpleNetwork package compiles

**Requirements:** Req 1 (AC 1, 2, 3–8)

---

## Task 3: Create DatatransPaymentFlowError enum

- [x] Create `PremierInn/Modules/DatatransPayment/DatatransPaymentFlowError.swift`
- [x] Define `DatatransPaymentFlowError` enum with cases: `invalidRequest`, `basketNotFound`, `bookingAlreadyPaid`, `bookingAlreadyConfirmed`, `paymentMethodNotAvailable`, `gatewayError`, `serviceUnavailable`, `timeout`, `noConnectivity`, `unknown`
- [x] Add computed `localizedMessage` property returning the appropriate `PILocalizedString` key for each case
- [x] Add `static func from(error: Error) -> DatatransPaymentFlowError` that parses the HTTP status code and response body (`DatatransPaymentErrorResponse`) to map to the correct case

**Requirements:** Req 1 (AC 3–9), Req 4 (AC 1, 4)

---

## Task 4: Create DatatransPayment VIPER module — Protocols and Module factory

- [x] Create `PremierInn/Modules/DatatransPayment/DatatransPaymentModule.swift`
- [x] Define protocols: `DatatransPaymentViewProtocol` (`showLoading`, `hideLoading`), `DatatransPaymentPresenterProtocol` (`viewDidLoad`), `DatatransPaymentInteractorProtocol` (`initiatePaymentSession(completion:)`), `DatatransPaymentRouterProtocol` (`presentSDK`, `showSuccessDialog`, `showErrorDialog`, `dismiss`), `DatatransPaymentDelegate` (`paymentDidComplete`, `paymentDidFail`, `paymentDidCancel`)
- [x] Define `DatatransPaymentDataProvider` protocol with `initMobileSDKPayment(basketId:completion:)` and add `extension RequestsManager: DatatransPaymentDataProvider {}`
- [x] Implement `enum DatatransPaymentModule` with `static func build(basketId:delegate:) -> UIViewController` that assembles and wires all VIPER layers

**Requirements:** Req 1, Req 2

---

## Task 5: Create DatatransPaymentView

- [x] Create `PremierInn/Modules/DatatransPayment/DatatransPaymentView.swift`
- [x] Implement `DatatransPaymentView: UIViewController` with a centred `UIActivityIndicatorView`
- [x] Store `var presenter: DatatransPaymentPresenterProtocol?` and call `presenter?.viewDidLoad()` in `viewDidLoad()`
- [x] Conform to `DatatransPaymentViewProtocol` — `showLoading()` starts the indicator, `hideLoading()` stops it

**Requirements:** Req 1 (AC 10), Req 2

---

## Task 6: Create DatatransPaymentInteractor

- [x] Create `PremierInn/Modules/DatatransPayment/DatatransPaymentInteractor.swift`
- [x] Accept `basketId: String` and `dataProvider: DatatransPaymentDataProvider` (default `RequestsManager()`) in `init`
- [x] Conform to `DatatransPaymentInteractorProtocol`
- [x] Implement `initiatePaymentSession(completion:)` — calls `dataProvider.initMobileSDKPayment`, maps success to `.success(response)`, maps error via `DatatransPaymentFlowError.from(error:)`

**Requirements:** Req 1 (AC 1–9)

---

## Task 7: Create DatatransPaymentPresenter

- [x] Create `PremierInn/Modules/DatatransPayment/DatatransPaymentPresenter.swift`
- [x] Import `Datatrans`; hold `weak var view`, `var interactor`, `var router`
- [x] Add `isPaymentInProgress` boolean guard to prevent double-submission
- [x] Implement `viewDidLoad()` — set flag, call `view?.showLoading()`, call `interactor?.initiatePaymentSession`
- [x] On success: call `router?.presentSDK(transactionId:from:)`
- [x] On failure: call `view?.hideLoading()`, reset flag, call `router?.showErrorDialog(message:)`
- [x] Conform to `TransactionDelegate`:
  - `transactionDidFinish(_:result:)` → `router?.showSuccessDialog()`
  - `transactionDidFail(_:error:)` → `router?.showErrorDialog(message: error.message)`
  - `transactionDidCancel(_:)` → `router?.dismiss()`

**Requirements:** Req 1 (AC 2, 9, 10), Req 2 (AC 1, 4–7), Req 3 (AC 2–4), Req 4 (AC 5)

---

## Task 8: Create DatatransPaymentRouter

- [x] Create `PremierInn/Modules/DatatransPayment/DatatransPaymentRouter.swift`
- [x] Import `Datatrans`; hold `weak var view: UIViewController?` and `weak var delegate: DatatransPaymentDelegate?`
- [x] Implement `presentSDK(transactionId:from:)`:
  - Create `Transaction(transactionId:)`
  - Set `transaction.options.appCallbackURL` to the app's premierinn.com universal link
  - Set `transaction.theme.accentColor = UIColor.BasePurple`
  - Set `transaction.delegate` to the presenter (via controller cast)
  - Call `transaction.start(presentingController:)`
  - Note: SDK v4.0.0 does not expose `testing` or `language` on `TransactionOptions`; environment is server-side
- [x] Implement `showSuccessDialog()` — present `UIAlertController` with localised success title/message, OK button that calls `delegate?.paymentDidComplete()` then `dismiss()`
- [x] Implement `showErrorDialog(message:)` — present `UIAlertController` with localised failure title and provided message, OK button that calls `delegate?.paymentDidFail()` then `dismiss()`
- [x] Implement `dismiss()` — `view?.dismiss(animated: true)`

**Requirements:** Req 2 (AC 1–5, 7), Req 3 (AC 1), Req 5 (AC 3, 4)

---

## Task 9: Integrate into ReviewAndBookRouter

- [x] Add `startDatatransPayment(basketId:)` to `ReviewAndBookRouterProtocol`
- [x] Implement `startDatatransPayment(basketId:)` in `ReviewAndBookRouter` — build and present `DatatransPaymentModule` modally (full screen)
- [x] Add `extension ReviewAndBookRouter: DatatransPaymentDelegate` with placeholder implementations for `paymentDidComplete()`, `paymentDidFail()`, `paymentDidCancel()` (guest stays on review screen)

**Requirements:** Req 2 (AC 6), Req 4 (AC 3)

---

## Task 10: Add localised strings (English + German)

- [x] Add all `datatransPayment*` string keys to `PremierInn/Resources/Base.lproj/Localizable.strings` with English values (see design doc Section 7 table)
- [x] Add all matching keys to `PremierInn/Resources/de.lproj/Localizable.strings` with German translations
- [x] Verify all `PILocalizedString` references in the module resolve without missing-key warnings

**Requirements:** Req 5 (AC 1–4)

---

## Task 11: Configure 3-D Secure universal link

- [x] Set `TransactionOptions.appCallbackURL` to the app's existing `premierinn.com` universal link in `DatatransPaymentRouter.presentSDK`
- [x] Verify the associated domains entitlement already covers the callback URL path (no change expected if reusing existing domain)

**Requirements:** Req 3 (AC 1, 2)

---

## Task 12: Unit tests — DatatransPaymentInteractor

- [x] Create `PremierInnTests/Modules/DatatransPayment/DatatransPaymentInteractorTests.swift`
- [x] Create a mock `DatatransPaymentDataProvider` that returns configurable responses
- [x] Test: success response maps to `.success` with correct `transactionId`
- [x] Test: each `DatatransPaymentErrorCode` maps to the correct `DatatransPaymentFlowError` case
- [x] Test: nil response + nil error maps to `.unknown`
- [x] Test: timeout error maps to `.timeout`
- [x] Test: connectivity error maps to `.noConnectivity`

**Requirements:** Req 1 (AC 1–9)

---

## Task 13: Unit tests — DatatransPaymentPresenter

- [x] Create `PremierInnTests/Modules/DatatransPayment/DatatransPaymentPresenterTests.swift`
- [x] Create mock `DatatransPaymentViewProtocol`, `DatatransPaymentInteractorProtocol`, `DatatransPaymentRouterProtocol`
- [x] Test: `viewDidLoad()` calls `showLoading()` then `initiatePaymentSession`
- [x] Test: on session success, `presentSDK` is called with correct transactionId
- [x] Test: on session failure, `hideLoading()` and `showErrorDialog(message:)` are called
- [x] Test: `transactionDidFinish` calls `showSuccessDialog()`
- [x] Test: `transactionDidFail` calls `showErrorDialog(message:)`
- [x] Test: `transactionDidCancel` calls `dismiss()`
- [x] Test: double-call to `viewDidLoad()` does not trigger a second session init (concurrency guard)

**Requirements:** Req 1 (AC 10), Req 2 (AC 1, 4–7), Req 4 (AC 5)
