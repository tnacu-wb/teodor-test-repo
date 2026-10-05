package com.whitbread.premierinn.alternativeroomselection

data class RoomTypeChoices(val roomId: Int,
                           val roomHeading: String,
                           val occupantsSubheading: String,
                           val firstTwinRoomOption: TwinRoomOption,
                           val secondTwinRoomOption: TwinRoomOption?)