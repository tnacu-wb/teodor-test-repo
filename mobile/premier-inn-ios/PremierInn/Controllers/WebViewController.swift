//
//  WebViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 30/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import WebKit
import SimpleNetwork

protocol WebViewControllerDelegate: class {
    func webViewControllerDidCompletePayment(sender: WebViewController, paRes: String?)
    func webViewControllerDidCancel(sender: WebViewController)
}

class WebViewController: BaseViewController {
    override var screenName: String { webViewScreenName }
    override var screenType: String { webViewScreenType  }

    weak var delegate: WebViewControllerDelegate?

    private let request: URLRequest?
    private let html: String?
    private let webViewScreenName: String
    private let webViewScreenType: String

    init(request: URLRequest, screenName: String, screenType: String) {
        self.request = request
        self.html = nil
        self.webViewScreenName = screenName
        self.webViewScreenType = screenType

        super.init(nibName: String(describing: WebViewController.self), bundle: nil)
    }

    init(html: String, scaleToFit: Bool = false, screenName: String, screenType: String) {
        self.request = nil
        self.html = html
        self.webViewScreenName = screenName
        self.webViewScreenType = screenType

        super.init(nibName: String(describing: WebViewController.self), bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {
        let aView = UIView(frame: .zero)
        aView.backgroundColor = .white

        view = aView
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(closeButtonDidTap)
        )

        let webView = UIWebView(frame: view.frame)
        webView.scalesPageToFit = true
        webView.delegate = self
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        view.addSubview(webView)

        if let request = request {
            webView.loadRequest(request)
        } else if let html = html {
            webView.loadHTMLString(html, baseURL: nil)
        }
    }

    @IBAction func closeButtonDidTap(_ sender: UIButton) {
        delegate?.webViewControllerDidCancel(sender: self)
    }
}

extension WebViewController: UIWebViewDelegate {
    func webView(
        _ webView: UIWebView,
        shouldStartLoadWith request: URLRequest,
        navigationType: UIWebViewNavigationType
    ) -> Bool {
		if RequestsManager.shouldCompletePaymentFlow(with: request.url) {
            if let httpBody = request.httpBody,
               let bodyString = String(data: httpBody, encoding: .utf8),
               let paresComponent = bodyString.components(separatedBy: "&").first,
               let pares = paresComponent.components(separatedBy: "=").last?.removingPercentEncoding {
                delegate?.webViewControllerDidCompletePayment(sender: self, paRes: pares)
            } else {
                AnalyticsManager.trackDebugAction("PaResError", userInfo: ["error": "pares not available"])
                delegate?.webViewControllerDidCompletePayment(sender: self, paRes: nil)
            }

            return false
        }

        return true
    }

    func webView(_ webView: UIWebView, didFailLoadWithError error: Error) {
        AnalyticsManager.trackDebugAction("webViewControllerError", userInfo: ["error": error.localizedDescription])
    }
}
