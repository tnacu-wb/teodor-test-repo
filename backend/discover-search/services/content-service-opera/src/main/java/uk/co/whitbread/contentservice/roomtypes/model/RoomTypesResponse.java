package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoomTypesResponse implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private List<RoomType> roomTypes;
}
