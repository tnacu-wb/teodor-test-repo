//
//  WebViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 30/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import WebKit

public enum PaymentResponseHandler: String {
    case threeCP = "threeCPaymentResponseHandler"
}

protocol WebViewControllerDelegate: AnyObject {
    func webViewControllerDidCancel(sender: WebViewController)
}

enum WebViewControllerLayout {
    case general
    case withBanner(NSAttributedString)
}

class WebViewController: BaseViewController {
    override var screenName: String { webViewScreenName }
    override var screenType: String { webViewScreenType  }

    weak var delegate: WebViewControllerDelegate?

    @objc var webView: WKWebView?

    private let request: URLRequest?
    private let html: String?
    private let webViewScreenName: String
    private let webViewScreenType: String
    private let userContentController: WKUserContentController?
    private let layout: WebViewControllerLayout

    lazy var bannerView: BannerView? = {
        guard let messageView: BannerView = UIView.fromNib(nibName: "BannerView") else { return nil }
        messageView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(messageView)
        NSLayoutConstraint.activate(
            [
                messageView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 12),
                messageView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -12),
                messageView.topAnchor.constraint(equalTo: view.topAnchor, constant: 27),
                messageView.heightAnchor.constraint(greaterThanOrEqualToConstant: 50)
            ]
        )
        return messageView
    }()

    init(
        request: URLRequest,
        screenName: String,
        screenType: String,
        userContentController: WKUserContentController? = nil,
        layout: WebViewControllerLayout = .general
    ) {
        self.request = request
        self.html = nil
        self.webViewScreenName = screenName
        self.webViewScreenType = screenType
        self.userContentController = userContentController
        self.layout = layout

        super.init(nibName: nil, bundle: nil)
    }

    init(
        html: String,
        screenName: String,
        screenType: String,
        userContentController: WKUserContentController? = nil,
        layout: WebViewControllerLayout = .general
    ) {
        self.request = nil
        self.html = html
        self.webViewScreenName = screenName
        self.webViewScreenType = screenType
        self.userContentController = userContentController
        self.layout = layout
        super.init(nibName: nil, bundle: nil)
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

        let paymentResponseHandlerConfiguration = WKWebViewConfiguration()
        if let userContentController = userContentController {
            paymentResponseHandlerConfiguration.userContentController = userContentController
        }

        webView = WKWebView(frame: view.frame, configuration: paymentResponseHandlerConfiguration)
        guard let webView = webView else { return }
        configureUI(layout: layout)

        if let request = request {
            webView.load(request)
        } else if let html = html {
            load(htmlString: html)
        }
    }

    private func configureUI(layout: WebViewControllerLayout) {
        guard let webView else { return }
        view.addSubview(webView)
        webView.isOpaque = false
        webView.backgroundColor = .clear

        switch layout {
        case .general:
            webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        case .withBanner(let bannerMessage):
            guard let bannerView else { return }
            webView.translatesAutoresizingMaskIntoConstraints = false

            NSLayoutConstraint.activate(
                [
                    webView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                    webView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                    webView.topAnchor.constraint(equalTo: bannerView.bottomAnchor),
                    webView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
                ]
            )
            bannerView.configure(text: bannerMessage)
        }
    }

    func load(htmlString: String) {
        webView?.loadHTMLString(htmlString, baseURL: nil)
    }

    func setCloseButtonEnabled(is enabled: Bool) {
        navigationItem.leftBarButtonItem?.isEnabled = enabled
    }

    @IBAction func closeButtonDidTap(_ sender: UIButton) {
        webView?.stopLoading()
        delegate?.webViewControllerDidCancel(sender: self)
    }
}
