//
//  DashboardFrequentBookingView.swift
//  PremierInn
//
//  Created by Simon Antoine on 12/03/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import UIKit

protocol DashboardFrequentBookingViewDelegate: AnyObject {
    func didLayoutFrequentlyBookedView()
    func selectHotelDate(with dashboardHotelDetails: DashboardHotelDetails)
}

class DashboardFrequentBookingView: UITableViewCell {
    @IBOutlet weak var stayAgainLabel: UILabel! {
        didSet {
            stayAgainLabel.textColor = .BasePurple
            stayAgainLabel.font = UIFont.Heading2_ExtraBold()
        }
    }
    @IBOutlet weak var collectionView: UICollectionView!

    weak var delegate: DashboardFrequentBookingViewDelegate?

    private var array: [FrequentBookingViewModel]?

    override func awakeFromNib() {
        super.awakeFromNib()

        collectionView.delegate = self
        collectionView.dataSource = self
        collectionView.register(
            UINib(nibName: "DashboardFrequentBookingCollectionViewCell", bundle: nil),
            forCellWithReuseIdentifier: "FrequentDashboardCell"
        )
        stayAgainLabel.text = PILocalizedString("Stay again")
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        delegate?.didLayoutFrequentlyBookedView()
    }

    func update(array: [FrequentBookingViewModel]) {
        self.array = array
        self.collectionView.reloadData()
    }
}

extension DashboardFrequentBookingView: UICollectionViewDelegate, UICollectionViewDataSource,
    UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        array?.count ?? 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: "FrequentDashboardCell",
            for: indexPath as IndexPath
        ) as? DashboardFrequentBookingCollectionViewCell else { return UICollectionViewCell() }

        cell.delegate = self
        cell.hotelName.text = array?[indexPath.row].name
        cell.hotelCode = array?[indexPath.row].code

        if let imageURLString = array?[indexPath.row].image, let imageUrl = URL(string: imageURLString) {
            cell.hotelPlaceHolder.af.setImage(withURL: imageUrl, placeholderImage: UIImage(named: "hotelPlaceholder"))
        }

        return cell
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        // SELECT CELL
    }

    func collectionView(
        _ collectionView: UICollectionView,
        layout collectionViewLayout: UICollectionViewLayout,
        sizeForItemAt indexPath: IndexPath
    ) -> CGSize {
        let size = (UIScreen.main.bounds.width - 16) - (array?.count ?? 0 > 1 ? 48 : 16)
        return CGSize.init(width: size, height: 96.0)
    }

    func collectionView(
        _ collectionView: UICollectionView,
        layout collectionViewLayout: UICollectionViewLayout,
        insetForSectionAt section: Int
    ) -> UIEdgeInsets {
        UIEdgeInsets(top: 0, left: 16, bottom: 0, right: 16)
    }
}

extension DashboardFrequentBookingView: DashboardFrequentBookingCollectionViewCellDelegate {
    func selectDatePressed(with identifier: String) {
        guard let dashboardHotelDetails = array?.first(where: { $0.code == identifier })?.dashboardHotelDetails
            else { return }

        delegate?.selectHotelDate(with: dashboardHotelDetails)
    }
}
