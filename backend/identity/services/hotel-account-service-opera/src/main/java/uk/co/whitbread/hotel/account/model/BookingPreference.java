package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private RoomCriteria roomRequirements;

    private BookingType reason;

    private Long foodPreference;

    private Boolean wantSmsConfirmations;

    private Boolean preselectWifi;

}
