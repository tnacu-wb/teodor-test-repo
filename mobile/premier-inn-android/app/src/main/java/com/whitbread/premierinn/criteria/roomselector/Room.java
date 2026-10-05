package com.whitbread.premierinn.criteria.roomselector;

import android.content.Context;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.domain.common.RoomType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public final class Room {

    private static final Map<String, RoomType> ID_TO_TYPE_MAP;
    private static final Map<RoomType, State> DEFAULT_ROOM_STATES_UNMODIFIABLE;
    private static final Map<RoomType, State> FAMILY_ROOM_STATES_UNMODIFIABLE;

    static {
        ID_TO_TYPE_MAP = new HashMap<>();
        for (RoomType type : EnumSet.allOf(RoomType.class)) {
            ID_TO_TYPE_MAP.put(type.getCode(), type);
        }
        DEFAULT_ROOM_STATES_UNMODIFIABLE = Collections.unmodifiableMap(createDefaultRoomStates());
        FAMILY_ROOM_STATES_UNMODIFIABLE = Collections.unmodifiableMap(createFamilyRoomStates());
    }

    public static RoomType typeLookUp(final String typeCode) {
        return ID_TO_TYPE_MAP.get(typeCode);
    }

    public enum State {
        NOT_AVAILABLE,
        AVAILABLE,
        SELECTED
    }

    public static Map<RoomType, State> getDefaultRoomStates() {
        // Creating new map to make it modifiable
        return new HashMap<>(DEFAULT_ROOM_STATES_UNMODIFIABLE);
    }

    public static Map<RoomType, State> getFamilyRoomStates() {
        // Creating new map to make it modifiable
        return new HashMap<>(FAMILY_ROOM_STATES_UNMODIFIABLE);
    }

    private static Map<RoomType, State> createDefaultRoomStates() {
        HashMap<RoomType, State> list = new HashMap<>();
        list.put(RoomType.DOUBLE, State.AVAILABLE);
        list.put(RoomType.SINGLE, State.AVAILABLE);
        list.put(RoomType.TWIN, State.NOT_AVAILABLE);
        list.put(RoomType.FAMILY, State.NOT_AVAILABLE);
        list.put(RoomType.ACCESSIBLE, State.AVAILABLE);
        return list;
    }

    private static Map<RoomType, State> createFamilyRoomStates() {
        HashMap<RoomType, State> hashMap = new HashMap<>();
        hashMap.put(RoomType.DOUBLE, State.NOT_AVAILABLE);
        hashMap.put(RoomType.SINGLE, State.NOT_AVAILABLE);
        hashMap.put(RoomType.TWIN, State.NOT_AVAILABLE);
        hashMap.put(RoomType.FAMILY, State.AVAILABLE);
        hashMap.put(RoomType.ACCESSIBLE, State.NOT_AVAILABLE);
        return hashMap;
    }

    public static String getRoomTypeLabel(@NonNull Context context, @NonNull RoomType type) {
        switch (type) {
            case DOUBLE:
                return context.getString(R.string.criteria_room_type_double);
            case TWIN:
                return context.getString(R.string.criteria_room_type_twin);
            case SINGLE:
                return context.getString(R.string.criteria_room_type_single);
            case FAMILY:
                return context.getString(R.string.criteria_room_type_family);
            case ACCESSIBLE:
                return context.getString(R.string.criteria_room_type_accessible);
            default:
                return context.getString(R.string.criteria_room_type_double);
        }
    }

    public static String getRoomNameLabel(@NonNull Context context, int roomOrder) {
        return context.getString(R.string.hotel_details_room_name_label, roomOrder);
    }

    public static String getRoomTypeLabelLowerCase(@NonNull Context context, @NonNull RoomType type) {
        return getRoomTypeLabel(context, type).toLowerCase();
    }

    public static Map<RoomType, Room.State> createRoomAvailabilityList(int adults, int children, boolean isCotSelected,
                                                                       boolean forPreferences) {
        Map<RoomType, Room.State> roomTypes;
        if (children > 0) {
            roomTypes = Room.getFamilyRoomStates();
        } else {
            roomTypes = Room.getDefaultRoomStates();

            if (isCotSelected) {
                roomTypes.put(RoomType.SINGLE, Room.State.NOT_AVAILABLE);
                if (forPreferences) {
                    roomTypes.put(RoomType.ACCESSIBLE, Room.State.NOT_AVAILABLE);
                }
            }
            if (adults > 1) {
                roomTypes.put(RoomType.SINGLE, Room.State.NOT_AVAILABLE);
            }
            if (adults == 2) {
                roomTypes.put(RoomType.TWIN, Room.State.AVAILABLE);
            }
        }
        return roomTypes;
    }

    public static RoomType replaceUnavailableRoom(int childrenStaying) {
        if (childrenStaying > 0) {
            return RoomType.FAMILY;
        } else {
            return RoomType.DOUBLE;
        }
    }
}
