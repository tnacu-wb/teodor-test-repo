package uk.co.whitbread.hotel.account.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaysTypesTotals {
    private int cancelled;
    private int upcoming;
    private int past;
    private int checkedIn;
}
