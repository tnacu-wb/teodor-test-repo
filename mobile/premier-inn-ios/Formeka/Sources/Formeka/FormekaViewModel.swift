//
//  FormekaViewModel.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 23/01/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import UIKit

public typealias TableRowCellSetup = (_ indexPath: IndexPath, _ row: FormekaModelRow, _ table: UITableView) -> UITableViewCell?
public typealias TableRowWillDisplay = (_ cell: UITableViewCell, _ indexPath: IndexPath, _ row: FormekaModelRow) -> Void
public typealias TableRowCellSelection = (_ indexPath: IndexPath, _ row: FormekaModelRow) -> Void
public typealias TableRowCellDeletion = (_ indexPath: IndexPath) -> Void
public typealias TableHeaderFooterSetup = (_ section: Int, _ table: UITableView) -> UITableViewHeaderFooterView?
public typealias TableHeaderFooterWillDisplay = (_ view: UIView, _ index: Int) -> Void
public typealias JsonDictionary = [String: Any]

public protocol FormekaViewModelDelegate: AnyObject {

    func tableViewWillBeginDragging(scrollView: UIScrollView)
    func tableViewDidEndScrollingAnimation(scrollView: UIScrollView)
    func scrollViewDidScroll(scrollView: UIScrollView)
}

extension FormekaViewModelDelegate {

    public func tableViewWillBeginDragging(scrollView: UIScrollView) { }
    public func tableViewDidEndScrollingAnimation(scrollView: UIScrollView) { }
    public func scrollViewDidScroll(scrollView: UIScrollView) { }
}

public class FormekaModelHeaderFooter {

	public var height: CGFloat
    let viewSetup: TableHeaderFooterSetup
	let viewWillDisplay: TableHeaderFooterWillDisplay?

	public init(height: CGFloat, viewSetup: @escaping TableHeaderFooterSetup, viewWillDisplay: TableHeaderFooterWillDisplay? = nil) {

		self.height = height
        self.viewSetup = viewSetup
		self.viewWillDisplay = viewWillDisplay
    }
}

public class FormekaModelSection {

    public var header: FormekaModelHeaderFooter?
    public var rows: [FormekaModelRow]
    public var footer: FormekaModelHeaderFooter?

    public init(header: FormekaModelHeaderFooter?, rows: [FormekaModelRow], footer: FormekaModelHeaderFooter?) {

        self.header = header
        self.rows = rows
        self.footer = footer
    }
}

public class FormekaViewModel: NSObject {

    public weak var delegate: FormekaViewModelDelegate?

    var isLocked = false

    public var sections: [FormekaModelSection]

    public init(sections: [FormekaModelSection]) {

        self.sections = sections
    }

    public func row(at indexPath: IndexPath) -> FormekaModelRow? {

        guard indexPath.section < sections.count else { return nil }

        let section = sections[indexPath.section]

        guard indexPath.row < section.rows.count else { return nil }

        return section.rows[indexPath.row]
    }

    public func row(named tag: String) -> FormekaModelRow? {

        for section in sections {
            for row in section.rows where row.tag == tag {
                return row
            }
        }

        return nil
    }

    public func rows(named tag: String) -> [FormekaModelRow] {

        var rows = [FormekaModelRow]()

        for section in sections {
            for row in section.rows where row.tag == tag {
                rows.append(row)
            }
        }

        return rows
    }

    public func indexPath(for row: FormekaModelRow) -> IndexPath? {

        for (sectionIndex, section) in sections.enumerated() {
            for (rowIndex, aRow) in section.rows.enumerated() where row == aRow {
                return IndexPath(item: rowIndex, section: sectionIndex)
            }
        }

        return nil
    }

    public func indexPaths(for rows: [FormekaModelRow]) -> [IndexPath] {

		return rows.compactMap { indexPath(for: $0) }
    }

    public func indexPath(forRowNamed name: String) -> IndexPath? {

        guard let row = row(named: name) else { return nil }
        guard let indexPath = indexPath(for: row) else { return nil }

        return indexPath
    }

    public func indexPaths(forRowNamed name: String) -> [IndexPath] {

        return indexPaths(for: rows(named: name))
    }

    public func add(row: FormekaModelRow, at indexPath: IndexPath) {

        if self.row(at: indexPath) != nil {
            let aSection = sections[indexPath.section]
            aSection.rows.insert(row, at: indexPath.row)
        } else if indexPath.section < sections.count {
            let aSection = sections[indexPath.section]
            aSection.rows.append(row)
        }
    }

    public func remove(row: FormekaModelRow) -> IndexPath? {

        guard let indexPath = indexPath(for: row) else { return nil }

        let section = sections[indexPath.section]
        section.rows.remove(at: indexPath.row)

        return indexPath
    }

    public func remove(rowNamed name: String) -> IndexPath? {

        guard let row = row(named: name) else { return nil }
        guard let removedIndexPath = remove(row: row) else { return nil }

        return removedIndexPath
    }

    public func remove(sectionAtIndex index: Int) {

        sections.remove(at: index)
    }

    public func add(section: FormekaModelSection, index: Int) {

        sections.insert(section, at: index)
    }
}

extension FormekaViewModel: UIScrollViewDelegate {

	public func scrollViewWillBeginDragging(_ scrollView: UIScrollView) {

		delegate?.tableViewWillBeginDragging(scrollView: scrollView)
	}

	public func scrollViewDidEndScrollingAnimation(_ scrollView: UIScrollView) {

		delegate?.tableViewDidEndScrollingAnimation(scrollView: scrollView)
	}

	public func scrollViewDidScroll(_ scrollView: UIScrollView) {

		delegate?.scrollViewDidScroll(scrollView: scrollView)
	}
}

extension FormekaViewModel: UITableViewDelegate, UITableViewDataSource {

	public func numberOfSections(in tableView: UITableView) -> Int {

		return sections.count
	}

	public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

		return sections[section].rows.count
	}

	public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

		guard let row = row(at: indexPath) else { return UITableViewCell() }

		return row.cellSetup(indexPath, row, tableView) ?? UITableViewCell()
	}

	public func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {

		guard let row = row(at: indexPath) else { return }

		row.cellWillDisplay?(cell, indexPath, row)
	}

	public func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {

		return sections[section].header?.height ?? CGFloat.leastNormalMagnitude
	}

	public func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {

		return sections[section].footer?.height ?? CGFloat.leastNormalMagnitude
	}

	public func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {

		return sections[section].header?.viewSetup(section, tableView)
	}

	public func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {

		sections[section].header?.viewWillDisplay?(view, section)
	}

	public func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {

		return sections[section].footer?.viewSetup(section, tableView)
	}

	public func tableView(_ tableView: UITableView, willDisplayFooterView view: UIView, forSection section: Int) {

		sections[section].footer?.viewWillDisplay?(view, section)
	}

	public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {

		guard let row = row(at: indexPath) else { return }

		row.didSelect?(indexPath, row)
	}

	public func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {

		return row(at: indexPath)?.editable ?? false
	}

	public func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {

		if editingStyle == .delete {
			row(at: indexPath)?.deleteAction?(indexPath)
		}
	}

}
