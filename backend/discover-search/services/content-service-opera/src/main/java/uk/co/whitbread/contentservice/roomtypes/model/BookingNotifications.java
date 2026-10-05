package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class BookingNotifications implements Serializable {
    private static final long serialVersionUID = 3973228892139419589L;
    private String bookingInfoMessage;
}
