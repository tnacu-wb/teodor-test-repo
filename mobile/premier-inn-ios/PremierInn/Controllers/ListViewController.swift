//
//  ListViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol ListItem {
    var title: String { get }
    var associatedObject: Any { get }
}

protocol ListSection {
    var items: [ListItem] { get }
}

protocol ListViewModel: AnyObject {
    var sections: [ListSection]? { get }
    var mainSectionTitle: String? { get }
    var noItemsErrorMessage: String? { get }
    var trackable: Bool? { get }
    var accessibilityPrefix: String? { get }
    var showsSearchBar: Bool { get }

    func preloadData(completion: @escaping () -> Void)
    func selectedItemObject(selectedItem: ListItem?, completion: @escaping (_ object: Any?) -> Void)
    func search(text: String, completion: @escaping (_ shouldShowError: Bool) -> Void)
    func items(forSection: Int) -> [ListItem]
    func item(at indexPath: IndexPath) -> ListItem?
}

class ListViewController: BaseViewController {
    override var trackScreen: Bool { viewModel.trackable ?? false }

    @IBOutlet weak var table: UITableView! {
        didSet {
            table.rowHeight = 55
            table.estimatedRowHeight = 125
            table.rowHeight = UITableView.automaticDimension
            table.backgroundColor = .whiteTwo
            table.tableHeaderView = table.style == .grouped ? UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: table.frame.size.width,
                height: CGFloat.leastNormalMagnitude
            )) : nil

            table.register(
                BorderedContentViewCell.self,
                forCellReuseIdentifier: String(describing: BorderedContentViewCell.self)
            )
        }
    }
    @IBOutlet weak var textField: UITextField! {
        didSet {
            textField.backgroundColor = invertedColours ? .lightestGray : .white
            textField.isHidden = !viewModel.showsSearchBar
        }
    }
    @IBOutlet weak var errorView: UIView! {
        didSet {
            errorView.isHidden = true
        }
    }
    @IBOutlet weak var errorLabel: UILabel! {
        didSet {
            errorLabel.text = nil
            errorLabel.font = UIFont.Body()
            errorLabel.textColor = UIColor.Tint8
        }
    }
    @IBOutlet weak var cancelButton: UIButton! {
        didSet {
            cancelButton.setTitle(PILocalizedString("Cancel", comment: "Title for alert cancel button"), for: .normal)
            cancelButton.setTitleColor(invertedColours ? .BasePurple : .white, for: .normal)
            cancelButton.titleLabel?.font = UIFont.Action1()

            guard let accessibilityPrefixFormat = viewModel.accessibilityPrefix else { return }
            cancelButton.accessibilityIdentifier = String(
                format: accessibilityPrefixFormat,
                AccessibilityIdentifiers.ButtonRowList.cancelButton
            )
        }
    }
    @IBOutlet weak var loadingIndicator: UIActivityIndicatorView!
    @IBOutlet weak var fakeNavigationBarView: UIView! {
        didSet {
            fakeNavigationBarView.backgroundColor = viewModel
                .showsSearchBar ? invertedColours ? .white : .BasePurple : .whiteTwo
        }
    }

    var textFieldPlaceholder: String?
    var textFieldValue: String?
    var shouldUppercaseTextFieldInput = false
    var shouldDelaySearchRequest = true
    var minimumSearchRequestCharacters = 0
    var shouldShowKeyboardOnLoad = false
    var selectedObjectOutput: ((_ sender: UIViewController, _ object: Any) -> Void)?
    var cancelButtonDidTap: ((_ sender: UIViewController) -> Void)?

    private var viewModel: ListViewModel
    private var searchTimer: Timer?
    private var mainSectionTitle: String?
    private let cellBorderWidth: CGFloat = 0.5
    private var isLoadingResults = false
    private var invertedColours: Bool = false

    init(
        viewModel: ListViewModel,
        nibName: String? = String(describing: ListViewController.self),
        invertedColours: Bool = false
    ) {
        self.invertedColours = invertedColours
        self.viewModel = viewModel
        self.mainSectionTitle = viewModel.mainSectionTitle

        super.init(nibName: nibName, bundle: nil)

        setNeedsStatusBarAppearanceUpdate()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = viewModel.showsSearchBar ? view.backgroundColor : .whiteTwo

        textField.placeholder = textFieldPlaceholder
        textField.text = textFieldValue
        textField.autocapitalizationType = shouldUppercaseTextFieldInput ? .allCharacters : .none
        textField.accessibilityIdentifier = "SearchInput"

        loadingIndicator.startAnimating()

        viewModel.preloadData {
            self.table.reloadData()
            self.loadingIndicator.stopAnimating()
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if let searchString = textField.text, searchString.isNotEmpty {
            search(text: searchString)
        } else if shouldShowKeyboardOnLoad {
            textField.becomeFirstResponder()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        textField.resignFirstResponder()
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        invertedColours ? .default : .lightContent
    }

    @IBAction private func cancelButtonDidTap(_ sender: Any) {
        cancelButtonDidTap?(self)
    }
}

extension ListViewController: UITextFieldDelegate {
    func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        let text: NSString = textField.text as NSString? ?? ""
        let searchTerm = text.replacingCharacters(in: range, with: string)

        delayedSearch(text: searchTerm)

        return true
    }

    private func delayedSearch(text: String) {
        searchTimer?.invalidate()
        let timeInterval = shouldDelaySearchRequest ? Constants.remoteSearchDelay : 0
        searchTimer = Timer.scheduledTimer(
            timeInterval: timeInterval,
            target: self,
            selector: #selector(ListViewController.search(timer:)),
            userInfo: text,
            repeats: false
        )
    }

    @objc private func search(timer: Timer) {
        guard let text = timer.userInfo as? String else { return }

        search(text: text)
    }

    func search(text: String) {
        guard text.count >= minimumSearchRequestCharacters else { return }

        loadingIndicator.startAnimating()

        viewModel.search(text: text) { [weak self] shouldShowError in
            self?.table.reloadData()
            self?.loadingIndicator.stopAnimating()

            self?.errorLabel.text = self?.viewModel.noItemsErrorMessage
            self?.errorView.isHidden = !shouldShowError
        }
    }
}

extension ListViewController: UITableViewDelegate, UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        viewModel.sections?.count ?? 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        viewModel.items(forSection: section).count
    }

    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        section == 0 ? mainSectionTitle : nil
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        section == 0 && (mainSectionTitle != nil) ? 45 : 12
    }

    func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        if let header = view as? UITableViewHeaderFooterView {
            header.contentView.backgroundColor = UIColor.whiteTwo
            header.textLabel?.font = UIFont.Heading4_Semibold()
            header.textLabel?.textColor = UIColor.TintD2
            header.textLabel?.text = self.tableView(tableView, titleForHeaderInSection: section)
            header.textLabel?.accessibilityTraits.insert(.header)

            guard let accessibilityPrefixFormat = viewModel.accessibilityPrefix else { return }
            header.textLabel?.accessibilityIdentifier = String(
                format: accessibilityPrefixFormat,
                AccessibilityIdentifiers.ButtonRowList.pageHeader
            )
        }
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell: BorderedContentViewCell = tableView.dequeueCell(for: indexPath) else { return UITableViewCell() }

        switch indexPath.row {
        case 0:
            cell.borders.width = cellBorderWidth

        default:
            cell.borders.width = cellBorderWidth
            cell.borders.top.width = 0.0
        }

        if let item = viewModel.item(at: indexPath) {
            cell.textLabel?.text = item.title

            if let accessibilityPrefixFormat = viewModel.accessibilityPrefix {
                cell.accessibilityIdentifier = String(format: accessibilityPrefixFormat, item.title)
            }
        }

        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let item = viewModel.item(at: indexPath) else { return }

        viewModel.selectedItemObject(selectedItem: item) { object in
            self.selectedObjectOutput?(self, object ?? item.associatedObject)
        }
    }
}

extension ListViewController {
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        textField.resignFirstResponder()
    }
}

extension ListViewModel {
    func selectedItemObject(selectedItem: ListItem?, completion: @escaping (_ object: Any?) -> Void) {
        completion(nil)
    }
}
