package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserDetailsResponse {
    protected TetheredUserDetailsOverview tetheredUserOverview;
    protected TetheredUserAccountOverview customerAccountOverview;
}
