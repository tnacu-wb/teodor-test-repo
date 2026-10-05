package uk.co.whitbread.contentservice.roomtypes.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AEMBookingNotifications implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private BookingInfoMessage bookingInfoMessage;

}
