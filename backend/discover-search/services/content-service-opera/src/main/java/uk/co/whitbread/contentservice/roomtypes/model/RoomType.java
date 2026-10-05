package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
public class RoomType implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private String roomTypeCode;
    private String roomCategory;
    private String roomLabel;
    private String roomDescription;
    private String roomInfoLabel;
    private String roomInfo;
    private String gridImage;
    private String roomImage;
    private List<Facility> facilities;

}
