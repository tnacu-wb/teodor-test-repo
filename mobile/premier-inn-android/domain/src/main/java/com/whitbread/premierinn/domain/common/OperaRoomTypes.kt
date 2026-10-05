package com.whitbread.premierinn.domain.common

const val SINGLE_KEY_GQL = "SB"
const val TWIN_KEY_GQL = "TWIN"
const val FAMILY_KEY_GQL = "FAM"
const val ACCESSIBLE_KEY_GQL = "DIS"
const val DB_KEY_GQL = "DB"

const val PPLDBL = "PPLDBL"
const val DOUBLE = "DOUBLE"
const val FMQUAD = "FMQUAD"
const val FMTRPL = "FMTRPL"
const val FMTHRE = "FMTHRE"
const val LOWDBL = "LOWDBL"
const val WETDBL = "WETDBL"
const val ZPLDBL = "ZPLDBL"
const val FMFOUR = "FMFOUR"
const val TWINRM = "TWINRM"
const val WETTWN = "WETTWN"
const val LOWTWN = "LOWTWN"
const val EXDLOW = "EXDLOW"
const val PPDLOW = "PPDLOW"
const val ACCSGL = "ACCSGL"
const val ACCWIN = "ACCWIN"
const val ACCNWD = "ACCNWD"
const val EXDWET = "EXDWET"
const val PPDWET = "PPDWET"
const val BRFDBL = "BRFDBL"
const val DBLWIN = "DBLWIN"
const val BIGWIN = "BIGWIN"
const val BIGNWD = "BIGNWD"
const val DBLNWD = "DBLNWD"

fun String.toRoomTypeGQL():RoomType {
    return when (this) {
        SINGLE_KEY_GQL -> RoomType.SINGLE
        DB_KEY_GQL -> RoomType.DOUBLE
        TWIN_KEY_GQL -> RoomType.TWIN
        ACCESSIBLE_KEY_GQL -> RoomType.ACCESSIBLE
        FAMILY_KEY_GQL -> RoomType.FAMILY
        // These are added for amend as bookingconfirmation returns it in these codes
        PPLDBL -> RoomType.DOUBLE
        DOUBLE -> RoomType.DOUBLE
        FMQUAD -> RoomType.FAMILY
        FMTRPL -> RoomType.FAMILY
        FMTHRE -> RoomType.FAMILY
        LOWDBL -> RoomType.ACCESSIBLE
        WETDBL -> RoomType.ACCESSIBLE
        ZPLDBL -> RoomType.DOUBLE
        FMFOUR -> RoomType.FAMILY
        TWINRM -> RoomType.TWIN
        WETTWN -> RoomType.TWIN
        LOWTWN -> RoomType.ACCESSIBLE
        ACCWIN -> RoomType.ACCESSIBLE
        DBLWIN -> RoomType.DOUBLE
        BIGWIN -> RoomType.DOUBLE
        BIGNWD -> RoomType.DOUBLE
        DBLNWD -> RoomType.DOUBLE
        else -> RoomType.DOUBLE
    }
}

fun RoomType.toRoomStringGQL(): String {
    return when (this) {
        RoomType.SINGLE -> SINGLE_KEY_GQL
        RoomType.DOUBLE -> DB_KEY_GQL
        RoomType.TWIN  -> TWIN_KEY_GQL
        RoomType.ACCESSIBLE -> ACCESSIBLE_KEY_GQL
        RoomType.FAMILY -> FAMILY_KEY_GQL
        else -> EMPTY_STRING_DOMAIN
    }
}

//Opera
fun String.roomTypeCode() : RoomTypeCode {
    return when(this) {
        // LOWDBL -  Accessible Double Lower bath
        // WETDBL - Accessible Double Wet room
        // WETTWN - Accessible Twin Wet room
        // LOWTWN-  Accessible Twin Lower Bath
        // ACCWIN - Hub/Zip Accessible

        LOWDBL -> RoomTypeCode(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX)
        WETDBL -> RoomTypeCode(DISABLED_WET_ROOM_LETTING_CODE_PREFIX)
        WETTWN -> RoomTypeCode(DISABLED_WET_ROOM_LETTING_CODE_TWIN_PREFIX)
        LOWTWN -> RoomTypeCode(DISABLED_LOWERED_BATH_TWIN_LETTING_CODE_PREFIX)
//            ACCWIN -> RoomTypeCode(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX) // Not supported yet
        else -> RoomTypeCode(DISABLED_LOWERED_BATH_LETTING_CODE_PREFIX)
    }
}
