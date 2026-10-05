//
//  SuggestionsView.swift
//  PremierInn
//
//  Created by Marcello Mascia on 18/06/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit
import SimpleNetwork

protocol SuggestionsViewControllerDelegate: AnyObject {
    func suggestionsViewControllerDidCancel(_ sender: SuggestionsViewController)
    func suggestionsViewControllerDidPickSuggestion(_ sender: SuggestionsViewController, suggestion: Suggestion)
}

class SuggestionsViewController: BaseViewController {
    @IBOutlet weak var searchFieldContainer: UIView!
    @IBOutlet weak var searchTextField: PITextField! {
        didSet {
            searchTextField.font = UIFont.Body()
            searchTextField.backgroundColor = .BaseGrey
            searchTextField.attributedPlaceholder = NSAttributedString(
                string: PILocalizedString(
                    "landingSearchPlaceholder",
                    comment: "Landing screen: search text field placeholder"
                ),
                attributes: [NSAttributedString.Key.foregroundColor: UIColor.TintD2]
            )
            searchTextField.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.searchTextField
            searchTextField.accessibilityTraits = .searchField
            searchTextField.textContentType = .location
        }
    }
    @IBOutlet weak var cancelButton: UIButton! {
        didSet {
            cancelButton.titleLabel?.font = UIFont.Action1()
            cancelButton.setTitleColor(.BasePurple, for: .normal)
            cancelButton.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.searchCancelButton
            cancelButton?.setTitle(PILocalizedString("Cancel", comment: ""), for: .normal)
        }
    }
    @IBOutlet weak var suggestionsTableView: UITableView! {
        didSet {
            suggestionsTableView.accessibilityIdentifier = "suggestionsTable"
            suggestionsTableView.tableHeaderView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: 0,
                height: CGFloat.leastNormalMagnitude
            ))
            suggestionsTableView.tableFooterView = UIView(frame: CGRect(
                x: 0,
                y: 0,
                width: 0,
                height: CGFloat.leastNormalMagnitude
            ))
            suggestionsTableView.backgroundColor = .BaseGrey
        }
    }
    @IBOutlet weak var suggestionsErrorLabel: UILabel! {
        didSet {
            suggestionsErrorLabel.font = UIFont.Body()
            suggestionsErrorLabel.text = PILocalizedString(
                "suggestionsNotFound",
                comment: "Suggestions: no suggestions found error message"
            )
            suggestionsErrorLabel.isHidden = true
            suggestionsErrorLabel.accessibilityIdentifier = AccessibilityIdentifiers.Suggestions.noResultText
        }
    }
    @IBOutlet weak var suggestionsActivityIndicator: UIActivityIndicatorView!

    weak var delegate: SuggestionsViewControllerDelegate?
    var suggestionsPresenter: SuggestionsPresenterProtocol?

    override var reachabilityViewPositionY: CGFloat { searchFieldContainer.frame.maxY }
    override var reachabilityAccessibilityIdentifier: String { AccessibilityIdentifiers.Suggestions.noInternetBanner }
    override var screenName: String { PIAnalytics.StateNames.suggestions }
    override var screenType: String { PIAnalytics.StateTypes.lookToBook }
    override var trackScreen: Bool { false }
}

extension SuggestionsViewController {
    override var preferredStatusBarStyle: UIStatusBarStyle {
        .default
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .BaseWhite

        trackState(withName: screenName)

        setupSuggestionsTable()

        suggestionsPresenter?.viewIsReady()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        navigationController?.setNavigationBarHidden(true, animated: animated)
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        searchTextField.resignFirstResponder()
    }

    private func setupSuggestionsTable() {
        suggestionsTableView.registerCellNib(with: SuggestionExpandCell.self)
        suggestionsTableView.registerCellNib(with: SuggestionTableViewCell.self)
        suggestionsTableView.registerHeaderFooterNib(with: SimpleHeaderWithActionLabel.self)

        suggestionsTableView.delegate = suggestionsPresenter?.tableDelegate
        suggestionsTableView.dataSource = suggestionsPresenter?.tableDatasource
    }

    @IBAction func cancelButtonDidTap(_ button: UIButton) {
        delegate?.suggestionsViewControllerDidCancel(self)
    }
}

extension SuggestionsViewController: SuggestionsViewProtocol {
    var parentNavigationController: UINavigationController? { navigationController }

    func showSuggestionsTable() {
        searchTextField.becomeFirstResponder()
    }

    func hideSuggestionsTable() {
    }

    func reloadSection(at indexSet: IndexSet) {
        suggestionsTableView.reloadSections(indexSet, with: .bottom)
    }

    func showLoadingIndicator() {
        suggestionsActivityIndicator.startAnimating()
    }

    func hideLoadingIndicator() {
        suggestionsActivityIndicator.stopAnimating()
    }

    func trackAnalytics(searchTerm: String) {
        var userInfo = PIDictionary()
        userInfo["searchLocation"] = searchTerm
        userInfo["previousScreen"] = "iOS: Hotel Details"
        userInfo["loginStatus"] = "unknown"
        userInfo["visitNumber"] = "???"

        analytics.trackAction(PIAnalytics.Action.remoteSuggestionsInvalidSearch, userInfo: userInfo)
        trackState(withName: PIAnalytics.StateNames.noResults)
    }

    func toggleNoResultsMessage(visible: Bool) {
        suggestionsErrorLabel.isHidden = !visible
    }

    func reloadSuggestionsTable() {
        suggestionsTableView.delegate = suggestionsPresenter?.tableDelegate
        suggestionsTableView.dataSource = suggestionsPresenter?.tableDatasource
        suggestionsTableView.reloadData()
    }

    func suggestionDidSelect(withText text: String?) {
        searchTextField.text = text
        searchTextField.resignFirstResponder()

        if let suggestion = suggestionsPresenter?.selectedSuggestion {
            delegate?.suggestionsViewControllerDidPickSuggestion(self, suggestion: suggestion)
        }
    }

    func presentAlertController(_ controller: UIAlertController) {
        present(controller, animated: true)
    }

    func setSuggestionTitle(_ title: String?) {
        searchTextField.text = title
    }

    func deselectSelectedCellIfAny() {
        if let indexPath = suggestionsTableView.indexPathForSelectedRow {
            suggestionsTableView.deselectRow(at: indexPath, animated: true)
        }
    }

    func showError(message: String) {
        let controller = UIAlertController(title: title, message: message, preferredStyle: .alert)
        controller.addAction(UIAlertAction(
            title: PILocalizedString("OK", comment: "OK button title"),
            style: .cancel,
            handler: nil
        ))

        present(controller, animated: true)
    }
}

extension SuggestionsViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()

        searchTextFieldDidChangeText(textField.text)

        return true
    }

    func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        let textFieldText: NSString = textField.text as NSString? ?? ""
        let text = textFieldText.replacingCharacters(in: range, with: string)

        searchTextFieldDidChangeText(text)

        return true
    }

    func textFieldShouldClear(_ textField: UITextField) -> Bool {
        searchTextFieldDidChangeText(nil)

        return true
    }

    private func searchTextFieldDidChangeText(_ text: String?) {
        suggestionsPresenter?.searchTextDidChange(text ?? "")
    }
}
