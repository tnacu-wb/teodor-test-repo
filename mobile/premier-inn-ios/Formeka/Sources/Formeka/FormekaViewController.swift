//
//  FormekaViewController.swift
//  PremierInn
//
//  Created by Marcello Mascia on 11/02/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public struct FormekaTextFieldTraits {

    public var autocapitalizationType: UITextAutocapitalizationType = .none
    public var autocorrectionType: UITextAutocorrectionType = .no
    public var spellCheckingType: UITextSpellCheckingType = .no
    public var keyboardType: UIKeyboardType = .default
    public var keyboardAppearance: UIKeyboardAppearance = .default
    public var returnKeyType: UIReturnKeyType = .default
    public var enablesReturnKeyAutomatically: Bool = true
    public var secureTextEntry: Bool = false
    public var textContentType: UITextContentType?
    public var placeholder: String?

    public init() {

    }
}

open class FormekaViewController: UIViewController {

    @IBOutlet public weak var table: UITableView! {
        didSet {
            table.rowHeight = UITableView.automaticDimension
            table.estimatedRowHeight = 200
        }
    }

    public var viewModel: FormekaViewModel?
    public var isLocked = false {
        didSet {
            viewModel?.isLocked = isLocked

            if isLocked {
                table.isUserInteractionEnabled = false
                table.alpha = 0.5
            } else {
                table.isUserInteractionEnabled = true
                table.alpha = 1
            }
        }
    }

    var scrollCompletion: (() -> Void)?
    open var scrollViewScrolled: ((_ scrollView: UIScrollView) -> Void)?

    deinit {
		NotificationCenter.default.removeObserver(self)
        #if DEBUG
        print("DEINIT \(self)")
        #endif
    }

    override open func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardDidShow), name: UIResponder.keyboardDidShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: UIResponder.keyboardWillHideNotification, object: nil)

        if let indexPath = table.indexPathForSelectedRow {
            table.deselectRow(at: indexPath, animated: animated)
        }
    }

    override open func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)

        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
		NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardDidShowNotification, object: nil)
        NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
    }
}

extension FormekaViewController {

    @objc fileprivate func endEditing() {

        view.endEditing(true)
    }

    @objc open func keyboardWillShow(notification: Notification) {

        guard let keyboardRect = notification.userInfo?[UIResponder.keyboardFrameBeginUserInfoKey] as? CGRect else { return }

        table.contentInset.bottom = keyboardRect.height
        table.verticalScrollIndicatorInsets.bottom = keyboardRect.height

        centerActiveCell(keyboardRect: keyboardRect)
    }

    @objc func keyboardDidShow(notification: Notification) {

    }

    @objc open func keyboardWillHide(notification: Notification) {

        table.contentInset.bottom = view.safeAreaInsets.bottom
        table.verticalScrollIndicatorInsets.bottom = view.safeAreaInsets.bottom
    }

	public func scrollAndFocus(at row: FormekaModelRow, completion: ((_ cell: FormekaErrorCell) -> Void)? = nil) {

		scrollAndFocus(at: viewModel?.indexPath(for: row), completion: completion)
	}

    public func scrollAndFocus(at indexPath: IndexPath?, completion: ((_ cell: FormekaErrorCell) -> Void)? = nil) {

        guard let indexPath = indexPath else { return }

        scrollCompletion = {
            let cell = self.table.cellForRow(at: indexPath)
            cell?.becomeFirstResponder()

            guard let errorCell = cell as? FormekaErrorCell else { return }

            completion?(errorCell)
        }

        let rect = table.rectForRow(at: indexPath)
        let cellIsVisible = rect.origin.y >= table.contentOffset.y

        table.reloadData()

        if cellIsVisible {
            scrollCompletion?()
            scrollCompletion = nil
        } else {
            table.scrollToRow(at: indexPath, at: .top, animated: true)
        }
    }

    private func centerActiveCell(keyboardRect: CGRect) {

		guard let cell = table.visibleCells.first(where: { $0.isFirstResponder }) else { return }

        var frame = cell.frame
        frame.size.height = table.frame.height - keyboardRect.height
        frame.origin.y -= frame.height / 2 - cell.frame.height / 2

        table.scrollRectToVisible(frame, animated: false)
    }
}

extension FormekaViewController: FormekaViewModelDelegate {

    public func tableViewWillBeginDragging(scrollView: UIScrollView) {

        endEditing()
    }

    public func tableViewDidEndScrollingAnimation(scrollView: UIScrollView) {

        scrollCompletion?()
        scrollCompletion = nil
    }

    public func scrollViewDidScroll(scrollView: UIScrollView) {

        scrollViewScrolled?(scrollView)
    }
}
