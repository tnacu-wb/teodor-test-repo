package com.whitbread.premierinn.domain.common

import com.whitbread.premierinn.domain.bathroomselection.entity.BathroomType
import com.whitbread.premierinn.domain.twin.TwinRoomType

const val DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX = "AB"
const val DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX = "IB"
const val DISABLED_WET_ROOM_LETTING_CODE_PREFIX = "AW"
const val DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX = "IW"

const val TRUE_TWIN_ROOM_LETTING_CODE = "WB"
const val TRUE_TWIN_DOUBLE_ZIP_LINK_ROOM_LETTING_CODE = "ZBT"
const val TRUE_TWIN_DOUBLE_ROOM_LETTING_CODE = "XB"
const val PREMIER_INN_TWIN_TRIPLE_ROOM_LETTING_CODE = "TBT"
const val PREMIER_INN_TWIN_QUAD_ROOM_LETTING_CODE = "QBT"

const val TWIN_ROOM_TWO_SINGLE_BEDS_OPERA = "TW2S"
const val TWIN_ROOM_DOUBLE_AND_SOFA_OPERA = "TWDS"

private val WET_ROOM_LETTING_PREFIXES = setOf(DISABLED_WET_ROOM_LETTING_CODE_PREFIX, DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX)
private val LOWERED_BATH_LETTING_PREFIXES = setOf(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX, DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX)

private val TRUE_TWIN_PREFIXES = setOf(TRUE_TWIN_ROOM_LETTING_CODE, TRUE_TWIN_DOUBLE_ZIP_LINK_ROOM_LETTING_CODE,
        TRUE_TWIN_DOUBLE_ROOM_LETTING_CODE)
private val PREMIER_INN_TWIN_PREFIXES = setOf(PREMIER_INN_TWIN_TRIPLE_ROOM_LETTING_CODE, PREMIER_INN_TWIN_QUAD_ROOM_LETTING_CODE)

// Letting Types: https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/77398090/Room+Types+Letting+Types+and+Substitution+rules
data class LettingType(val code: String) {

    val roomTypeCode = RoomTypeCode(code.substring(0, 2)) // The first two characters of a lettingcode refer to the room type
    val twinTypeCode = TwinTypeCode(code)
    val bathroomType = code.toBathroomType()
    val isAccessible = roomTypeCode.isAccessible()
    val isBusiness = roomTypeCode.isBusiness()
    val isBiggerRoom = roomTypeCode.isBiggerRoom()
    val isPremiumRoom = roomTypeCode.isPremiumRoom()
    val isTrueTwinRoom = twinTypeCode.isTrueTwin()
    val isPremierInnNewTwinRoom = twinTypeCode.isPremierInnNewTwin()

    fun isTrueTwin(): Boolean {
        return TRUE_TWIN_PREFIXES.any { prefix -> code.equals(prefix, ignoreCase = true) }
    }

    fun isPremierInnNewTwin(): Boolean {
        return PREMIER_INN_TWIN_PREFIXES.any { prefix -> code.equals(prefix, ignoreCase = true) }
    }

    private fun String.toBathroomType(): BathroomType? {
        return when(this) {
            LOWDBL, LOWTWN, EXDLOW, PPDLOW -> BathroomType.LOWERED_BATH
            WETDBL, WETTWN, ACCSGL, ACCWIN, ACCNWD, EXDWET, PPDWET, BRFDBL -> BathroomType.WET_ROOM
            else -> BathroomType.WET_ROOM
        }
    }

    val twinRoomType: TwinRoomType?
        get() = when {
            isTrueTwin() -> TwinRoomType.TRUE_TWIN
            isPremierInnNewTwin() -> TwinRoomType.PREMIER_INN_TWIN
            else -> null
        }
}

data class TwinTypeCode(val code: String) {
    val twinRoomType: TwinRoomType?
        get() = when {
            isTrueTwin() -> TwinRoomType.TRUE_TWIN
            isPremierInnNewTwin() -> TwinRoomType.PREMIER_INN_TWIN
            else -> null
        }

    fun isTrueTwin(): Boolean {
        return TRUE_TWIN_PREFIXES.any { prefix -> code.equals(prefix, ignoreCase = true) }
    }

    fun isPremierInnNewTwin(): Boolean {
        return PREMIER_INN_TWIN_PREFIXES.any { prefix -> code.equals(prefix, ignoreCase = true) }
    }
}

data class RoomTypeCode(val roomTypeCode: String) {

    val bathroomType: BathroomType?
        get() = when {
            isWetRoom() -> BathroomType.WET_ROOM
            isLoweredBath() -> BathroomType.LOWERED_BATH
            else -> null
        }

    fun isBiggerRoom(): Boolean = roomTypeCode.equals("GB", ignoreCase = true) || roomTypeCode.equals("GN", ignoreCase = true)

    fun isPremiumRoom(): Boolean = roomTypeCode.equals("RB", ignoreCase = true)

    fun isBusiness(): Boolean = roomTypeCode.equals("PB", ignoreCase = true)

    fun isAccessible(): Boolean = roomTypeCode.startsWith("BB", true)
            || roomTypeCode.equals("BN", true)
            || roomTypeCode.equals("HB", true)
            || roomTypeCode.equals(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX, true)
            || roomTypeCode.equals(DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX, true)
            || roomTypeCode.equals(DISABLED_WET_ROOM_LETTING_CODE_PREFIX, true)
            || roomTypeCode.equals(DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX, true)

    private fun isWetRoom(): Boolean {
        return WET_ROOM_LETTING_PREFIXES.any { prefix -> roomTypeCode.equals(prefix, ignoreCase = true) }
    }

    private fun isLoweredBath(): Boolean {
        return LOWERED_BATH_LETTING_PREFIXES.any { prefix -> roomTypeCode.equals(prefix, ignoreCase = true) }
    }
}


fun String?.toLettingTypeBart(): String {
    return when (this) {
        TWIN_ROOM_TWO_SINGLE_BEDS_OPERA -> TRUE_TWIN_ROOM_LETTING_CODE
        TWIN_ROOM_DOUBLE_AND_SOFA_OPERA -> PREMIER_INN_TWIN_TRIPLE_ROOM_LETTING_CODE
        else -> EMPTY_STRING_DOMAIN

    }
}
