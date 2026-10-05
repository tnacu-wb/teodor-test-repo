package com.whitbread.premierinn.hoteldetails;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.domain.common.RoomType;

/**
 * Helper object used for RoomSubstitutionInfo presentation only
 */
@AutoValue
public abstract class RoomSubstitutionInfo {

    public abstract int getRoomNumberAffected();

    public abstract RoomType getFromType();

    public abstract RoomType getToType();

    public static RoomSubstitutionInfo create(int roomNumber, RoomType fromType, RoomType toType) {
        return new AutoValue_RoomSubstitutionInfo(roomNumber, fromType, toType);
    }
}
