//
//  SearchResultsViewController+ViewModel.swift
//  PremierInn
//
//  Created by Marcello Mascia on 24/05/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit
import Formeka

extension SearchResultsViewController {
    func reloadData(response: AvailabilitiesResponse?) {
        guard let response = response else {
            viewModel = TableViewModel<TableViewModelRow>(sections: [])

            if table != nil {
                table.delegate = viewModel
                table.dataSource = viewModel

                table.reloadData()
            }

            return
        }

        var sections = [TableViewModelSection<TableViewModelRow>]()

        if response.hotels.isEmpty {
            sections.append(noHotelsInCloseProximitySection())
        } else {
            if let unavailableHotelsSection = unavailableHotelsSection(hotels: response.hotels) {
                sections.append(unavailableHotelsSection)
            }

            sections.append(hotelsSection(response: response))

            if response.shouldPaginate && !response.hotels.isEmpty && response.hotels.count < response.total {
                sections.append(loadMoreSection())
            }
        }

        if table != nil {
            sections.append(gdprShieldSection(for: table))
        }

        viewModel = TableViewModel<TableViewModelRow>(sections: sections)

        if table != nil {
            table.delegate = viewModel
            table.dataSource = viewModel

            table.reloadData()
        }
    }

    private func unavailableHotelsSection(hotels: [Hotel]) -> TableViewModelSection<TableViewModelRow>? {
        guard let hotelCode = unavailableHotelCode else { return nil }
        guard let hotel = hotels.first(where: { $0.code == hotelCode }) else { return nil }

        let rows = hotelRows(with: [hotel])

        return TableViewModelSection(header: nil, rows: rows, footer: nil)
    }

    private func hotelsSection(response: AvailabilitiesResponse) -> TableViewModelSection<TableViewModelRow> {
        var filteredHotels: [Hotel] {
            guard let hotelCode = unavailableHotelCode else { return response.hotels }

            return response.hotels.filter { $0.code != hotelCode }
        }

        var rows: [TableViewModelRow] = []

        if response.shouldPaginate == false {
            rows.append(TableViewModelRow(cellSetup: { [weak self] indexPath in
                guard let cell: SortCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }

                cell.label.text = PILocalizedString("searchResultsSortingLabel", comment: "Search results: sorting label")
                cell.segmentedControl.setTitle(
                    PILocalizedString("searchResultsSortingDistance", comment: "Search results: sorting distance label"),
                    forSegmentAt: 0
                )
                cell.segmentedControl.setTitle(
                    PILocalizedString("searchResultsSortingPrice", comment: "Search results: sorting price label"),
                    forSegmentAt: 1
                )
                cell.segmentedControl.selectedSegmentIndex = self?.sortingType ?? 0
                cell.delegate = self

                return cell
            }))
        }

        rows += hotelRows(with: filteredHotels)

        return TableViewModelSection(
            header: TableViewModelHeaderFooter(height: 40, viewSetup: { [weak self] _ in
                let view: SimpleHeader? = self?.table.headerFooterView()
                view?.contentView.backgroundColor = .clear
                view?.titleLabel.text = PILocalizedString("searchResultsSectionTitle")
                view?.titleLabel.font = UIFont.premierInnBold(ofSize: 16)
                view?.titleLabel.textColor = .premierInnBlack

                return view
            }),
            rows: rows,
            footer: nil
        )
    }

    private func loadMoreSection() -> TableViewModelSection<TableViewModelRow> {
        let row = TableViewModelRow(tag: "loadMore", cellSetup: { [weak self] indexPath in
            guard let cell: SpinnerCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }
            cell.spinner?.startAnimating()
            cell.titleLabel?.text = nil
            cell.contentLabel?.text = nil
            cell.retryButton?.isHidden = true
            cell.delegate = self

            if let weakSelf = self {
                if weakSelf.shouldShowTimeoutError {
                    cell.spinner?.stopAnimating()
                    cell.titleLabel?.text = PILocalizedString(
                        "searchResultsLoadMoreErrorTitle",
                        comment: "Search results: load more error title"
                    )
                    cell.contentLabel?.text = PILocalizedString(
                        "searchResultsLoadMoreErrorMessage",
                        comment: "Search results: load more error message"
                    )
                    cell.retryButton?.isHidden = false

                    weakSelf.shouldShowTimeoutError = false
                }

                try? weakSelf.loadMoreHotelsIfNeeded()
            }

            return cell
            })

        return TableViewModelSection(header: nil, rows: [row], footer: nil)
    }

    private func noHotelsInCloseProximitySection() -> TableViewModelSection<TableViewModelRow> {
        let row = TableViewModelRow(cellSetup: { [weak self] indexPath in
            guard let cell: ErrorCell = self?.table.dequeueCell(for: indexPath) else { return UITableViewCell() }
            cell.errorLabel.text = PILocalizedString(
                "searchResultsNoHotelsNearbyMessage",
                comment: "Search results: no hotels nearby error message"
            )
            cell.topConstraint.constant = 1
            cell.leftConstraint.constant = -2
            cell.bottomConstraint.constant = 0
            cell.rightConstraint.constant = -2

            return cell
        })

        return TableViewModelSection(header: nil, rows: [row], footer: nil)
    }

    private func hotelRows(with hotels: [Hotel]) -> [TableViewModelRow] {
        hotels.compactMap { hotel -> TableViewModelRow? in
            TableViewModelRow(
                tag: hotel.code,
                cellSetup: { [weak self] indexPath in
                    if hotel.available == false {
                        guard let cell: HotelFullyBookedCell = self?.table.dequeueCell(for: indexPath)
                        else { return UITableViewCell() }
                        cell.setup(hotel: hotel)
                        cell.editDatesButton.addTarget(self, action: #selector(self?.editButtonDidTap), for: .touchUpInside)

                        return cell
                    }

                    guard let cell: HotelPhotoPriceCell = self?.table.dequeueCell(for: indexPath)
                    else { return UITableViewCell() }
                    cell.delegate = self
                    cell.setup(with: hotel)

                    return cell
                },
                didSelect: { [weak self] indexPath in
                guard let weakSelf = self else { return }

                weakSelf.selectedHotel = hotel

                weakSelf.controllerOutput?.resultDidTap(
                    sender: weakSelf,
                    hotel: hotel,
                    suggestion: weakSelf.suggestion,
                    searchIndex: indexPath.row
                )
            })
        }
    }
}
