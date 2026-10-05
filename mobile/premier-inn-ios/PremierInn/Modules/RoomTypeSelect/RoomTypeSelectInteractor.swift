//
//  RoomTypeSelectInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 22/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork

protocol SelectableRoomType {
    var name: String { get }
    var description: String { get }
    var iconName: String { get }
}

extension RoomType: SelectableRoomType {}
extension AccessibleRoomType: SelectableRoomType {
    var name: String {
        PILocalizedString("accessible\(self)Title")
    }

    var iconName: String {
        PILocalizedString("accessible\(self)Room")
    }

    var description: String {
        PILocalizedString("accessible\(self)Description")
    }
}

private struct ViewModel: RoomTypeSelectViewModel {
    let roomTypeOptions: [RoomTypeSelectOptionViewModel]
}

struct OptionViewModel: RoomTypeSelectOptionViewModel {
    let name: String
    let description: String
    let iconName: String
    let enabled: Bool
    let selected: Bool
}

// MARK: Inaccessible Room Types Interactor

class RoomTypeSelectInteractor {
    private let roomTypes: [SelectableRoomType] = [
        RoomType.single,
        RoomType.double,
        RoomType.twin,
        RoomType.family,
        RoomType.accessible
    ]
    private let availableRoomTypes: [SelectableRoomType]

    private var preselectedRoomType: SelectableRoomType

    init(with availableRoomTypes: [SelectableRoomType], selectedRoomType: SelectableRoomType) {
        self.availableRoomTypes = availableRoomTypes
        self.preselectedRoomType = selectedRoomType
    }
}

extension RoomTypeSelectInteractor: RoomTypeSelectInteractorProtocol {
    var viewModel: RoomTypeSelectViewModel {
        let roomTypesToDisplay = roomTypes.filter { availableRoomTypes.compactMap { $0.name }.contains($0.name) }
        return ViewModel(
            roomTypeOptions: roomTypesToDisplay.map {
                OptionViewModel(
                    name: $0.name,
                    description: $0.description,
                    iconName: $0.iconName,
                    enabled: availableRoomTypes.compactMap { $0.name }.contains($0.name),
                    selected: $0.name == preselectedRoomType.name
                )
            }
        )
    }

    var selectedRoomType: SelectableRoomType? {
        preselectedRoomType
    }

    func select(roomType: RoomTypeSelectOptionViewModel) {
        guard let selectedRoomType = availableRoomTypes.first(where: { roomType.name == $0.name }) else { return }
        preselectedRoomType = selectedRoomType
    }
}

// MARK: Accessible Room Types Interactor

class AccessibleRoomTypeSelectInteractor {
    private let availableRoomTypes: [SelectableRoomType]

    private var preselectedRoomType: SelectableRoomType

    init(with availableRoomTypes: [SelectableRoomType], selectedRoomType: SelectableRoomType) {
        self.availableRoomTypes = availableRoomTypes
        self.preselectedRoomType = selectedRoomType
    }
}

extension AccessibleRoomTypeSelectInteractor: RoomTypeSelectInteractorProtocol {
    var viewModel: RoomTypeSelectViewModel {
        ViewModel(
            roomTypeOptions: availableRoomTypes.map {
                OptionViewModel(
                    name: $0.name,
                    description: $0.description,
                    iconName: $0.iconName,
                    enabled: true,
                    selected: $0.name == preselectedRoomType.name
                )
            }
        )
    }

    var selectedRoomType: SelectableRoomType? {
        preselectedRoomType
    }

    func select(roomType: RoomTypeSelectOptionViewModel) {
        guard let selectedRoomType = availableRoomTypes.first(where: { roomType.name == $0.name }) else { return }
        preselectedRoomType = selectedRoomType
    }
}
