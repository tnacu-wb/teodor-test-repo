//
//  GuestEditDateCell.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 28.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//


import UIKit
import Formeka

struct DateOfBirthCellModel {
    init(date: Date? = nil, error: String?) {
        self.date = date
        self.error = error
    }
    var date: Date?
    var error: String?
}

final class GuestEditDateCell: FormekaDateCell {
    static let reuseIdentifier = String(describing: GuestEditDateCell.self)

    override var errorMessage: String? {
        didSet {
            if let errorMessage {
                errorMessageLabel.isHidden = false
                errorMessageLabel.text = errorMessage
            } else {
                errorMessageLabel.isHidden = true
                errorMessageLabel.text = nil
            }
        }
    }

    private lazy var datePicker = UIDatePicker()
    private lazy var toolbar = UIToolbar()

    private lazy var errorMessageLabel: UIPaddingLabel = {
        let label = UIPaddingLabel(frame: .zero, padding: .init(top: 0, left: 0, bottom: 12, right: 0))
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .Tint8
        label.font = .BodySmall()
        return label
    }()

    private lazy var stackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 16
        stackView.axis = .vertical
        stackView.alignment = .fill
        stackView.distribution = .equalSpacing
        return stackView
    }()

    private lazy var dateComponentsStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.spacing = 20
        stackView.axis = .horizontal
        stackView.distribution = .fillEqually
        stackView.heightAnchor.constraint(equalToConstant: 60).isActive = true
        stackView.alignment = .center
        return stackView
    }()

    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textColor = .TintD1
        label.text = PILocalizedString("guestEditDateOfBirthTitle")
        label.font = .BodySmall()
        return label
    }()

    private lazy var dayView = DateComponentView()
    private lazy var monthView = DateComponentView()
    private lazy var yearView = DateComponentView()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        setupViews()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupViews()
    }

    func configure(model: DateOfBirthCellModel) {
        errorMessage = model.error
        guard let date = model.date else {
            dayView.configure(value: PILocalizedString(GuestDateComponent.day.localisation))
            monthView.configure(value: PILocalizedString(GuestDateComponent.month.localisation))
            yearView.configure(value: PILocalizedString(GuestDateComponent.year.localisation))
            return
        }

        datePicker.date = date
        let components = date.get(.day, .month, .year)
        if let day = components.day {
            dayView.configure(value: "\(day)")
        }
        if let month = components.month {
            monthView.configure(value: "\(month)")
        }
        if let year = components.year {
            yearView.configure(value: "\(year)")
        }
    }

    private func setupViews() {
        let spacer = UIView()
        spacer.setContentHuggingPriority(.defaultLow, for: .horizontal)
        contentView.addSubview(stackView)
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 0),
            stackView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            stackView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -8)
        ])
        datePicker.preferredDatePickerStyle = .wheels
        datePicker.maximumDate = Date()
        datePicker.datePickerMode = .date

        toolbar.sizeToFit()
        let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(doneTapped))
        let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        toolbar.setItems([space, doneButton], animated: false)
        dayView.compTextfield.inputView = datePicker
        dayView.compTextfield.inputAccessoryView = toolbar

        monthView.compTextfield.inputView = datePicker
        monthView.compTextfield.inputAccessoryView = toolbar

        yearView.compTextfield.inputView = datePicker
        yearView.compTextfield.inputAccessoryView = toolbar

        stackView.addArrangedSubview(titleLabel)

        dateComponentsStackView.addArrangedSubview(dayView)
        dateComponentsStackView.addArrangedSubview(monthView)
        dateComponentsStackView.addArrangedSubview(yearView)

        stackView.addArrangedSubview(dateComponentsStackView)
        stackView.addArrangedSubview(errorMessageLabel)
    }

    @objc func doneTapped() {
        let components = datePicker.date.get(.day, .month, .year)
        if let day = components.day {
            dayView.configure(value: "\(day)")
        }
        if let month = components.month {
            monthView.configure(value: "\(month)")
        }
        if let year = components.year {
            yearView.configure(value: "\(year)")
        }
        endEditing(true)
        delegate?.didPickDate(date: datePicker.date, cell: self)
    }
}


extension Date {
    func get(_ components: Calendar.Component..., calendar: Calendar = Calendar.current) -> DateComponents {
        calendar.dateComponents(Set(components), from: self)
    }

    func get(_ component: Calendar.Component, calendar: Calendar = Calendar.current) -> Int {
        calendar.component(component, from: self)
    }
}

class DateComponentView: UIView {
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }


    private func setup() {
        translatesAutoresizingMaskIntoConstraints = false
        layer.borderColor = UIColor.TintL1.cgColor
        layer.borderWidth = 1.0
        layer.cornerRadius = 4.0
        heightAnchor.constraint(equalToConstant: 60).isActive = true
        addSubview(compTextfield)
        NSLayoutConstraint.activate([
            compTextfield.topAnchor.constraint(equalTo: topAnchor, constant: 8),
            compTextfield.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 12),
            compTextfield.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8),
            compTextfield.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -8)
        ])
    }

    lazy var compTextfield: UITextField = {
        let tf = UITextField()
        tf.translatesAutoresizingMaskIntoConstraints = false
        tf.textAlignment = .left
        tf.delegate = self
        return tf
    }()

    func configure(value: String) {
        compTextfield.text = value
    }
}

extension DateComponentView: UITextFieldDelegate {
    func textField(
        _ textField: UITextField,
        shouldChangeCharactersIn range: NSRange,
        replacementString string: String
    ) -> Bool {
        false
    }
}

enum GuestDateComponent {
    case day
    case month
    case year

    var localisation: String {
        switch self {
        case .day: "guestEditDateDay"
        case .month: "guestEditDateMonth"
        case .year: "guestEditDateYear"
        }
    }
}
