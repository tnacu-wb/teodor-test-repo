//
//  ForceUpdateView.swift
//  PremierInn
//
//  Created by Freddie Parks on 10/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import UIKit

protocol ForceUpdateViewModel {
    var title: String { get }
    var description: String { get }
    var actionButtonTitle: String { get }
    var actionButtonDescription: String { get }
}

protocol ForceUpdateViewProtocol: AnyObject {
    func update(with viewModel: ForceUpdateViewModel)
}

protocol ForceUpdatePresenterProtocol: AnyObject {
    func viewIsReady()
    func updateButtonDidTap()
}

class ForceUpdateView: BaseViewController {
    var presenter: ForceUpdatePresenterProtocol?

    @IBOutlet weak var forceUpdateTitle: UILabel! {
        didSet {
            forceUpdateTitle.font = .Heading2_Semibold()
            forceUpdateTitle.accessibilityTraits.insert(.header)
        }
    }
    @IBOutlet weak var forceUpdateDescription: UILabel! {
        didSet {
            forceUpdateDescription.font = .Body()
        }
    }
    @IBOutlet weak var updateButton: RoundedCornersButton! {
        didSet {
            updateButton.titleLabel?.font = .Button1()
            updateButton.backgroundColor = .Tint1
            updateButton.setTitleColor(.BaseWhite, for: .normal)
        }
    }
    @IBOutlet weak var updateButtonDescription: UILabel! {
        didSet {
            updateButtonDescription.font = .BodySmall_Semibold()
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = .BasePurple
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        presenter?.viewIsReady()
    }

    @IBAction func updateButtonDidTap(_ sender: Any) {
        presenter?.updateButtonDidTap()
    }
}

extension ForceUpdateView: ForceUpdateViewProtocol {
    func update(with viewModel: ForceUpdateViewModel) {
        forceUpdateTitle.text = viewModel.title
        forceUpdateDescription.text = viewModel.description
        updateButton.setTitle(viewModel.actionButtonTitle, for: .normal)
        updateButtonDescription.text = viewModel.actionButtonDescription
    }
}
