//
//  PaymentMethodsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 23/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import Formeka
import SimpleNetwork

struct PaymentMethodsLink {
    let title: String
    let color: UIColor
	let action: PaymentMethodsLinkAction
}

enum PaymentMethodsLinkAction {
	case edit
	case delete
}

enum PaymentMethodsViewRow: String {
    case savedCardSyncInfo
    case paymentMethodsInfo
}

protocol PaymentMethodsPresenterProtocol {
    var paymentMethodsTracking: PaymentMethodsTracking { get }

    func viewIsReady()
	func linkCellDidSelect(with action: PaymentMethodsLinkAction, section: PaymentMethodsCardSection)
    func selectedPaymentCard(at index: Int)
    func ctaDidTap()
}

class PaymentMethodsViewController: FormekaViewController {
    override var screenName: String {
        presenter?.paymentMethodsTracking.staName ?? super.screenName
    }
    override var screenType: String {
        presenter?.paymentMethodsTracking.screenType ?? super.screenType
    }

    @IBOutlet weak var ctaButton: UIButton! {
        didSet {
            ctaButton.titleLabel?.font = .Heading3_Semibold()
            ctaButton.tintColor = .BasePurple
            ctaButton.backgroundColor = .whiteTwo
            ctaButton.layer.cornerRadius = 5
            ctaButton.layer.borderColor = UIColor.BasePurple.cgColor
            ctaButton.layer.borderWidth = 1
        }
    }

    override var customParameters: [String: Any]? { customAnalyticsParameters }

    var customAnalyticsParameters: PIDictionary?

    var presenter: PaymentMethodsPresenterProtocol?

    override func viewDidLoad() {
        super.viewDidLoad()

        if table != nil {
            setupTableView()
        }

        presenter?.viewIsReady()
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        adjustTableFooterHeight()
    }

    private func setupTableView() {
        table.accessibilityIdentifier = "tablePaymentMethods"
        table.contentInset = UIEdgeInsets(top: 8, left: 0, bottom: 0, right: 0)
        table.estimatedRowHeight = 100
        table.rowHeight = UITableView.automaticDimension
        table.backgroundColor = .BaseWhite
        table.tableHeaderView = UIView(frame: CGRect(
            x: 0,
            y: 0,
            width: table.frame.size.width,
            height: CGFloat.leastNormalMagnitude
        ))
        table.tableFooterView = UIView(frame: CGRect(
            x: 0,
            y: 0,
            width: CGFloat.leastNormalMagnitude,
            height: CGFloat.leastNormalMagnitude
        ))
        table.separatorStyle = .none
        table.separatorColor = .ColourLD3

        table.registerCellNib(with: PaymentMethodCell.self)
        table.registerCellNib(with: SimpleActionCell.self)
        table.registerCellNib(with: FlexibleContentInformationCell.self)
        table.register(UITableViewCell.self, forCellReuseIdentifier: String(describing: UITableViewCell.self))
        table.registerHeaderFooterNib(with: FormekaIconFooter.self)
    }

    func showError(title: String?, message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(
            title: PILocalizedString("OK", comment: "OK button title"),
            style: .cancel,
            handler: nil
        ))

        present(controller, animated: true, completion: nil)
    }

    @objc private func cancelButtonDidTap() {
        dismiss(animated: true)
    }

    @IBAction func ctaButtonDidTap(_ sender: Any) {
        presenter?.ctaDidTap()
    }
}

extension PaymentMethodsViewController: PaymentMethodsViewProtocol {
    func setTitle(_ title: String?) {
        self.title = title
    }

    func loadViewModel(sections: [PaymentMethodsCardSection], ctaTitle: String?, infoFooterMessage: String?) {
        viewModel = FormekaViewModel(sections: viewModelSections(
            sections: sections,
            ctaTitle: ctaTitle,
            footerMessage: infoFooterMessage
        ))
        viewModel?.delegate = self

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel
        }

        table.reloadData()
    }

	func askForConfirmation(
	    title: String,
	    message: String,
	    cancelButtonTitle: String,
	    confirmButtonTitle: String,
	    completion: @escaping (Bool) -> Void
	) {
		let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
		controller.addAction(UIAlertAction(title: cancelButtonTitle, style: .cancel) { _ in
			completion(false)
		})
		controller.addAction(UIAlertAction(title: confirmButtonTitle, style: .destructive) { _ in
			completion(true)
		})

		present(controller, animated: true, completion: nil)
	}

	func dismiss() {
		navigationController?.popViewController(animated: true)
	}

    func handle(error: Error) {
        BARTDowntimeHandler.handle(error: error, withParentNavigationController: self.navigationController)

		let controller = UIAlertController(title: title, message: error.localizedDescription, preferredStyle: .alert)
		controller.addAction(UIAlertAction(
		    title: PILocalizedString("OK", comment: "OK button title"),
		    style: .cancel,
		    handler: nil
		))

		present(controller, animated: true, completion: nil)
	}

    func showCancelButton() {
        navigationItem.leftBarButtonItem = UIBarButtonItem(
            barButtonSystemItem: .cancel,
            target: self,
            action: #selector(cancelButtonDidTap)
        )
    }
}

extension PaymentMethodsViewController {
    private func viewModelSections(
        sections: [PaymentMethodsCardSection],
        ctaTitle: String? = nil,
        footerMessage: String?
    ) -> [FormekaModelSection] {
        var formekaSections: [FormekaModelSection] = []

        if let ctaTitle = ctaTitle {
            ctaButton.setTitle(ctaTitle, for: .normal)
            ctaButton.isHidden = false
        }

        formekaSections.append(savedCardSyncInfoSection)
        formekaSections.append(contentsOf: sections.map { simpleSection(with: $0) })
        if let footer = footerMessageRow(message: footerMessage) {
            formekaSections.append(footer)
        }
        return formekaSections
    }

    private var savedCardSyncInfoSection: FormekaModelSection {
        FormekaModelSection(header: nil, rows: [savedCardSyncInfoRow], footer: nil)
    }

    private var savedCardSyncInfoRow: FormekaModelRow {
        iconInfoRow(
            tag: PaymentMethodsViewRow.savedCardSyncInfo.rawValue,
            text: PILocalizedString("myAccountSavedCardSyncInfo"),
            backgroundColor: .BaseWhite,
            topPadding: 8,
            style: .info
        )
    }

    private func footerMessageRow(message: String?) -> FormekaModelSection? {
        guard let message else { return nil }

        let row = iconInfoRow(
            tag: PaymentMethodsViewRow.paymentMethodsInfo.rawValue,
            text: message
        )
        return FormekaModelSection(header: nil, rows: [row], footer: nil)
    }

    private func simpleSection(with section: PaymentMethodsCardSection) -> FormekaModelSection {
        var rows = [FormekaModelRow]()

        rows.append(FormekaModelRow(cellSetup: { [weak self] indexPath, _, table in
            guard let self,
                  let cell: PaymentMethodCell = table.dequeueCell(for: indexPath) else {
                return nil
            }

            let cardAccessibilityLabel = paymentMethodAccessibilityLabel(for: section)

            cell.configure(
                with: section,
                accessibilityLabel: cardAccessibilityLabel,
                onDelete: { [weak self] in
                    self?.presenter?.linkCellDidSelect(with: .delete, section: section)
                },
                onLinkSelected: { [weak self] action in
                    self?.presenter?.linkCellDidSelect(with: action, section: section)
                }
            )

            return cell
        }, didSelect: { [weak self] indexPath, _ in
            guard section.selectable else { return }
            self?.presenter?.selectedPaymentCard(at: indexPath.section)
        }))

        return FormekaModelSection(header: nil, rows: rows, footer: nil)
    }

    private func paymentMethodAccessibilityLabel(for section: PaymentMethodsCardSection) -> String {
        if let accessibilityLabel = nonEmpty(section.accessibilityLabel) {
            return accessibilityLabel
        }

        return [
            nonEmpty(section.cardName),
            nonEmpty(section.cardHiddenNumber),
            nonEmpty(section.cardExpiration),
            nonEmpty(section.usageDescription)
        ].compactMap { $0 }.joined(separator: ", ")
    }

    private func nonEmpty(_ string: String?) -> String? {
        let trimmedString = string?.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedString?.isEmpty == false ? trimmedString : nil
    }

    private var simpleFooter: FormekaModelHeaderFooter {
        FormekaModelHeaderFooter(height: 10, viewSetup: { _, table in
            let view: SimpleFooter? = table.headerFooterView()
            view?.contentView.backgroundColor = .whiteTwo
            view?.lineView.backgroundColor = .TintL2

            return view
        })
    }

    private func infoFooter(with message: String?) -> FormekaIconFooter? {
        let view: FormekaIconFooter? = table.headerFooterView()
        view?.grayAreaView.isHidden = true
        view?.grayAreaView.backgroundColor = .red
        view?.lineView.backgroundColor = .green
        view?.contentView.backgroundColor = .whiteTwo

        view?.imageView.image = #imageLiteral(resourceName: "UNKNOWN")
        view?.imageView.tintColor = .BasePurple
        view?.imageView.isHidden = message == nil || (message?.isEmpty) != nil

        view?.titleLabel.text = message
        view?.titleLabel.textColor = .TintD1
        view?.titleLabel.font = .BodySmall()

        return view
    }

    private func adjustTableFooterHeight() {
        guard let footerView = table.tableFooterView else { return }

        let height = footerView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height

        // Comparison necessary to avoid infinite loop
        guard height != footerView.frame.height else { return }

        footerView.frame.size.height = height
        table.tableFooterView = footerView
    }
}
