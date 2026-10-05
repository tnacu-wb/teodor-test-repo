package uk.co.whitbread.marketing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MarketingSubscriptionInfoRequest {

    private String emailAddress;

    private Boolean allUserInfo;

}
