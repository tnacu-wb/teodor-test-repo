//
//  PostCodeViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 09/11/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol PostCodeDelegate: class {
    func selectedAddress(sender: UIViewController, address: Address)
}

class PostCodeViewController: BaseViewController {
    @IBOutlet var table: UITableView! {
        didSet {
            table.rowHeight = 55
            table.registerNibForCell(String(describing: CountryCell.self))
        }
    }
    @IBOutlet weak var searchTextField: UITextField! {
        didSet {
            searchTextField.placeholder = PILocalizedString("post code", comment: "Post code search input placeholder")
        }
    }
    @IBOutlet weak var cancelButton: UIButton! {
        didSet {
            cancelButton.setTitle(PILocalizedString("Cancel", comment: "Cancel button title"), for: .normal)
        }
    }
    @IBOutlet weak var loadingIndicator: UIActivityIndicatorView!

    weak var delegate: PostCodeDelegate?
    private var viewModel: PostCodeViewModel?
    private let cellBorderWidth: CGFloat = 0.5
    private var postCode: String?
    private var searchTimer: Timer?

    deinit {
        searchTimer?.invalidate()
    }

    convenience init(postCode: String?) {
        self.init(nibName: String(describing: PostCodeViewController.self), bundle: nil)

        self.postCode = postCode
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        viewModel = PostCodeViewModel()
        viewModel?.delegate = self

        if let postCode = postCode {
            searchTextField.text = postCode
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if let postCode = postCode, postCode.isEmpty == false {
            searchWithText(text: postCode)
        } else {
            searchTextField.becomeFirstResponder()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        searchTextField.resignFirstResponder()
    }


    // MARK: - Actions


    private func searchWithText(text: String) {
        searchTimer?.invalidate()
        searchTimer = Timer.scheduledTimer(
            timeInterval: Constants.remoteSearchDelay,
            target: self,
            selector: #selector(PostCodeViewController.search(timer:)),
            userInfo: text,
            repeats: false
        )
    }

    @objc func search(timer: Timer) {
        guard let text = timer.userInfo as? String else { return }
        guard text.length > Constants.minimumCharactersForPostCodeLookup else { return }

        loadingIndicator.startAnimating()
        viewModel?.search(text: text)
    }

    @IBAction func cancelButtonDidTap(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }
}

extension PostCodeViewController: UITextFieldDelegate {
    func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        let text: NSString = textField.text as NSString? ?? ""
        let searchTerm = text.replacingCharacters(in: range, with: string)

        searchWithText(text: searchTerm)

        return true
    }
}

extension PostCodeViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        viewModel?.results?.count ?? 0
    }

    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        section == 0 ? PILocalizedString("Look up address", comment: "") : nil
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        section == 0 ? 45 : 12
    }

    func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        if let header = view as? UITableViewHeaderFooterView {
            header.contentView.backgroundColor = UIColor.white
            header.textLabel?.font = UIFont.premierInnBold(ofSize: 16.0)
            header.textLabel?.textColor = .premierInnHeader
        }
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(
            withIdentifier: String(describing: CountryCell.self),
            for: indexPath
        ) as? CountryCell else { return UITableViewCell() }

        switch indexPath.row {
        case 0:
            cell.borders.width = cellBorderWidth

        default:
            cell.borders.width = cellBorderWidth
            cell.borders.top.width = 0.0
        }

        if let address = viewModel?.results?[indexPath.row] {
            cell.name.text = address.label ?? address.description
        }

        return cell
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let address = viewModel?.results?[indexPath.row] {
            searchTextField?.resignFirstResponder()
            delegate?.selectedAddress(sender: self, address: address)

            dismiss(animated: true, completion: nil)
        }
    }
}

extension PostCodeViewController: UIScrollViewDelegate {
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {
        searchTextField.resignFirstResponder()
    }
}

extension PostCodeViewController: PostCodeViewModelDelegate {
    func resultsDidLoad() {
        loadingIndicator.stopAnimating()
        table.reloadData()
    }
}
