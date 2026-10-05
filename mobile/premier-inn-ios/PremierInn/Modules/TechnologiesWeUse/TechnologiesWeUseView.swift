//
//  TechnologiesWeUseViewController.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import WebKit

class TechnologiesWeUseViewController: WebViewController {
    var eventHandler: TechnologiesWeUseViewEventHandler?

    private static let buttonHeight: CGFloat = 44
    private static let footerHeight: CGFloat = buttonHeight + 20
    private static let buttonLeftMargin: CGFloat = 10
    private static let buttonTopMargin: CGFloat = 10

    private var footerView: UIView?

    init(with viewModel: TechnologiesWeUseViewModel) {
        super.init(html: viewModel.html, screenName: viewModel.screenName, screenType: viewModel.screenType)
        self.title = viewModel.title
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    deinit {
        print("DEINIT: \(self)")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        eventHandler?.viewHasLoaded()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        eventHandler?.viewHasFinishedLayout()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        webView?.navigationDelegate = self

        eventHandler?.viewIsAppearing(withAnimation: animated)
    }

    @objc private func buttonDidTap() {
        eventHandler?.userDidAccept()
    }

    func setUpFooter(withButtonTitle buttonTitle: String) {
        let footerRect = CGRect(
            x: 0,
            y: view.bounds.height - TechnologiesWeUseViewController.footerHeight - view.safeAreaInsets.bottom,
            width: view.bounds.width,
            height: TechnologiesWeUseViewController.footerHeight + view.safeAreaInsets.bottom
        )

        footerView = UIView(frame: footerRect)
        footerView?.backgroundColor = .white
        footerView?.autoresizingMask = [.flexibleWidth, .flexibleTopMargin]

        if let footerView = footerView {
            view.addSubview(footerView)

            var buttonRect = footerView.bounds.insetBy(
                dx: TechnologiesWeUseViewController.buttonLeftMargin,
                dy: TechnologiesWeUseViewController.buttonTopMargin
            )
            buttonRect.size.height = TechnologiesWeUseViewController.buttonHeight

            let button = FadeOnHighlightButton(frame: buttonRect)
            button.cornerRadius = 6
            button.titleLabel?.font = UIFont.Button1()
            button.accessibilityIdentifier = "technologiesGDPRAcceptAcc"
            button.setTitle(buttonTitle, for: .normal)
            button.addTarget(self, action: #selector(buttonDidTap), for: .touchUpInside)
            button.backgroundColor = .BasePurple
            button.autoresizingMask = [.flexibleWidth, .flexibleHeight]

            footerView.addSubview(button)
        }
    }

    func configureWebViewScrollView() {
        webView?.scrollView.contentInset.bottom = TechnologiesWeUseViewController.footerHeight
        webView?.scrollView.verticalScrollIndicatorInsets.bottom = TechnologiesWeUseViewController.footerHeight
    }
}

extension TechnologiesWeUseViewController: WKNavigationDelegate {
    func webView(
        _ webView: WKWebView,
        decidePolicyFor navigationAction: WKNavigationAction,
        decisionHandler: ((WKNavigationActionPolicy) -> Void)
    ) {
        if let url = navigationAction.request.url, navigationAction.navigationType == .linkActivated,
           UIApplication.shared.canOpenURL(url) {
            decisionHandler(.cancel)
            openURLInSafari(url: url)
            return
        }

        decisionHandler(.allow)
    }
}

extension TechnologiesWeUseViewController: TechnologiesWeUseViewProtocol {
    func configureLeftNavigationBar(withTitle title: String) {
        navigationItem.leftBarButtonItem = isModal() ? UIBarButtonItem(
            title: title,
            style: .plain,
            target: self,
            action: #selector(closeButtonDidTap)
        ) : nil
    }

    func addFooter(withButtonTitle buttonTitle: String) {
        guard footerView == nil else { return }

        self.setUpFooter(withButtonTitle: buttonTitle)
    }

    func showNavigationBar(withAnimation animated: Bool) {
        navigationController?.setNavigationBarHidden(false, animated: animated)
    }

    func performAdditionalLayoutSetup() {
        self.configureWebViewScrollView()
    }
}
