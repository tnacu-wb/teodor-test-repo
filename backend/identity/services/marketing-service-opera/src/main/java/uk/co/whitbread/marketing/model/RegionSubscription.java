package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegionSubscription {

    @NotNull
    private String regionId;
    @NotNull
    private Boolean subscribed;

}
