//
//  DatatransPaymentView.swift
//  PremierInn
//
//  Copyright © 2025 Whitbread. All rights reserved.
//

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

// MARK: - DatatransPaymentViewProtocol

extension DatatransPaymentView: DatatransPaymentViewProtocol {
    func showLoading() {
        activityIndicator.startAnimating()
    }

    func hideLoading() {
        activityIndicator.stopAnimating()
    }
}
