package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Data
@Schema
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingPreference {

    @Valid
    private RoomCriteria roomRequirements;

    private BookingType reason;
    private long foodPreference;
    private boolean wantSmsConfirmations;

    public boolean toBusinessUse() {
        return reason != null && !StringUtils.equals("LEISURE", Objects.toString(reason));
    }
}