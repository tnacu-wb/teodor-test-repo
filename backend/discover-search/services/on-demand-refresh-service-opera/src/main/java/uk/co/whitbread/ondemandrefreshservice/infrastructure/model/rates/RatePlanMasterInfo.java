package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatePlanMasterInfo {

    private PrimaryDetails primaryDetails;
    private List<RoomType> roomTypeList;
    private List<RatePlanBasedOnRates> ratePlanBasedOnRates;
    private String hotelId;
    private String ratePlanCode;
    private boolean bARRate;
    private boolean tiered;
    private boolean daily;
    private String currencyCode;
    private boolean complimentary;
    private boolean houseUse;
    private boolean advancedDailyBase;
}
