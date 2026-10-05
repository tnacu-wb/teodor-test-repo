package uk.co.whitbread.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingAlerts {
    private PriceCapLocations rateCaps;
    private List<String> bookingAlertHotels;
    private List<String> recipientEmailAddresses;
    private boolean dayOfArrival;
    private boolean weekendArrival;
    private boolean passThroughWeekend;
    private BookingAlertsFrequency frequency;
}

