package com.whitbread.premierinn.criteria.model

import com.whitbread.premierinn.api.response.customer.RoomRequirements
import com.whitbread.premierinn.criteria.NumberSelectorMaxNumberInfo
import com.whitbread.premierinn.criteria.roomselector.Room
import com.whitbread.premierinn.criteria.view.CotState
import com.whitbread.premierinn.criteria.view.NumberSelectorViewInput
import com.whitbread.premierinn.domain.common.RoomType

object RoomConfigurationCreator {

    fun createNoError(roomConfig: RoomConfiguration, forPreferences: Boolean): RoomConfiguration {
        return RoomRules.apply(RoomConfiguration.builder()
                .number(roomConfig.number())
                .adults(NumberSelectorViewInput.createNoError(roomConfig.adults()))
                .children(NumberSelectorViewInput.createNoError(roomConfig.children()))
                .infants(NumberSelectorViewInput.createNoError(roomConfig.infants()))
                .roomType(roomConfig.roomType())
                .cot(roomConfig.cot())
                .autobuild(),
                forPreferences)
    }

    fun create(number: Int, forPreferences: Boolean): RoomConfiguration {
        return RoomRules.apply(RoomConfiguration.builder()
                .number(number)
                .adults(NumberSelectorViewInput.createDefault(RoomConfiguration.MIN_NUMBER_OF_ADULTS_IN_ROOM))
                .children(NumberSelectorViewInput.createDefault(RoomConfiguration.MIN_NUMBER_OF_CHILDREN_IN_ROOM))
                .infants(NumberSelectorViewInput.createDefault(RoomConfiguration.MIN_NUMBER_OF_INFANTS_IN_ROOM))
                .roomType(RoomConfiguration.DEFAULT_ROOM_TYPE)
                .cot(CotState.NOT_SELECTED)
                .autobuild(),
                forPreferences)
    }

    fun createFromRoomRequirements(roomRequirements: RoomRequirements, forPreferences: Boolean): RoomConfiguration {
        val adultNumberSelector = NumberSelectorViewInput.builder()
                .value(roomRequirements.adults())
                .minusEnabled(roomRequirements.adults() > RoomConfiguration.MIN_NUMBER_OF_ADULTS_IN_ROOM)
                .plusEnabled(roomRequirements.adults() < RoomConfiguration.MAX_NUMBER_OF_ADULTS_IN_ROOM).build()

        val childNumberSelector = NumberSelectorViewInput.builder()
                .value(roomRequirements.children())
                .minusEnabled(roomRequirements.children() > RoomConfiguration.MIN_NUMBER_OF_CHILDREN_IN_ROOM)
                .plusEnabled(roomRequirements.children() < RoomConfiguration.MAX_NUMBER_OF_CHILDREN_IN_ROOM).build()

        val infants = if (roomRequirements.cotRequired()) 1 else 0
        val infantNumberSelector = NumberSelectorViewInput.builder()
                .value(infants)
                .minusEnabled(infants > RoomConfiguration.MIN_NUMBER_OF_INFANTS_IN_ROOM)
                .plusEnabled(infants < RoomConfiguration.MAX_NUMBER_OF_INFANTS_IN_ROOM).build()

        val roomType = if (roomRequirements.type() == null) RoomConfiguration.DEFAULT_ROOM_TYPE else Room.typeLookUp(roomRequirements.type())
        return RoomRules.apply(RoomConfiguration.builder()
                .number(1)
                .adults(adultNumberSelector)
                .children(childNumberSelector)
                .infants(infantNumberSelector)
                .roomType(roomType)
                .cot(if (roomRequirements.cotRequired()) CotState.SELECTED else CotState.NOT_SELECTED)
                .autobuild(),
                forPreferences)
    }

    fun createFromAdultChange(newValue: Int, currentRoomConf: RoomConfiguration,
                              maxNumberInfo: NumberSelectorMaxNumberInfo?,
                              forPreferences: Boolean): RoomConfiguration {
        val numberSelectorViewInput = RoomConfiguration.getAdultNumberSelectorViewInput(newValue, maxNumberInfo)
        return RoomRules.apply(currentRoomConf.toBuilder()
                .number(1)
                .adults(numberSelectorViewInput)
                .autobuild(),
                forPreferences)
    }

    fun createFromChildrenChange(newValue: Int, currentRoomConf: RoomConfiguration,
                                 maxNumberInfo: NumberSelectorMaxNumberInfo?,
                                 forPreferences: Boolean): RoomConfiguration {
        val numberSelectorViewInput = RoomConfiguration.getChildrenNumberSelectorViewInput(newValue, maxNumberInfo)
        return RoomRules.apply(currentRoomConf.toBuilder()
                .number(1)
                .children(numberSelectorViewInput)
                .autobuild(),
                forPreferences)
    }

    fun createFromCotChange(checked: Boolean, currentRoomConf: RoomConfiguration, forPreferences: Boolean): RoomConfiguration {
        return RoomRules.apply(currentRoomConf.toBuilder()
                .number(1)
                .cot(if (checked) CotState.SELECTED else CotState.NOT_SELECTED)
                .autobuild(),
                forPreferences)
    }

    fun createFromRoomTypeChange(roomType: RoomType, currentRoomConf: RoomConfiguration, forPreferences: Boolean): RoomConfiguration {
        return RoomRules.apply(currentRoomConf.toBuilder()
                .number(1)
                .roomType(roomType)
                .autobuild(),
                forPreferences)
    }

    fun createDefault(forPreferences: Boolean): RoomConfiguration {
        return create(RoomConfiguration.DEFAULT_ROOM_NUMBER, forPreferences)
    }
}