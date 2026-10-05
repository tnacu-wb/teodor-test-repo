//
//  PhotoPageViewController.swift
//  PremierInn
//
//  Created by Freddie Parks on 03/08/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit

protocol PhotoPageViewControllerDelegate: AnyObject {
    func photoPageViewControllerCurrentPhotoDidChange(currentIndex: Int)
}

class PhotoPageViewController: UIPageViewController {
    weak var carouselDelegate: PhotoPageViewControllerDelegate?

    var controllers: [UIViewController]
    var currentIndex: Int {
        guard let currentController = viewControllers?.first else { return 0 }
        guard let index = controllers.firstIndex(of: currentController) else { return 0 }

        return index
    }

    private var startIndex = 0

    private static let imageSpacing: CGFloat = 10

    init(controllers: [UIViewController], startIndex: Int) {
        self.controllers = controllers
        self.startIndex = startIndex

        super.init(
            transitionStyle: .scroll,
            navigationOrientation: .horizontal,
            options: convertToOptionalUIPageViewControllerOptionsKeyDictionary(
                [convertFromUIPageViewControllerOptionsKey(UIPageViewController
            .OptionsKey.interPageSpacing): PhotoPageViewController.imageSpacing]
            )
        )

        self.isDoubleSided = false
        self.dataSource = self
        self.delegate = self
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        setCurrentPageIndex(startIndex)
    }

    func setCurrentPageIndex(_ index: Int) {
        guard index < controllers.count else { return }

        let controller = controllers[index]

        setViewControllers([controller], direction: .forward, animated: false)
    }

    func followingController(
        _ currentController: UIViewController,
        direction: UIPageViewController.NavigationDirection
    ) -> UIViewController? {
        guard controllers.count > 1 else { return nil }

        switch direction {
        case .forward:
            if currentController == controllers.last {
                return controllers.first
            }
        case .reverse:
            if currentController == controllers.first {
                return controllers.last
            }
        @unknown default:
            return nil
        }

        let directionValue = direction == .forward ? 1 : -1

        if let index = controllers.firstIndex(of: currentController), index + directionValue < controllers.count {
            return controllers[index + directionValue]
        }

        return nil
    }
}

extension PhotoPageViewController: UIPageViewControllerDelegate, UIPageViewControllerDataSource {
    func pageViewController(
        _ pageViewController: UIPageViewController,
        viewControllerAfter viewController: UIViewController
    ) -> UIViewController? {
        followingController(viewController, direction: .forward)
    }

    func pageViewController(
        _ pageViewController: UIPageViewController,
        viewControllerBefore viewController: UIViewController
    ) -> UIViewController? {
        followingController(viewController, direction: .reverse)
    }

    func pageViewController(
        _ pageViewController: UIPageViewController,
        didFinishAnimating finished: Bool,
        previousViewControllers: [UIViewController],
        transitionCompleted completed: Bool
    ) {
        carouselDelegate?.photoPageViewControllerCurrentPhotoDidChange(currentIndex: currentIndex)
    }
}

// Helper function inserted by Swift 4.2 migrator.
private func convertToOptionalUIPageViewControllerOptionsKeyDictionary(_ input: [String: Any]?)
    -> [UIPageViewController.OptionsKey: Any]? {
	guard let input = input else { return nil }
	return Dictionary(uniqueKeysWithValues: input.map { key, value in
	    (UIPageViewController.OptionsKey(rawValue: key), value)})
}

// Helper function inserted by Swift 4.2 migrator.
private func convertFromUIPageViewControllerOptionsKey(_ input: UIPageViewController.OptionsKey) -> String {
	input.rawValue
}
