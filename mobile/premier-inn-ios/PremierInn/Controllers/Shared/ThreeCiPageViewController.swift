//
//  ThreeCiPageViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 20/04/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork
import WebKit

struct ThreeCiPageParams {
    let html: String
    let trackingParams: PIDictionary?
    let allowedEvents: [ThreeCEvent]?
}

enum ThreeCEvent: String {
    case applePaySelected = "Apple pay button clicked"
    case googlePaySelected = "Google pay button clicked"
    case authorizationMessage
}

enum ThreeCiPageError: LocalizedError {
    case missingTransactionId
    case paymentDeclined
    case redirectNotComplete
    case unknown

    var localizedDescription: String {
        switch self {
        case .missingTransactionId:
            return PILocalizedString("missingTxID")
        case .paymentDeclined:
            return PILocalizedString("cardDeclinedError")
        default:
            return String(describing: self)
        }
    }
}

enum ThreeCiPageViewConfig {
    static func checkForPaymentStatus(in url: URL) throws -> String {
        guard url.pathComponents.contains("complete") else {
            throw ThreeCiPageError.redirectNotComplete
        }

        guard let txId = url.queryParams["TxID"] else {
            throw ThreeCiPageError.missingTransactionId
        }

        printDev("succeeded with url: \(url.absoluteString)")
        return txId
    }
}

protocol ThreeCiPageDelegate: AnyObject {
    func finishedAuth(cardType: String?)
    func startPolling(sender: ThreeCiPageViewController?, transactionID: String)
}

protocol WalletAnalyticsDelegate: AnyObject {
    func appleWalletSelected()
    func googleWalletSelected()
}

enum ThreeCiFlow {
    case general
    case regCard
}

typealias ThreeCWebPageInitVariables = (
    html: String,
    screenName: String,
    screenType: String,
    allowedEvents: [ThreeCEvent]?,
    trackingParams: PIDictionary,
    layout: WebViewControllerLayout
)

protocol AuthorizationDelegate: AnyObject {
    func didReceiveAuthorizationMessage(string: String)
}
class ThreeCiPageViewController: WebViewController {
    override var screenName: String { PIAnalytics.StateNames.pay3CiPage }

    weak var threeCiPageDelegate: ThreeCiPageDelegate?
    weak var walletAnalyticsDelegate: WalletAnalyticsDelegate?
    weak var auhorizationDelegate: AuthorizationDelegate?
    private var trackingParams: PIDictionary?
    private let allowedEvents: [ThreeCEvent]?
    private var flow: ThreeCiFlow = .general
    private var ipageLoadStartTime: Date?
    private var ipageLoadEndTime: Date? {
        didSet {
            trackIPageLoadTime()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        webView?.configuration.defaultWebpagePreferences.allowsContentJavaScript = true
        webView?.configuration.userContentController.add(self, name: PaymentResponseHandler.threeCP.rawValue)
        webView?.navigationDelegate = self
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appDidBecomeActive),
            name: UIApplication.didBecomeActiveNotification,
            object: nil
        )
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        NotificationCenter.default.removeObserver(self, name: UIApplication.didBecomeActiveNotification, object: nil)
    }

    init(parameters: ThreeCWebPageInitVariables) {
        if parameters.allowedEvents?.contains(ThreeCEvent.authorizationMessage) == true {
            flow = .regCard
        }

        let contentController = WKUserContentController()

        if parameters.allowedEvents?.count ?? 0 > 0 {
            let scriptSource = "window.addEventListener('message', function(e) {window.webkit.messageHandlers." +
                PaymentResponseHandler.threeCP.rawValue + ".postMessage(e.data);});"
            let script = WKUserScript(source: scriptSource, injectionTime: .atDocumentStart, forMainFrameOnly: true)
            contentController.addUserScript(script)
        }

        self.allowedEvents = parameters.allowedEvents

        super.init(
            html: parameters.html,
            screenName: parameters.screenName,
            screenType: parameters.screenType,
            userContentController: contentController,
            layout: parameters.layout
        )

        self.trackingParams = parameters.trackingParams
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func load(htmlString: String) {
        super.load(htmlString: htmlString)

        ipageLoadStartTime = Date()
    }

    @objc private func appDidBecomeActive() {
        setCloseButtonEnabled(is: !(webView?.isLoading ?? false))
    }

    private func trackIPageLoadTime() {
        guard let startTime = ipageLoadStartTime, let endTime = ipageLoadEndTime, startTime < endTime else { return }

        var params = trackingParams ?? [:]
        params[PIAnalytics.Keys.iPageloadTime] = endTime.timeIntervalSince(startTime)

        trackState(withName: PIAnalytics.StateNames.pay3CiPageLaunched, type: screenType, additionalData: params)
    }

    override var customParameters: [String: Any]? {
        trackingParams
    }
    var didLoadRegCardAuthorization: Bool = false
}

extension ThreeCiPageViewController: ThreeCiPageProtocol {
    func dismiss() {
        dismiss(animated: true)
    }
}

extension ThreeCiPageViewController: WKNavigationDelegate {
    func webView(
        _ webView: WKWebView,
        decidePolicyFor navigationAction: WKNavigationAction,
        decisionHandler: @escaping (WKNavigationActionPolicy) -> Void
    ) {
        guard let url = navigationAction.request.url else { return }

        checkTheRedirect(url: url, decisionHandler: decisionHandler)
    }

    private func checkTheRedirect(url: URL, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        do {
            let action: (() -> Bool) = {
                switch flow {
                case .general:
                    return {
                        self.threeCiPageDelegate?.finishedAuth(cardType: url.queryParams["CardType"])
                        return false
                    }
                case .regCard:
                    return {
                        guard self.didLoadRegCardAuthorization == false else {
                            decisionHandler(.allow)
                            return true
                        }
                        let req = URLRequest(url: url)
                        self.webView?.load(req)
                        self.didLoadRegCardAuthorization = true
                        return false
                    }
                }
            }()

            try checkRedirectWith(url: url, perform: action, decisionHandler: decisionHandler)
        } catch ThreeCiPageError.redirectNotComplete {
            setCloseButtonEnabled(is: false)
            decisionHandler(.allow)
        } catch {
            // delegate handle error
            // dismiss web view
            print(error.localizedDescription)
            decisionHandler(.cancel)
        }
    }

    private func checkRedirectWith(
        url: URL,
        perform: (() -> Bool),
        decisionHandler: @escaping (WKNavigationActionPolicy) -> Void
    ) throws {
        let transactionID = try ThreeCiPageViewConfig.checkForPaymentStatus(in: url)
        let didCallDecisionHandler = perform()

        self.threeCiPageDelegate?.startPolling(sender: self, transactionID: transactionID)
        guard didCallDecisionHandler == false else { return }
        decisionHandler(.allow)
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation?) {
        setCloseButtonEnabled(is: true)

        guard ipageLoadEndTime == nil else { return }
        guard webView.url?.absoluteString.contains("iPage/Service") == true else { return }

        ipageLoadEndTime = Date()
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation?, withError error: Error) {
        setCloseButtonEnabled(is: true)
    }
}

extension ThreeCiPageViewController: WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        guard let allowedEvents = allowedEvents, allowedEvents.isNotEmpty else { return }

        if message.name == PaymentResponseHandler.threeCP.rawValue {
            // we are intercepting all messages in the iframe so we should ignore all the ones we don't care about
            guard let body = message.body as? String else { return }
            switch flow {
            case .general:
                guard let bodyEnum = ThreeCEvent(rawValue: body),
                      allowedEvents.contains(bodyEnum) == true else { return }

                switch bodyEnum {
                case .applePaySelected:
                    walletAnalyticsDelegate?.appleWalletSelected()
                case .googlePaySelected:
                    walletAnalyticsDelegate?.googleWalletSelected()
                default: break
                }
            case .regCard:
                auhorizationDelegate?.didReceiveAuthorizationMessage(string: body)
            }
        }
    }
}
