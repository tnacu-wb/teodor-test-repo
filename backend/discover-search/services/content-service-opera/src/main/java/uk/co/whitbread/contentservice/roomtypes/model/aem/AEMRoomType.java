package uk.co.whitbread.contentservice.roomtypes.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class AEMRoomType implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private RoomTypeCode roomTypeCode;
    private RoomCategory roomCategory;
    private RoomLabel roomLabel;
    private RoomDescription roomDescription;
    private RoomInfoLabel roomInfoLabel;
    private RoomInfo roomInfo;
    private GridImage gridImage;
    private RoomImage roomImage;
    private Facilities facilities;

}
