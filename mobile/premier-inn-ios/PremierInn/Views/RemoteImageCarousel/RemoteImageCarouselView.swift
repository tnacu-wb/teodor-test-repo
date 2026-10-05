//
//  RemoteImageCarouselView.swift
//  PremierInn
//
//  Created by Rodrigues, Seymour (Contractor) on 24/06/2026.
//  Copyright © 2026 Whitbread. All rights reserved.
//

import UIKit

// MARK: - RemoteImageCarouselView

final class RemoteImageCarouselView: UIView {
    var onTap: (() -> Void)?
    var urlImageSize: URLImageSize = .medium

    private let viewModel = RemoteImageCarouselViewModel()

    private lazy var collectionView: UICollectionView = {
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal
        layout.minimumLineSpacing = ViewConstants.Spacing.none
        layout.minimumInteritemSpacing = ViewConstants.Spacing.none

        let collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.isPagingEnabled = true
        collectionView.showsHorizontalScrollIndicator = false
        collectionView.backgroundColor = .clear
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.register(
            RemoteImageCarouselCell.self,
            forCellWithReuseIdentifier: RemoteImageCarouselCell.reuseIdentifier
        )
        return collectionView
    }()

    private let pageControlContainer: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.TintD1.withAlphaComponent(0.8)
        view.layer.cornerRadius = 8
        view.clipsToBounds = true
        return view
    }()

    private let pageControl: UIPageControl = {
        let control = UIPageControl()
        control.hidesForSinglePage = true
        control.isUserInteractionEnabled = false
        control.backgroundStyle = .minimal
        return control
    }()

    override init(frame: CGRect) {
        super.init(frame: frame)

        addSubview(collectionView)
        addSubview(pageControlContainer)
        pageControlContainer.addSubview(pageControl)

        collectionView.translatesAutoresizingMaskIntoConstraints = false
        pageControlContainer.translatesAutoresizingMaskIntoConstraints = false
        pageControl.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            collectionView.topAnchor.constraint(equalTo: topAnchor),
            collectionView.leadingAnchor.constraint(equalTo: leadingAnchor),
            collectionView.trailingAnchor.constraint(equalTo: trailingAnchor),
            collectionView.bottomAnchor.constraint(equalTo: bottomAnchor),

            pageControlContainer.centerXAnchor.constraint(equalTo: centerXAnchor),
            pageControlContainer.bottomAnchor.constraint(
                equalTo: bottomAnchor,
                constant: -ViewConstants.Spacing.small
            ),
            pageControlContainer.heightAnchor.constraint(
                equalToConstant: ViewConstants.Spacing.medium
            ),

            pageControl.topAnchor.constraint(
                equalTo: pageControlContainer.topAnchor,
                constant: ViewConstants.Spacing.xSmall
            ),
            pageControl.bottomAnchor.constraint(
                equalTo: pageControlContainer.bottomAnchor,
                constant: -ViewConstants.Spacing.xSmall
            ),
            pageControl.leadingAnchor.constraint(
                equalTo: pageControlContainer.leadingAnchor,
                constant: ViewConstants.Spacing.xSmall
            ),
            pageControl.trailingAnchor.constraint(
                equalTo: pageControlContainer.trailingAnchor,
                constant: -ViewConstants.Spacing.xSmall
            )
        ])

        let tapGesture = UITapGestureRecognizer(
            target: self,
            action: #selector(didTapCarousel)
        )
        collectionView.addGestureRecognizer(tapGesture)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func setImages(_ urls: [URL]) {
        guard viewModel.urls != urls else { return }

        viewModel.setImages(urls)

        collectionView.reloadData()
        collectionView.setContentOffset(.zero, animated: false)

        updatePageControl()
    }

    override func layoutSubviews() {
        super.layoutSubviews()

        guard let layout = collectionView.collectionViewLayout as? UICollectionViewFlowLayout else {
            return
        }

        layout.itemSize = collectionView.bounds.size
        layout.invalidateLayout()
    }
}

// MARK: - UICollectionViewDataSource, UICollectionViewDelegateFlowLayout

extension RemoteImageCarouselView: UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(
        _ collectionView: UICollectionView,
        numberOfItemsInSection section: Int
    ) -> Int {
        viewModel.urls.count
    }

    func collectionView(
        _ collectionView: UICollectionView,
        cellForItemAt indexPath: IndexPath
    ) -> UICollectionViewCell {
        guard let cell = collectionView.dequeueReusableCell(
            withReuseIdentifier: RemoteImageCarouselCell.reuseIdentifier,
            for: indexPath
        ) as? RemoteImageCarouselCell else {
            return UICollectionViewCell()
        }

        cell.configure(
            with: viewModel.urls[safe: indexPath.item],
            urlImageSize: urlImageSize
        )
        return cell
    }

    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        updateCurrentIndex(scrollView)
    }

    func scrollViewDidEndDragging(
        _ scrollView: UIScrollView,
        willDecelerate decelerate: Bool
    ) {
        if !decelerate {
            updateCurrentIndex(scrollView)
        }
    }
}

// MARK: - Helpers

private extension RemoteImageCarouselView {
    @objc func didTapCarousel() {
        onTap?()
    }

    func updatePageControl() {
        let isHidden = viewModel.isPageControlHidden

        pageControl.numberOfPages = viewModel.pageControlNumberOfPages
        pageControl.currentPage = viewModel.activePageControlIndex
        pageControl.isHidden = isHidden
        pageControlContainer.isHidden = isHidden
    }

    func updateCurrentIndex(_ scrollView: UIScrollView) {
        let oldIndex = viewModel.currentIndex

        viewModel.updateCurrentIndex(
            contentOffsetX: scrollView.contentOffset.x,
            pageWidth: scrollView.bounds.width
        )

        guard viewModel.currentIndex != oldIndex else { return }

        updatePageControl()
    }
}
